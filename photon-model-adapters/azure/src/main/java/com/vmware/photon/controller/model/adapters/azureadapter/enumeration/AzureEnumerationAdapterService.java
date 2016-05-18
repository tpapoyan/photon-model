/*
 * Copyright (c) 2015-2016 VMware, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.vmware.photon.controller.model.adapters.azureadapter.enumeration;

import static com.vmware.photon.controller.model.adapters.azureadapter.AzureConstants.AZURE_INSTANCE_ID;
import static com.vmware.photon.controller.model.adapters.azureadapter.AzureConstants.AZURE_OSDISK_CACHING;
import static com.vmware.photon.controller.model.adapters.azureadapter.AzureConstants.AZURE_STORAGE_ACCOUNT_NAME;
import static com.vmware.photon.controller.model.adapters.azureadapter.AzureConstants.AZURE_VM_SIZE;
import static com.vmware.photon.controller.model.adapters.azureadapter.AzureUtils.awaitTermination;
import static com.vmware.photon.controller.model.adapters.azureadapter.AzureUtils.cleanUpHttpClient;
import static com.vmware.photon.controller.model.adapters.azureadapter.AzureUtils.getAzureConfig;
import static com.vmware.photon.controller.model.resources.ComputeDescriptionService.ComputeDescription.ENVIRONMENT_NAME_AZURE;
import static com.vmware.photon.controller.model.tasks.ResourceAllocationTaskService.CUSTOM_DISPLAY_NAME;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.azure.ListOperationCallback;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.compute.ComputeManagementClient;
import com.microsoft.azure.management.compute.ComputeManagementClientImpl;
import com.microsoft.azure.management.compute.models.ImageReference;
import com.microsoft.azure.management.compute.models.VirtualMachine;
import com.microsoft.rest.ServiceResponse;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import com.vmware.photon.controller.model.adapterapi.ComputeEnumerateResourceRequest;
import com.vmware.photon.controller.model.adapterapi.EnumerationAction;
import com.vmware.photon.controller.model.adapters.azureadapter.AzureConstants;
import com.vmware.photon.controller.model.adapters.azureadapter.AzureUriPaths;
import com.vmware.photon.controller.model.adapters.util.AdapterUtils;
import com.vmware.photon.controller.model.adapters.util.enums.EnumerationStages;
import com.vmware.photon.controller.model.resources.ComputeDescriptionService;
import com.vmware.photon.controller.model.resources.ComputeDescriptionService.ComputeDescription;
import com.vmware.photon.controller.model.resources.ComputeService;
import com.vmware.photon.controller.model.resources.ComputeService.ComputeState;
import com.vmware.photon.controller.model.resources.DiskService;
import com.vmware.photon.controller.model.resources.DiskService.DiskState;
import com.vmware.photon.controller.model.resources.DiskService.DiskType;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.OperationContext;
import com.vmware.xenon.common.OperationJoin;
import com.vmware.xenon.common.OperationSequence;
import com.vmware.xenon.common.StatelessService;
import com.vmware.xenon.common.UriUtils;
import com.vmware.xenon.common.Utils;
import com.vmware.xenon.services.common.AuthCredentialsService;
import com.vmware.xenon.services.common.AuthCredentialsService.AuthCredentialsServiceState;
import com.vmware.xenon.services.common.QueryTask;
import com.vmware.xenon.services.common.QueryTask.NumericRange;
import com.vmware.xenon.services.common.QueryTask.Query;
import com.vmware.xenon.services.common.QueryTask.Query.Occurance;
import com.vmware.xenon.services.common.QueryTask.QuerySpecification.QueryOption;
import com.vmware.xenon.services.common.ServiceUriPaths;

/**
 * Enumeration adapter for data collection of VMs on Azure.
 */
public class AzureEnumerationAdapterService extends StatelessService {

    public static final String SELF_LINK = AzureUriPaths.AZURE_ENUMERATION_ADAPTER;
    private static final Pattern STORAGE_ACCOUNT_NAME_PATTERN = Pattern.compile("https?://([^.]*)");
    public static final List<String> AZURE_VM_TERMINATION_STATES = Arrays
            .asList("Deleting", "Deleted");

    /**
     * Substages to handle Azure VM data collection.
     */
    private enum EnumerationSubStages {
        LISTVMS, QUERY, UPDATE, CREATE, DELETE, FINISHED
    }

    /**
     * The enumeration service context that holds all the information needed to determine the list
     * of instances that need to be represented in the system.
     */
    private static class EnumerationContext {
        ComputeEnumerateResourceRequest enumRequest;
        ComputeDescription computeHostDesc;
        EnumerationStages stage;
        Throwable error;
        AuthCredentialsServiceState parentAuth;
        long enumerationStartTimeInMicros;

        // Substage specific fields
        EnumerationSubStages subStage;
        Map<String, VirtualMachine> virtualMachines = new ConcurrentHashMap<>();
        Map<String, ComputeState> computeStates = new ConcurrentHashMap<>();
        List<String> vmIds = new ArrayList<>();

        // Azure specific fields
        ApplicationTokenCredentials credentials;
        OkHttpClient.Builder clientBuilder;
        OkHttpClient httpClient;

        EnumerationContext(ComputeEnumerateResourceRequest request) {
            enumRequest = request;
            stage = EnumerationStages.HOSTDESC;
        }
    }

    private ExecutorService executorService;
    private Set<String> ongoingEnumerations = new ConcurrentSkipListSet<>();

    @Override
    public void handleStart(Operation startPost) {
        executorService = getHost().allocateExecutor(this);

        super.handleStart(startPost);
    }

    @Override
    public void handleStop(Operation delete) {
        executorService.shutdown();
        awaitTermination(this, executorService);
        super.handleStop(delete);
    }

    @Override
    public void handlePatch(Operation op) {
        if (!op.hasBody()) {
            op.fail(new IllegalArgumentException("body is required"));
            return;
        }
        op.complete();
        EnumerationContext ctx = new EnumerationContext(
                op.getBody(ComputeEnumerateResourceRequest.class));
        validateState(ctx);
        if (ctx.enumRequest.isMockRequest) {
            // patch status to parent task
            AdapterUtils.sendPatchToEnumerationTask(this, ctx.enumRequest.enumerationTaskReference);
            return;
        }
        handleEnumerationRequest(ctx);
    }

    private void handleEnumerationRequest(EnumerationContext ctx) {
        switch (ctx.stage) {
        case HOSTDESC:
            getHostComputeDescription(ctx, EnumerationStages.PARENTAUTH);
            break;
        case PARENTAUTH:
            getParentAuth(ctx, EnumerationStages.CLIENT);
            break;
        case CLIENT:
            if (ctx.credentials == null) {
                try {
                    ctx.credentials = getAzureConfig(ctx.parentAuth);
                } catch (Throwable e) {
                    logSevere(e);
                    ctx.error = e;
                    ctx.stage = EnumerationStages.ERROR;
                    handleEnumerationRequest(ctx);
                    return;
                }
            }

            if (ctx.httpClient == null) {
                ctx.httpClient = new OkHttpClient();
                ctx.clientBuilder = ctx.httpClient.newBuilder();
            }
            ctx.stage = EnumerationStages.ENUMERATE;
            handleEnumerationRequest(ctx);
            break;
        case ENUMERATE:
            String enumKey = getEnumKey(ctx);
            switch (ctx.enumRequest.enumerationAction) {
            case START:
                if (ongoingEnumerations.contains(enumKey)) {
                    logInfo("Enumeration service has already been started for %s", enumKey);
                    return;
                }
                ongoingEnumerations.add(enumKey);
                logInfo("Launching enumeration service for %s", enumKey);
                ctx.enumerationStartTimeInMicros = Utils.getNowMicrosUtc();
                ctx.enumRequest.enumerationAction = EnumerationAction.REFRESH;
                handleEnumerationRequest(ctx);
                break;
            case REFRESH:
                ctx.subStage = EnumerationSubStages.LISTVMS;
                handleSubStage(ctx);
                break;
            case STOP:
                if (ongoingEnumerations.contains(enumKey)) {
                    logInfo("Enumeration service will be stopped for %s", enumKey);
                    ongoingEnumerations.remove(enumKey);
                } else {
                    logInfo("Enumeration service is not running or has already been stopped for %s",
                            enumKey);
                }
                ctx.stage = EnumerationStages.FINISHED;
                handleEnumerationRequest(ctx);
                break;
            default:
                logSevere("Unknown enumeration action %s", ctx.enumRequest.enumerationAction);
                ctx.stage = EnumerationStages.ERROR;
                handleEnumerationRequest(ctx);
                break;
            }
            break;
        case FINISHED:
            logInfo("Enumeration finished for %s", getEnumKey(ctx));
            ongoingEnumerations.remove(getEnumKey(ctx));
            AdapterUtils.sendPatchToEnumerationTask(this, ctx.enumRequest.enumerationTaskReference);
            cleanUpHttpClient(this, ctx.httpClient);
            break;
        case ERROR:
            logWarning("Enumeration error for %s", getEnumKey(ctx));
            AdapterUtils.sendFailurePatchToEnumerationTask(this,
                    ctx.enumRequest.enumerationTaskReference, ctx.error);
            cleanUpHttpClient(this, ctx.httpClient);
            break;
        default:
            String msg = String.format("Unknown Azure enumeration stage %s ", ctx.stage.toString());
            logSevere(msg);
            ctx.error = new IllegalStateException(msg);
            AdapterUtils.sendFailurePatchToEnumerationTask(this,
                    ctx.enumRequest.enumerationTaskReference, ctx.error);
            cleanUpHttpClient(this, ctx.httpClient);
        }
    }

    /**
     * Handle enumeration substages for VM data collection.
     */
    private void handleSubStage(EnumerationContext ctx) {
        if (!ongoingEnumerations.contains(getEnumKey(ctx))) {
            ctx.stage = EnumerationStages.FINISHED;
            handleEnumerationRequest(ctx);
            return;
        }

        switch (ctx.subStage) {
        case LISTVMS:
            enumerate(ctx);
            break;
        case QUERY:
            queryForComputeStates(ctx, ctx.virtualMachines);
            break;
        case UPDATE:
            update(ctx);
            break;
        case CREATE:
            create(ctx);
            break;
        case DELETE:
            delete(ctx);
            break;
        case FINISHED:
            ctx.stage = EnumerationStages.FINISHED;
            handleEnumerationRequest(ctx);
            break;
        default:
            String msg = String
                    .format("Unknown Azure enumeration sub-stage %s ", ctx.subStage.toString());
            ctx.error = new IllegalStateException(msg);
            ctx.stage = EnumerationStages.ERROR;
            handleEnumerationRequest(ctx);
            break;
        }
    }

    /**
     * Deletes undiscovered resources.
     *
     * The logic works by recording a timestamp when enumeration starts. This timestamp is used to
     * lookup resources which haven't been touched as part of current enumeration cycle. The other
     * data point this method uses is the virtual machines discovered as part of list vm call.
     *
     * Finally, delete on a resource is invoked only if it meets two criteria:
     * - Timestamp older than current enumeration cycle.
     * - VM not present on Azure.
     */
    private void delete(EnumerationContext ctx) {
        QueryTask q = new QueryTask();
        q.setDirect(true);
        q.querySpec = new QueryTask.QuerySpecification();
        q.querySpec.options.add(QueryOption.EXPAND_CONTENT);
        q.querySpec.query = Query.Builder.create()
                .addKindFieldClause(ComputeState.class)
                .addFieldClause(ComputeState.FIELD_NAME_PARENT_LINK,
                        ctx.enumRequest.parentComputeLink)
                .addRangeClause(ComputeState.FIELD_NAME_UPDATE_TIME_MICROS,
                        NumericRange.createLessThanRange(ctx.enumerationStartTimeInMicros))
                .build();
        q.tenantLinks = ctx.computeHostDesc.tenantLinks;

        // create the query to find resources
        sendRequest(Operation
                .createPost(this, ServiceUriPaths.CORE_QUERY_TASKS)
                .setBody(q)
                .setCompletion((o, e) -> {
                    if (e != null) {
                        handleError(ctx, e);
                        return;
                    }
                    QueryTask queryTask = o.getBody(QueryTask.class);

                    if (queryTask.results.documentCount == 0) {
                        logInfo("No compute states found for deletion");
                        ctx.subStage = EnumerationSubStages.FINISHED;
                        handleSubStage(ctx);
                        return;
                    }

                    List<Operation> operations = new ArrayList<>();
                    for (Object s : queryTask.results.documents.values()) {
                        ComputeState computeState = Utils
                                .fromJson(s, ComputeState.class);
                        String vmId = computeState.customProperties.get(AZURE_INSTANCE_ID);

                        // Since we only update disks during update, some compute states might be
                        // present in Azure but have older timestamp in local repository.
                        if (ctx.vmIds.contains(vmId)) {
                            continue;
                        }

                        operations.add(Operation.createDelete(this, computeState.documentSelfLink));
                        logFine("Deleting compute state %s", computeState.documentSelfLink);

                        if (computeState.diskLinks != null && !computeState.diskLinks.isEmpty()) {
                            operations.add(Operation
                                    .createDelete(this, computeState.diskLinks.get(0)));
                            logFine("Deleting disk state %s", computeState.diskLinks.get(0));
                        }
                    }

                    if (operations.size() == 0) {
                        logInfo("No compute/disk states deleted");
                        ctx.subStage = EnumerationSubStages.FINISHED;
                        handleSubStage(ctx);
                        return;
                    }

                    OperationJoin.create(operations)
                            .setCompletion((ops, exs) -> {
                                if (exs != null) {
                                    exs.values().forEach(
                                            ex -> logWarning("Error: %s", ex.getMessage()));
                                }
                                logInfo("Finished deletion of compute states for Azure");
                                ctx.subStage = EnumerationSubStages.FINISHED;
                                handleSubStage(ctx);
                            })
                            .sendWith(this);
                }));
    }

    /**
     * Method to retrieve the parent compute host on which the enumeration task will be performed.
     */
    private void getHostComputeDescription(EnumerationContext ctx, EnumerationStages next) {
        Consumer<Operation> onSuccess = (op) -> {
            ctx.computeHostDesc = op.getBody(ComputeDescription.class);
            ctx.stage = next;
            handleEnumerationRequest(ctx);
        };
        URI parentURI = UriUtils.buildExpandLinksQueryUri(
                UriUtils.buildUri(this.getHost(), ctx.enumRequest.computeDescriptionLink));
        AdapterUtils.getServiceState(this, parentURI, onSuccess, getFailureConsumer(ctx));
    }

    /**
     * Method to arrive at the credentials needed to call the Azure API for enumerating the instances.
     */
    private void getParentAuth(EnumerationContext ctx, EnumerationStages next) {
        URI authUri = UriUtils.buildUri(this.getHost(),
                ctx.computeHostDesc.authCredentialsLink);
        Consumer<Operation> onSuccess = (op) -> {
            ctx.parentAuth = op.getBody(AuthCredentialsServiceState.class);
            ctx.stage = next;
            handleEnumerationRequest(ctx);
        };
        AdapterUtils.getServiceState(this, authUri, onSuccess, getFailureConsumer(ctx));
    }

    private Consumer<Throwable> getFailureConsumer(EnumerationContext ctx) {
        return (t) -> {
            ctx.stage = EnumerationStages.ERROR;
            ctx.error = t;
            handleEnumerationRequest(ctx);
        };
    }

    /**
     * Enumerate VMs from Azure.
     */
    private void enumerate(EnumerationContext ctx) {
        // TODO VSYM-628: Paginate enumeration results from Azure
        ComputeManagementClient client = new ComputeManagementClientImpl(AzureConstants.BASE_URI,
                ctx.credentials, ctx.clientBuilder, getRetrofitBuilder());
        client.setSubscriptionId(ctx.parentAuth.userLink);

        logInfo("Enumeration VMs from Azure");
        client.getVirtualMachinesOperations().listAllAsync(new AzureEnumerationAsyncHandler(ctx));
    }

    /**
     * Query all compute states for the cluster filtered by the received set of instance Ids.
     */
    private void queryForComputeStates(EnumerationContext ctx, Map<String, VirtualMachine> vms) {
        QueryTask q = new QueryTask();
        q.setDirect(true);
        q.querySpec = new QueryTask.QuerySpecification();
        q.querySpec.options.add(QueryOption.EXPAND_CONTENT);
        q.querySpec.query = Query.Builder.create()
                .addKindFieldClause(ComputeState.class)
                .addFieldClause(ComputeState.FIELD_NAME_PARENT_LINK,
                        ctx.enumRequest.parentComputeLink)
                .build();

        Query.Builder instanceIdFilterParentQuery = Query.Builder.create(Occurance.MUST_OCCUR);

        for (String instanceId : vms.keySet()) {
            QueryTask.Query instanceIdFilter = Query.Builder.create(Occurance.SHOULD_OCCUR)
                    .addCompositeFieldClause(ComputeState.FIELD_NAME_CUSTOM_PROPERTIES,
                            AZURE_INSTANCE_ID, instanceId).build();
            instanceIdFilterParentQuery.addClause(instanceIdFilter);
        }
        q.querySpec.query.addBooleanClause(instanceIdFilterParentQuery.build());
        q.tenantLinks = ctx.computeHostDesc.tenantLinks;

        sendRequest(Operation
                .createPost(this, ServiceUriPaths.CORE_QUERY_TASKS)
                .setBody(q)
                .setCompletion((o, e) -> {
                    if (e != null) {
                        handleError(ctx, e);
                        return;
                    }
                    QueryTask queryTask = o.getBody(QueryTask.class);

                    logInfo("Found %d matching compute states for Azure VMs",
                            queryTask.results.documentCount);

                    // If there are no matches, there is nothing to update.
                    if (queryTask.results.documentCount == 0) {
                        ctx.subStage = EnumerationSubStages.CREATE;
                        handleSubStage(ctx);
                        return;
                    }

                    for (Object s : queryTask.results.documents.values()) {
                        ComputeState computeState = Utils
                                .fromJson(s, ComputeState.class);
                        String instanceId = computeState.customProperties.get(AZURE_INSTANCE_ID);
                        ctx.computeStates.put(instanceId, computeState);
                    }

                    ctx.subStage = EnumerationSubStages.UPDATE;
                    handleSubStage(ctx);
                }));
    }

    /**
     * Updates matching compute states for given VMs.
     */
    private void update(EnumerationContext ctx) {
        if (ctx.computeStates.size() == 0) {
            logInfo("No compute states available for update");
            ctx.subStage = EnumerationSubStages.CREATE;
            handleSubStage(ctx);
            return;
        }

        Iterator<Entry<String, ComputeState>> iterator = ctx.computeStates.entrySet()
                .iterator();
        AtomicInteger numOfUpdates = new AtomicInteger(ctx.computeStates.size());
        while (iterator.hasNext()) {
            Entry<String, ComputeState> csEntry = iterator.next();
            ComputeState computeState = csEntry.getValue();
            VirtualMachine virtualMachine = ctx.virtualMachines.get(csEntry.getKey());
            iterator.remove();
            updateHelper(ctx, computeState, virtualMachine, numOfUpdates);
        }
    }

    private void updateHelper(EnumerationContext ctx, ComputeState computeState,
            VirtualMachine vm, AtomicInteger numOfUpdates) {
        if (computeState.diskLinks == null || computeState.diskLinks.size() != 1) {
            logWarning("Only 1 disk is currently supported. Update skipped for compute state %s",
                    computeState.id);

            if (ctx.computeStates.size() == 0) {
                logInfo("Finished updating compute states");
                ctx.subStage = EnumerationSubStages.CREATE;
                handleSubStage(ctx);
            }

            return;
        }

        DiskState rootDisk = new DiskState();
        rootDisk.customProperties = new HashMap<>();
        rootDisk.customProperties.put(AZURE_OSDISK_CACHING,
                vm.getStorageProfile().getOsDisk().getCaching());
        rootDisk.documentSelfLink = computeState.diskLinks.get(0);
        // TODO VSYM-630: Discover storage keys for storage account during Azure enumeration

        Operation.createPatch(this, rootDisk.documentSelfLink)
                .setBody(rootDisk)
                .setCompletion((completedOp, failure) -> {
                    // Remove processed virtual machine from the map
                    ctx.virtualMachines
                            .remove(computeState.customProperties.get(AZURE_INSTANCE_ID));

                    if (failure != null) {
                        logSevere(failure);
                    }

                    if (numOfUpdates.decrementAndGet() == 0) {
                        logInfo("Finished updating compute states");
                        ctx.subStage = EnumerationSubStages.CREATE;
                        handleSubStage(ctx);
                    }
                })
                .sendWith(this);
    }

    /**
     * Creates relevant resources for given VMs.
     */
    private void create(EnumerationContext ctx) {
        if (ctx.virtualMachines.size() == 0) {
            logInfo("No virtual machine available for creation");
            ctx.subStage = EnumerationSubStages.DELETE;
            handleSubStage(ctx);
            return;
        }

        logInfo("%d compute description with states to be created", ctx.virtualMachines.size());

        Iterator<Entry<String, VirtualMachine>> iterator = ctx.virtualMachines.entrySet()
                .iterator();
        AtomicInteger size = new AtomicInteger(ctx.virtualMachines.size());

        while (iterator.hasNext()) {
            Entry<String, VirtualMachine> vmEntry = iterator.next();
            VirtualMachine virtualMachine = vmEntry.getValue();
            iterator.remove();
            createHelper(ctx, virtualMachine, size);
        }
    }

    private void createHelper(EnumerationContext ctx, VirtualMachine virtualMachine,
            AtomicInteger size) {
        AuthCredentialsServiceState auth = new AuthCredentialsServiceState();
        auth.userEmail = virtualMachine.getOsProfile().getAdminUsername();
        auth.privateKey = virtualMachine.getOsProfile().getAdminPassword();
        auth.documentSelfLink = UUID.randomUUID().toString();
        auth.tenantLinks = ctx.computeHostDesc.tenantLinks;

        String authLink = UriUtils.buildUriPath(AuthCredentialsService.FACTORY_LINK,
                auth.documentSelfLink);

        Operation authOp = Operation
                .createPost(getHost(), AuthCredentialsService.FACTORY_LINK)
                .setBody(auth);

        // TODO VSYM-631: Match existing descriptions for new VMs discovered on Azure
        ComputeDescription computeDescription = new ComputeDescription();
        computeDescription.id = UUID.randomUUID().toString();
        computeDescription.name = virtualMachine.getName();
        computeDescription.regionId = virtualMachine.getLocation();
        computeDescription.authCredentialsLink = authLink;
        computeDescription.documentSelfLink = computeDescription.id;
        computeDescription.environmentName = ENVIRONMENT_NAME_AZURE;
        computeDescription.instanceAdapterReference = UriUtils
                .buildUri(getHost(), AzureUriPaths.AZURE_INSTANCE_ADAPTER);
        computeDescription.statsAdapterReference = UriUtils
                .buildUri(getHost(), AzureUriPaths.AZURE_STATS_ADAPTER);
        computeDescription.customProperties = new HashMap<>();
        computeDescription.customProperties
                .put(AZURE_VM_SIZE, virtualMachine.getHardwareProfile().getVmSize());
        computeDescription.tenantLinks = ctx.computeHostDesc.tenantLinks;

        Operation compDescOp = Operation
                .createPost(getHost(), ComputeDescriptionService.FACTORY_LINK)
                .setBody(computeDescription);

        // Create root disk
        DiskState rootDisk = new DiskState();
        rootDisk.id = UUID.randomUUID().toString();
        rootDisk.documentSelfLink = rootDisk.id;
        rootDisk.name = virtualMachine.getStorageProfile().getOsDisk().getName();
        rootDisk.type = DiskType.HDD;
        ImageReference imageReference = virtualMachine.getStorageProfile().getImageReference();
        rootDisk.sourceImageReference = URI.create(imageReferenceToImageId(imageReference));
        rootDisk.bootOrder = 1;
        rootDisk.customProperties = new HashMap<>();
        rootDisk.customProperties.put(AZURE_OSDISK_CACHING,
                virtualMachine.getStorageProfile().getOsDisk().getCaching());
        rootDisk.customProperties.put(AZURE_STORAGE_ACCOUNT_NAME,
                getStorageAccountName(
                        virtualMachine.getStorageProfile().getOsDisk().getVhd().getUri()));
        rootDisk.tenantLinks = ctx.computeHostDesc.tenantLinks;

        // TODO VSYM-629: Add logic to fetch storage account type for newly discovered VMs on Azure
        // TODO VSYM-630: Discover storage keys for storage account during Azure enumeration
        //rootDisk.customProperties
        //        .put(AZURE_STORAGE_ACCOUNT_TYPE, AccountType.STANDARD_LRS.toValue());

        Operation diskOp = Operation.createPost(getHost(), DiskService.FACTORY_LINK)
                .setBody(rootDisk);

        List<String> vmDisks = new ArrayList<>();
        vmDisks.add(UriUtils.buildUriPath(DiskService.FACTORY_LINK, rootDisk.id));

        // Create compute state
        ComputeState resource = new ComputeState();
        resource.id = UUID.randomUUID().toString();
        resource.documentSelfLink = resource.id;
        resource.parentLink = ctx.enumRequest.parentComputeLink;
        resource.descriptionLink = UriUtils.buildUriPath(
                ComputeDescriptionService.FACTORY_LINK, computeDescription.id);
        resource.resourcePoolLink = ctx.enumRequest.resourcePoolLink;
        resource.diskLinks = vmDisks;
        resource.customProperties = new HashMap<>();
        resource.customProperties.put(CUSTOM_DISPLAY_NAME, virtualMachine.getName());
        resource.customProperties.put(AZURE_INSTANCE_ID, virtualMachine.getId().toLowerCase());
        resource.tenantLinks = ctx.computeHostDesc.tenantLinks;

        Operation resourceOp = Operation
                .createPost(getHost(), ComputeService.FACTORY_LINK)
                .setBody(resource);

        OperationSequence
                .create(authOp)
                .next(compDescOp)
                .next(diskOp)
                .next(resourceOp)
                .setCompletion((ops, exs) -> {
                    if (exs != null) {
                        exs.values().forEach(ex -> logWarning("Error: %s", ex.getMessage()));
                    }

                    if (size.decrementAndGet() == 0) {
                        ctx.subStage = EnumerationSubStages.DELETE;
                        handleSubStage(ctx);
                    }
                }).sendWith(this);
    }

    /**
     * Method to validate that the passed in Enumeration Request State is valid.
     * Validating that the parent compute link and the adapter links are populated
     * in the request.
     *
     * Also defaulting the EnumerationRequestType to REFRESH
     * @param ctx The enumeration context.
     */
    private void validateState(EnumerationContext ctx) {
        if (ctx.enumRequest.computeDescriptionLink == null) {
            throw new IllegalArgumentException("computeDescriptionLink is required.");
        }
        if (ctx.enumRequest.adapterManagementReference == null) {
            throw new IllegalArgumentException(
                    "adapterManagementReference is required.");
        }
        if (ctx.enumRequest.parentComputeLink == null) {
            throw new IllegalArgumentException(
                    "parentComputeLink is required.");
        }
        if (ctx.enumRequest.enumerationAction == null) {
            ctx.enumRequest.enumerationAction = EnumerationAction.START;
        }
    }

    private Retrofit.Builder getRetrofitBuilder() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.callbackExecutor(executorService);
        return builder;
    }

    private void handleError(EnumerationContext ctx, Throwable e) {
        logSevere(e);
        ctx.error = e;
        ctx.stage = EnumerationStages.ERROR;
        handleEnumerationRequest(ctx);
    }

    /**
     * Return a key to uniquely identify enumeration for compute host instance.
     */
    private String getEnumKey(EnumerationContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("hostLink:").append(ctx.enumRequest.parentComputeLink);
        sb.append("-enumerationAdapterReference:")
                .append(ctx.computeHostDesc.enumerationAdapterReference);
        return sb.toString();
    }

    /**
     * Converts image reference to image identifier.
     */
    private String imageReferenceToImageId(ImageReference imageReference) {
        return imageReference.getPublisher() + ":" + imageReference.getOffer() + ":"
                + imageReference.getSku() + ":" + imageReference.getVersion();
    }

    /**
     * Extracts storage account name from the given storage URI.
     */
    private String getStorageAccountName(String storageUri) {
        Matcher matcher = STORAGE_ACCOUNT_NAME_PATTERN.matcher(storageUri);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return storageUri;
    }

    /**
     * Async callback to handle Azure list VM callback.
     */
    private class AzureEnumerationAsyncHandler extends ListOperationCallback<VirtualMachine> {

        private final OperationContext opContext;
        private final EnumerationContext ctx;

        public AzureEnumerationAsyncHandler(EnumerationContext ctx) {
            opContext = OperationContext.getOperationContext();
            this.ctx = ctx;
        }

        @Override
        public void failure(Throwable e) {
            OperationContext.restoreOperationContext(opContext);
            handleError(ctx, e);
        }

        @Override
        public void success(ServiceResponse<List<VirtualMachine>> result) {
            OperationContext.restoreOperationContext(opContext);
            List<VirtualMachine> virtualMachines = result.getBody();

            logInfo("Retrieved %d VMs from Azure", virtualMachines.size());

            // If there are no VMs in Azure we directly skip over to deletion phase.
            if (virtualMachines.size() == 0) {
                ctx.subStage = EnumerationSubStages.DELETE;
                handleSubStage(ctx);
                return;
            }

            for (VirtualMachine virtualMachine : virtualMachines) {
                // We don't want to process VMs that are being terminated.
                if (AZURE_VM_TERMINATION_STATES.contains(virtualMachine.getProvisioningState())) {
                    logFine("Not processing %d", virtualMachine.getId());
                    continue;
                }

                // Azure for some case changes the case of the vm id.
                String vmId = virtualMachine.getId().toLowerCase();
                ctx.virtualMachines.put(vmId, virtualMachine);
                ctx.vmIds.add(vmId);
            }

            logInfo("Processing %d VMs", ctx.vmIds.size());

            ctx.subStage = EnumerationSubStages.QUERY;
            handleSubStage(ctx);
        }
    }
}
