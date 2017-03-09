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

package com.vmware.photon.controller.model.adapters.vsphere;

import static com.vmware.photon.controller.model.ComputeProperties.RESOURCE_GROUP_NAME;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.netty.util.internal.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.vmware.photon.controller.model.adapters.vsphere.ProvisionContext.NetworkInterfaceStateWithDetails;
import com.vmware.photon.controller.model.adapters.vsphere.network.DvsProperties;
import com.vmware.photon.controller.model.adapters.vsphere.network.NsxProperties;
import com.vmware.photon.controller.model.adapters.vsphere.ovf.OvfDeployer;
import com.vmware.photon.controller.model.adapters.vsphere.ovf.OvfParser;
import com.vmware.photon.controller.model.adapters.vsphere.ovf.OvfRetriever;
import com.vmware.photon.controller.model.adapters.vsphere.util.VimNames;
import com.vmware.photon.controller.model.adapters.vsphere.util.VimPath;
import com.vmware.photon.controller.model.adapters.vsphere.util.connection.BaseHelper;
import com.vmware.photon.controller.model.adapters.vsphere.util.connection.Connection;
import com.vmware.photon.controller.model.adapters.vsphere.util.connection.GetMoRef;
import com.vmware.photon.controller.model.adapters.vsphere.util.finders.Element;
import com.vmware.photon.controller.model.adapters.vsphere.util.finders.Finder;
import com.vmware.photon.controller.model.adapters.vsphere.util.finders.FinderException;
import com.vmware.photon.controller.model.resources.ComputeService.ComputeState;
import com.vmware.photon.controller.model.resources.ComputeService.ComputeStateWithDescription;
import com.vmware.photon.controller.model.resources.DiskService.DiskState;
import com.vmware.photon.controller.model.resources.DiskService.DiskState.BootConfig.FileEntry;
import com.vmware.photon.controller.model.resources.DiskService.DiskStatus;
import com.vmware.photon.controller.model.resources.DiskService.DiskType;
import com.vmware.vim25.ArrayOfManagedObjectReference;
import com.vmware.vim25.ArrayOfVAppPropertyInfo;
import com.vmware.vim25.ArrayOfVirtualDevice;
import com.vmware.vim25.ArrayUpdateOperation;
import com.vmware.vim25.DistributedVirtualSwitchPortConnection;
import com.vmware.vim25.DuplicateName;
import com.vmware.vim25.FileAlreadyExists;
import com.vmware.vim25.InvalidCollectorVersionFaultMsg;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.MethodFault;
import com.vmware.vim25.OvfNetworkMapping;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VAppPropertyInfo;
import com.vmware.vim25.VAppPropertySpec;
import com.vmware.vim25.VirtualCdrom;
import com.vmware.vim25.VirtualCdromAtapiBackingInfo;
import com.vmware.vim25.VirtualCdromIsoBackingInfo;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecFileOperation;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualDeviceConnectInfo;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.VirtualDiskMode;
import com.vmware.vim25.VirtualDiskSpec;
import com.vmware.vim25.VirtualE1000;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardDistributedVirtualPortBackingInfo;
import com.vmware.vim25.VirtualEthernetCardMacType;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualEthernetCardOpaqueNetworkBackingInfo;
import com.vmware.vim25.VirtualFloppy;
import com.vmware.vim25.VirtualFloppyDeviceBackingInfo;
import com.vmware.vim25.VirtualFloppyImageBackingInfo;
import com.vmware.vim25.VirtualIDEController;
import com.vmware.vim25.VirtualLsiLogicController;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineFileInfo;
import com.vmware.vim25.VirtualMachineGuestOsIdentifier;
import com.vmware.vim25.VirtualMachineRelocateDiskMoveOptions;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualMachineSnapshotInfo;
import com.vmware.vim25.VirtualPCIController;
import com.vmware.vim25.VirtualSCSIController;
import com.vmware.vim25.VirtualSCSISharing;
import com.vmware.vim25.VirtualSIOController;
import com.vmware.vim25.VmConfigSpec;
import com.vmware.xenon.common.Utils;

/**
 * A simple client for vsphere. Consist of a valid connection and some context. This class does
 * blocking IO but doesn't talk back to xenon. A client operates in the context of a datacenter. If
 * the datacenter cannot be determined at construction time a ClientException is thrown.
 */
public class InstanceClient extends BaseHelper {
    private static final Logger logger = LoggerFactory.getLogger(InstanceClient.class.getName());

    private static final String CLOUD_CONFIG_PROPERTY_USER_DATA = "user-data";
    private static final String COREOS_CLOUD_CONFIG_PROPERTY_USER_DATA = "guestinfo.coreos.config.data";
    private static final String COREOS_CLOUD_CONFIG_PROPERTY_USER_DATA_ENCODING = "guestinfo.coreos.config.data.encoding";
    private static final String CLOUD_CONFIG_BASE64_ENCODING = "base64";

    private static final String CLOUD_CONFIG_PROPERTY_HOSTNAME = "hostname";
    private static final String COREOS_CLOUD_CONFIG_PROPERTY_HOSTNAME = "guestinfo.guestinfo.hostname";

    private static final String CLOUD_CONFIG_PROPERTY_PUBLIC_KEYS = "public-keys";
    private static final String OVF_PROPERTY_ENV = "ovf-env";

    private static final String CLONE_STRATEGY_FULL = "FULL";

    private static final String CLONE_STRATEGY_LINKED = "LINKED";

    private final ComputeStateWithDescription state;
    private final ComputeStateWithDescription parent;
    private final List<DiskState> disks;
    private final List<NetworkInterfaceStateWithDetails> nics;
    private final ManagedObjectReference placementTarget;
    private final String targetDatacenterPath;

    private final GetMoRef get;
    private final Finder finder;
    private ManagedObjectReference vm;
    private ManagedObjectReference datastore;
    private ManagedObjectReference resourcePool;
    private ManagedObjectReference host;

    private static final VirtualMachineGuestOsIdentifier DEFAULT_GUEST_ID = VirtualMachineGuestOsIdentifier.OTHER_GUEST_64;

    public InstanceClient(Connection connection,
            ComputeStateWithDescription resource,
            ComputeStateWithDescription parent,
            List<DiskState> disks,
            List<NetworkInterfaceStateWithDetails> nics,
            ManagedObjectReference placementTarget,
            String targetDatacenterPath)
            throws ClientException, FinderException {
        super(connection);

        this.state = resource;
        this.parent = parent;
        this.disks = disks;
        this.nics = nics;
        this.placementTarget = placementTarget;
        this.targetDatacenterPath = targetDatacenterPath;

        try {
            this.finder = new Finder(connection, this.targetDatacenterPath);
        } catch (RuntimeFaultFaultMsg | InvalidPropertyFaultMsg e) {
            throw new ClientException(
                    String.format("Error looking for datacenter for id '%s'",
                            this.targetDatacenterPath),
                    e);
        }

        this.get = new GetMoRef(this.connection);
    }

    public ComputeState createInstanceFromTemplate(ManagedObjectReference template)
            throws Exception {
        ManagedObjectReference vm = cloneVm(template);

        VirtualMachineConfigSpec spec = new VirtualMachineConfigSpec();

        // even though this is a clone, hw config from the compute resource
        // is takes precedence
        spec.setNumCPUs((int) this.state.description.cpuCount);

        String gt = CustomProperties.of(this.state).getString(CustomProperties.GUEST_ID, null);
        if (gt != null) {
            spec.setGuestId(gt);
        }

        spec.setMemoryMB(toMb(this.state.description.totalMemoryBytes));

        // set ovf environment
        ArrayOfVAppPropertyInfo infos = this.get.entityProp(vm,
                VimPath.vm_config_vAppConfig_property);
        populateCloudConfig(spec, infos);

        // remove nics and attach to proper networks if nics are configured
        if (this.nics != null && this.nics.size() > 0) {
            ArrayOfVirtualDevice devices = this.get.entityProp(vm,
                    VimPath.vm_config_hardware_device);
            devices.getVirtualDevice().stream()
                    .filter(d -> d instanceof VirtualEthernetCard)
                    .forEach(nic -> {
                        VirtualDeviceConfigSpec removeNicChange = new VirtualDeviceConfigSpec();
                        removeNicChange.setOperation(VirtualDeviceConfigSpecOperation.REMOVE);
                        removeNicChange.setDevice(nic);
                        spec.getDeviceChange().add(removeNicChange);
                    });

            for (NetworkInterfaceStateWithDetails niState : this.nics) {
                VirtualDevice nic = createNic(niState, null);
                addDeviceToVm(spec, nic);
            }
        }

        ManagedObjectReference task = getVimPort().reconfigVMTask(vm, spec);
        TaskInfo info = waitTaskEnd(task);

        if (info.getState() == TaskInfoState.ERROR) {
            return VimUtils.rethrow(info.getError());
        }

        if (vm == null) {
            // vm was created by someone else
            return null;
        }

        // store reference to created vm for further processing
        this.vm = vm;

        ComputeState state = new ComputeState();
        state.resourcePoolLink = VimUtils
                .firstNonNull(this.state.resourcePoolLink, this.parent.resourcePoolLink);

        return state;
    }

    private ManagedObjectReference cloneVm(ManagedObjectReference template) throws Exception {
        ManagedObjectReference folder = getVmFolder();
        ManagedObjectReference datastore = getDatastore();
        ManagedObjectReference resourcePool = getResourcePool();

        VirtualMachineRelocateSpec relocSpec = new VirtualMachineRelocateSpec();
        relocSpec.setDatastore(datastore);
        relocSpec.setFolder(folder);
        relocSpec.setPool(resourcePool);
        relocSpec.setDiskMoveType(computeDiskMoveType().value());

        VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
        cloneSpec.setLocation(relocSpec);
        cloneSpec.setPowerOn(false);
        cloneSpec.setTemplate(false);

        String displayName = this.state.name;

        ManagedObjectReference cloneTask = getVimPort()
                .cloneVMTask(template, folder, displayName, cloneSpec);

        TaskInfo info = waitTaskEnd(cloneTask);

        if (info.getState() == TaskInfoState.ERROR) {
            MethodFault fault = info.getError().getFault();
            if (fault instanceof FileAlreadyExists) {
                // a .vmx file already exists, assume someone won the race to create the vm
                return null;
            } else {
                return VimUtils.rethrow(info.getError());
            }
        }

        return (ManagedObjectReference) info.getResult();
    }

    private VirtualMachineRelocateDiskMoveOptions computeDiskMoveType() {
        String strategy = CustomProperties.of(this.state)
                .getString(CustomProperties.CLONE_STRATEGY, CLONE_STRATEGY_LINKED);

        if (CLONE_STRATEGY_FULL.equals(strategy)) {
            return VirtualMachineRelocateDiskMoveOptions.MOVE_ALL_DISK_BACKINGS_AND_ALLOW_SHARING;
        } else if (CLONE_STRATEGY_LINKED.equals(strategy)) {
            return VirtualMachineRelocateDiskMoveOptions.MOVE_CHILD_MOST_DISK_BACKING;
        } else {
            logger.warn("Unknown clone strategy {}, defaulting to LINKED", strategy);
            return VirtualMachineRelocateDiskMoveOptions.MOVE_CHILD_MOST_DISK_BACKING;
        }
    }

    public void deleteInstance() throws Exception {
        ManagedObjectReference vm = CustomProperties.of(this.state)
                .getMoRef(CustomProperties.MOREF);
        if (vm == null) {
            logger.info("No moref associated with the given instance, skipping delete.");
            return;
        }

        TaskInfo info;
        // power off
        ManagedObjectReference task = getVimPort().powerOffVMTask(vm);
        info = waitTaskEnd(task);
        ignoreError("Ignore error powering off VM", info);

        // delete vm
        task = getVimPort().destroyTask(vm);
        info = waitTaskEnd(task);
        ignoreError("Ignore error deleting VM", info);
    }

    private void ignoreError(String s, TaskInfo info) {
        if (info.getState() == TaskInfoState.ERROR) {
            logger.info(s + ": " + info.getError().getLocalizedMessage());
        }
    }

    /**
     * Does provisioning and return a patchable state to patch the resource.
     *
     * @return
     */
    public ComputeState createInstance() throws Exception {
        if (this.targetDatacenterPath == null || this.targetDatacenterPath.length() == 0) {
            throw new IllegalArgumentException(
                    "Datacenter is required for provisioning "
                            + this.state.description.documentSelfLink);
        }

        ManagedObjectReference vm;

        if (isOvfDeploy()) {
            vm = deployOvf();
            this.vm = vm;
        } else {
            vm = createVm();

            if (vm == null) {
                // vm was created by someone else
                return null;
            }

            // store reference to created vm for further processing
            this.vm = vm;
            attachDisks(this.disks);
        }

        ComputeState state = new ComputeState();
        state.resourcePoolLink = VimUtils
                .firstNonNull(this.state.resourcePoolLink, this.parent.resourcePoolLink);

        return state;
    }

    private boolean isOvfDeploy() {
        CustomProperties cp = CustomProperties.of(this.state.description);
        return cp.getString(OvfParser.PROP_OVF_URI) != null ||
                cp.getString(OvfParser.PROP_OVF_ARCHIVE_URI) != null;
    }

    private ManagedObjectReference deployOvf() throws Exception {
        OvfDeployer deployer = new OvfDeployer(this.connection);
        CustomProperties cust = CustomProperties.of(this.state.description);

        URI ovfUri = cust.getUri(OvfParser.PROP_OVF_URI);

        URI archiveUri = cust.getUri(OvfParser.PROP_OVF_ARCHIVE_URI);
        if (archiveUri != null) {
            logger.info("Prefer ova {} uri to ovf {}", archiveUri, ovfUri);
            OvfRetriever retriever = deployer.getRetriever();
            ovfUri = retriever.downloadIfOva(archiveUri);
        }

        ManagedObjectReference folder = getVmFolder();
        ManagedObjectReference ds = getDatastore();
        ManagedObjectReference resourcePool = getResourcePool();

        String vmName = "pmt-" + deployer.getRetriever().hash(ovfUri);

        GetMoRef get = new GetMoRef(this.connection);

        ManagedObjectReference vm = findTemplateByName(vmName, get);
        if (vm != null) {
            if (!isSameDatastore(ds, vm, get)) {
                vm = replicateVMTemplate(resourcePool, ds, folder, vmName, vm, get);
            }
        } else {
            String config = cust.getString(OvfParser.PROP_OVF_CONFIGURATION);
            try {
                OvfParser parser = new OvfParser();
                Document ovfDoc = parser.retrieveDescriptor(ovfUri);
                List<OvfNetworkMapping> networks = mapNetworks(parser.extractNetworks(ovfDoc),
                        ovfDoc, this.nics);
                vm = deployer.deployOvf(ovfUri, getHost(), folder, vmName, networks,
                        ds, Collections.emptyList(), config, resourcePool);

                logger.info("Removing NICs from deployed template: {} ({})", vmName, vm.getValue());
                ArrayOfVirtualDevice devices = get.entityProp(vm,
                        VimPath.vm_config_hardware_device);
                if (devices != null) {
                    VirtualMachineConfigSpec reconfig = new VirtualMachineConfigSpec();

                    for (VirtualDevice device : devices.getVirtualDevice()) {
                        if (device instanceof VirtualEthernetCard) {
                            VirtualDeviceConfigSpec spec = new VirtualDeviceConfigSpec();
                            spec.setDevice(device);
                            spec.setOperation(VirtualDeviceConfigSpecOperation.REMOVE);
                            reconfig.getDeviceChange().add(spec);
                        }
                    }
                    ManagedObjectReference reconfigTask = getVimPort().reconfigVMTask(vm, reconfig);
                    VimUtils.waitTaskEnd(this.connection, reconfigTask);
                }
                ManagedObjectReference snapshotTask = getVimPort().createSnapshotTask(vm, "initial",
                        null, false, false);
                VimUtils.waitTaskEnd(this.connection, snapshotTask);
            } catch (Exception e) {
                logger.warn("Error deploying Ovf for template [" + vmName + "],reason:", e);
                vm = awaitVM(vmName, folder, ds, get);
            }
        }

        return cloneOvfBasedTemplate(vm, ds, folder, resourcePool);
    }

    private List<OvfNetworkMapping> mapNetworks(List<String> ovfNetworkNames, Document ovfDoc,
            List<NetworkInterfaceStateWithDetails> nics)
            throws FinderException, InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        List<OvfNetworkMapping> networks = new ArrayList<>();

        if (ovfNetworkNames.isEmpty() || nics.isEmpty()) {
            return networks;
        }

        CustomProperties custProp;
        ManagedObjectReference moRef;
        NetworkInterfaceStateWithDetails nic = nics.iterator().next();
        if (nic.network != null) {
            custProp = CustomProperties.of(nic.network);
        } else {
            custProp = CustomProperties.of(nic.subnet);
        }
        moRef = custProp.getMoRef(CustomProperties.MOREF);

        if (moRef == null) {
            moRef = this.finder.networkList("*").iterator().next().object;
        }

        final ManagedObjectReference finalMoRef = moRef;
        ovfNetworkNames.forEach(n -> {
            OvfNetworkMapping nm = new OvfNetworkMapping();
            nm.setName(n);
            nm.setNetwork(finalMoRef);
            networks.add(nm);
        });
        return networks;
    }

    private ManagedObjectReference replicateVMTemplate(ManagedObjectReference resourcePool,
            ManagedObjectReference datastore, ManagedObjectReference vmFolder, String vmName,
            ManagedObjectReference vm, GetMoRef get) throws Exception {
        logger.info("Template lives on a different datastore, looking for a local copy of: {}.",
                vmName);

        String replicatedName = vmName + "_" + datastore.getValue();
        ManagedObjectReference repVm = findTemplateByName(replicatedName, get);
        if (repVm != null) {
            return repVm;
        }

        logger.info("Replicating {} ({}) to {}", vmName, vm.getValue(), replicatedName);

        VirtualMachineRelocateSpec spec = new VirtualMachineRelocateSpec();
        spec.setPool(resourcePool);
        spec.setDatastore(datastore);

        VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
        cloneSpec.setLocation(spec);
        cloneSpec.setTemplate(false);
        ManagedObjectReference cloneTask = getVimPort()
                .cloneVMTask(vm, vmFolder, replicatedName, cloneSpec);

        TaskInfo info = VimUtils.waitTaskEnd(this.connection, cloneTask);

        if (info.getState() == TaskInfoState.ERROR) {
            MethodFault fault = info.getError().getFault();
            if (fault instanceof DuplicateName) {
                logger.info(
                        "Template is being replicated by another thread, waiting for {} to be ready",
                        replicatedName);
                return awaitVM(replicatedName, vmFolder, datastore, get);
            } else {
                return VimUtils.rethrow(info.getError());
            }
        }

        ManagedObjectReference rvm = (ManagedObjectReference) info.getResult();
        logger.info("Replicated {} ({}) to {} ({})", vmName, vm.getValue(), replicatedName,
                rvm.getValue());
        logger.info("Creating initial snapshot for linked clones on {}", rvm.getValue());
        ManagedObjectReference snapshotTask = getVimPort().createSnapshotTask(rvm, "initial",
                null, false, false);
        VimUtils.waitTaskEnd(this.connection, snapshotTask);
        logger.info("Created initial snapshot for linked clones on {}", rvm.getValue());
        return rvm;

    }

    private ManagedObjectReference awaitVM(String replicatedName, ManagedObjectReference vmFolder,
            ManagedObjectReference datastore, GetMoRef get)
            throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg, FinderException {

        Element element = this.finder.fullPath(vmFolder);
        String path = element.path;

        if (path.endsWith("/")) {
            path = path + replicatedName;
        } else {
            path = path + "/" + replicatedName;
        }

        // remove the datacenters folder from path, as findByInventoryPath is using relative to it
        // paths
        if (path.startsWith("/Datacenters")) {
            path = path.substring("/Datacenters".length());
        }

        logger.info("Searching for vm using InventoryPath {}", path);
        ManagedObjectReference reference = getVimPort()
                .findByInventoryPath(getServiceContent().getSearchIndex(), path);

        Object snapshot = get.entityProp(reference, VimPath.vm_snapshot);
        if (snapshot == null) {
            int retryCount = 30;
            while (retryCount > 0) {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    return null;
                }
                snapshot = get.entityProp(reference, VimPath.vm_snapshot);
                if (snapshot != null) {
                    return reference;
                }
            }
        }

        return reference;
    }

    private ManagedObjectReference findTemplateByName(String vmName, GetMoRef get) {
        try {
            return get.vmByVMname(vmName,
                    this.connection.getServiceContent().getPropertyCollector());
        } catch (InvalidPropertyFaultMsg | RuntimeFaultFaultMsg e) {
            logger.debug("Error finding template vm[" + vmName + "]", e);
            return null;
        }
    }

    private boolean isSameDatastore(ManagedObjectReference datastore, ManagedObjectReference vm,
            GetMoRef get) throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        ArrayOfManagedObjectReference datastores = get.entityProp(vm,
                VimPath.vm_datastore);
        if (null != datastores) {
            for (ManagedObjectReference p : datastores.getManagedObjectReference()) {
                if (p.getValue().equals(datastore.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    private ManagedObjectReference cloneOvfBasedTemplate(ManagedObjectReference vmTempl,
            ManagedObjectReference datastore, ManagedObjectReference folder,
            ManagedObjectReference resourcePool) throws Exception {

        String vmName = this.state.name;

        Map<String, Object> props = this.get.entityProps(vmTempl, VimPath.vm_summary_config_numCpu,
                VimPath.vm_summary_config_memorySizeMB, VimPath.vm_snapshot,
                VimPath.vm_config_hardware_device, VimPath.vm_config_vAppConfig_property);

        VirtualMachineSnapshotInfo snapshot = (VirtualMachineSnapshotInfo) props
                .get(VimPath.vm_snapshot);// this.get.entityProp(vmTempl, VimPath.vm_snapshot);
        ArrayOfVirtualDevice devices = (ArrayOfVirtualDevice) props
                .get(VimPath.vm_config_hardware_device);// this.get.entityProp(vmTempl,
        // VimPath.vm_config_hardware_device);

        VirtualDisk vd = devices.getVirtualDevice().stream()
                .filter(d -> d instanceof VirtualDisk)
                .map(d -> (VirtualDisk) d).findFirst().orElse(null);
        VirtualMachineRelocateDiskMoveOptions diskMoveOptions = VirtualMachineRelocateDiskMoveOptions.CREATE_NEW_CHILD_DISK_BACKING;

        String datastoreName = this.get.entityProp(datastore, VimPath.ds_summary_name);
        VirtualDevice scsiController = getFirstScsiController(devices);
        int scsiUnit = findFreeUnit(scsiController, devices.getVirtualDevice());

        List<VirtualDeviceConfigSpec> newDisks = new ArrayList<>();
        DiskState bootDisk = findBootDisk();
        if (bootDisk != null) {
            if (vd == null) {
                String path = makePathToVmdkFile("ephemeral_disk", vmName);
                String diskName = String.format("[%s] %s", datastoreName, path);
                VirtualDeviceConfigSpec hdd = createHdd(scsiController.getKey(), scsiUnit, bootDisk,
                        diskName, datastore);
                newDisks.add(hdd);
            } else {
                if (vd.getCapacityInKB() < toKb(bootDisk.capacityMBytes)) {
                    VirtualDeviceConfigSpec hdd = resizeHdd(vd, bootDisk);
                    newDisks.add(hdd);
                    diskMoveOptions = VirtualMachineRelocateDiskMoveOptions.MOVE_CHILD_MOST_DISK_BACKING;
                }
            }
        }

        VirtualCdrom vcd = devices.getVirtualDevice().stream()
                .filter(d -> d instanceof VirtualCdrom)
                .map(d -> (VirtualCdrom) d).findFirst().orElse(null);

        // add a cdrom so that ovf transport works
        if (vcd == null) {
            VirtualDevice ideController = getFirstIdeController(devices);
            int ideUnit = findFreeUnit(ideController, devices.getVirtualDevice());
            VirtualDeviceConfigSpec cdrom = createCdrom(ideController, ideUnit);
            newDisks.add(cdrom);
        } else {
            VirtualDeviceConfigSpec cdrom = reconfigureCdrom(vcd);
            newDisks.add(cdrom);
        }

        VirtualMachineConfigSpec spec = new VirtualMachineConfigSpec();

        // even though this is a clone, hw config from the compute resource
        // is takes precedence
        spec.setNumCPUs((int) this.state.description.cpuCount);
        spec.setMemoryMB(toMb(this.state.description.totalMemoryBytes));
        String gt = CustomProperties.of(this.state).getString(CustomProperties.GUEST_ID, null);
        if (gt != null) {
            spec.setGuestId(gt);
        }

        // set ovf environment
        ArrayOfVAppPropertyInfo infos = (ArrayOfVAppPropertyInfo) props
                .get(VimPath.vm_config_vAppConfig_property);// this.get.entityProp(vmTempl,
        // VimPath.vm_config_vAppConfig_property);
        populateVAppProperties(spec, infos);
        populateCloudConfig(spec, infos);
        // add disks one at a time
        for (VirtualDeviceConfigSpec newDisk : newDisks) {
            spec.getDeviceChange().add(newDisk);
        }

        // configure network
        VirtualPCIController pci = getFirstPciController(devices);
        for (NetworkInterfaceStateWithDetails nicWithDetails : this.nics) {
            VirtualDevice nic = createNic(nicWithDetails, pci.getControllerKey());
            addDeviceToVm(spec, nic);
        }

        // remove any networks from the template
        devices.getVirtualDevice().stream()
                .filter(d -> VirtualEthernetCard.class.isAssignableFrom(d.getClass()))
                .forEach(d -> addRemoveDeviceFromVm(spec, d));

        VirtualMachineRelocateSpec relocSpec = new VirtualMachineRelocateSpec();
        relocSpec.setDatastore(datastore);
        relocSpec.setFolder(folder);
        relocSpec.setPool(resourcePool);
        relocSpec.setDiskMoveType(diskMoveOptions.value());

        VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
        cloneSpec.setLocation(relocSpec);
        cloneSpec.setPowerOn(false);
        cloneSpec.setTemplate(false);
        cloneSpec.setSnapshot(snapshot.getCurrentSnapshot());
        cloneSpec.setConfig(spec);

        ManagedObjectReference cloneTask = getVimPort().cloneVMTask(vmTempl, folder, vmName,
                cloneSpec);
        TaskInfo info = waitTaskEnd(cloneTask);

        if (info.getState() == TaskInfoState.ERROR) {
            return VimUtils.rethrow(info.getError());
        }

        return (ManagedObjectReference) info.getResult();
    }

    /**
     * The first HDD disk is considered the boot disk.
     *
     * @return
     */
    private DiskState findBootDisk() {
        if (this.disks == null) {
            return null;
        }

        return this.disks.stream()
                .filter(d -> d.type == DiskType.HDD)
                .findFirst()
                .orElse(null);
    }

    /**
     * Creates disks and attaches them to the vm created by {@link #createInstance()}. The given
     * diskStates are enriched with data from vSphere and can be patched back to xenon.
     */
    public void attachDisks(List<DiskState> diskStates) throws Exception {
        if (isOvfDeploy()) {
            return;
        }

        if (this.vm == null) {
            throw new IllegalStateException("Cannot attach diskStates if VM is not created");
        }

        EnumSet<DiskType> notSupportedTypes = EnumSet.of(DiskType.SSD, DiskType.NETWORK);
        List<DiskState> unsupportedDisks = diskStates.stream()
                .filter(d -> notSupportedTypes.contains(d.type))
                .collect(Collectors.toList());
        if (!unsupportedDisks.isEmpty()) {
            throw new IllegalStateException(
                    "Some diskStates cannot be created: " + unsupportedDisks.stream()
                            .map(d -> d.documentSelfLink).collect(Collectors.toList()));
        }

        // the path to folder holding all vm files
        String dir = this.get.entityProp(this.vm, VimPath.vm_config_files_vmPathName);
        dir = Paths.get(dir).getParent().toString();

        ArrayOfVirtualDevice devices = this.get
                .entityProp(this.vm, VimPath.vm_config_hardware_device);

        VirtualDevice scsiController = getFirstScsiController(devices);
        int scsiUnit = findFreeUnit(scsiController, devices.getVirtualDevice());

        VirtualDevice ideController = getFirstIdeController(devices);
        int ideUnit = findFreeUnit(ideController, devices.getVirtualDevice());

        VirtualDevice sioController = getFirstSioController(devices);
        int sioUnit = findFreeUnit(sioController, devices.getVirtualDevice());

        List<VirtualDeviceConfigSpec> newDisks = new ArrayList<>();

        boolean cdromAdded = false;

        for (DiskState ds : diskStates) {
            String diskPath = VimUtils.uriToDatastorePath(ds.sourceImageReference);

            if (ds.type == DiskType.HDD) {
                if (diskPath != null) {
                    // create full clone of given disk
                    VirtualDeviceConfigSpec hdd = createFullCloneAndAttach(diskPath, ds, dir,
                            scsiController, scsiUnit);
                    newDisks.add(hdd);
                } else {
                    String diskName = makePathToVmdkFile(ds.id, dir);
                    VirtualDeviceConfigSpec hdd = createHdd(scsiController.getKey(),
                            scsiUnit, ds, diskName, getDatastore());
                    newDisks.add(hdd);
                }

                scsiUnit = nextUnitNumber(scsiUnit);
            }
            if (ds.type == DiskType.CDROM) {
                VirtualDeviceConfigSpec cdrom = createCdrom(ideController, ideUnit);
                ideUnit = nextUnitNumber(ideUnit);
                if (diskPath != null) {
                    // mount iso image
                    insertCdrom((VirtualCdrom) cdrom.getDevice(), diskPath);
                }
                newDisks.add(cdrom);
                cdromAdded = true;
            }
            if (ds.type == DiskType.FLOPPY) {
                VirtualDeviceConfigSpec floppy = createFloppy(sioController, sioUnit);
                sioUnit = nextUnitNumber(sioUnit);
                if (diskPath != null) {
                    // mount iso image
                    insertFloppy((VirtualFloppy) floppy.getDevice(), diskPath);
                }
                newDisks.add(floppy);
            }

            // mark disk as attached
            ds.status = DiskStatus.ATTACHED;
        }

        // add a cdrom so that ovf transport works
        if (!cdromAdded) {
            VirtualDeviceConfigSpec cdrom = createCdrom(ideController, ideUnit);
            newDisks.add(cdrom);
        }

        // add disks one at a time
        for (VirtualDeviceConfigSpec newDisk : newDisks) {
            VirtualMachineConfigSpec spec = new VirtualMachineConfigSpec();
            spec.getDeviceChange().add(newDisk);

            ManagedObjectReference reconfigureTask = getVimPort().reconfigVMTask(this.vm, spec);
            TaskInfo info = waitTaskEnd(reconfigureTask);
            if (info.getState() == TaskInfoState.ERROR) {
                VimUtils.rethrow(info.getError());
            }
        }
    }

    private TaskInfo waitTaskEnd(ManagedObjectReference task)
            throws InvalidCollectorVersionFaultMsg, InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        return VimUtils.waitTaskEnd(this.connection, task);
    }

    private VirtualDeviceConfigSpec createFullCloneAndAttach(String sourcePath, DiskState ds,
            String dir, VirtualDevice scsiController, int unitNumber)
            throws Exception {

        ManagedObjectReference diskManager = this.connection.getServiceContent()
                .getVirtualDiskManager();

        // put full clone in the vm folder
        String destName = makePathToVmdkFile(ds.id, dir);

        // all ops are within a datacenter
        ManagedObjectReference sourceDc = this.finder.datacenter(this.targetDatacenterPath).object;
        ManagedObjectReference destDc = sourceDc;

        Boolean force = true;

        // spec is not supported, should use null for now
        VirtualDiskSpec spec = null;

        ManagedObjectReference task = getVimPort()
                .copyVirtualDiskTask(diskManager, sourcePath, sourceDc, destName, destDc, spec,
                        force);

        // wait for the disk to be copied
        TaskInfo taskInfo = waitTaskEnd(task);
        if (taskInfo.getState() == TaskInfoState.ERROR) {
            return VimUtils.rethrow(taskInfo.getError());
        }

        VirtualDisk disk = new VirtualDisk();

        VirtualDiskFlatVer2BackingInfo backing = new VirtualDiskFlatVer2BackingInfo();
        backing.setDiskMode(VirtualDiskMode.PERSISTENT.value());
        backing.setThinProvisioned(true);
        backing.setFileName(destName);
        backing.setDatastore(getDatastore());

        disk.setBacking(backing);
        disk.setControllerKey(scsiController.getKey());
        disk.setUnitNumber(unitNumber);
        disk.setKey(-1);

        VirtualDeviceConfigSpec change = new VirtualDeviceConfigSpec();
        change.setDevice(disk);
        change.setOperation(VirtualDeviceConfigSpecOperation.ADD);

        return change;
    }

    private VirtualDeviceConfigSpec createCdrom(VirtualDevice ideController, int unitNumber) {
        VirtualCdrom cdrom = new VirtualCdrom();

        cdrom.setControllerKey(ideController.getKey());
        cdrom.setUnitNumber(unitNumber);

        VirtualDeviceConnectInfo info = new VirtualDeviceConnectInfo();
        info.setAllowGuestControl(true);
        info.setConnected(true);
        info.setStartConnected(true);
        cdrom.setConnectable(info);

        VirtualCdromAtapiBackingInfo backing = new VirtualCdromAtapiBackingInfo();
        backing.setDeviceName(String.format("cdrom-%d-%d", ideController.getKey(), unitNumber));
        backing.setUseAutoDetect(false);
        cdrom.setBacking(backing);

        VirtualDeviceConfigSpec spec = new VirtualDeviceConfigSpec();
        spec.setDevice(cdrom);
        spec.setOperation(VirtualDeviceConfigSpecOperation.ADD);

        return spec;
    }

    private VirtualDeviceConfigSpec reconfigureCdrom(VirtualCdrom vcd) {
        VirtualCdrom cdrom = new VirtualCdrom();

        cdrom.setControllerKey(vcd.getControllerKey());
        cdrom.setKey(vcd.getKey());
        cdrom.setUnitNumber(vcd.getUnitNumber());

        VirtualDeviceConnectInfo info = new VirtualDeviceConnectInfo();
        info.setAllowGuestControl(true);
        info.setConnected(true);
        info.setStartConnected(true);
        cdrom.setConnectable(info);

        cdrom.setBacking(vcd.getBacking());

        VirtualDeviceConfigSpec spec = new VirtualDeviceConfigSpec();
        spec.setDevice(cdrom);
        spec.setOperation(VirtualDeviceConfigSpecOperation.EDIT);

        return spec;
    }

    /**
     * Changes to backing of the cdrom to an iso-backed one.
     *
     * @param cdrom
     * @param imagePath
     *            path to iso on disk, sth. like "[datastore] /images/ubuntu-16.04-amd64.iso"
     */
    private void insertCdrom(VirtualCdrom cdrom, String imagePath) {
        VirtualCdromIsoBackingInfo backing = new VirtualCdromIsoBackingInfo();
        backing.setFileName(imagePath);

        cdrom.setBacking(backing);
    }

    /**
     * Changes to backing of the floppy to an image-backed one.
     *
     * @param floppy
     * @param imagePath
     */
    private void insertFloppy(VirtualFloppy floppy, String imagePath) {
        VirtualFloppyImageBackingInfo backingInfo = new VirtualFloppyImageBackingInfo();
        backingInfo.setFileName(imagePath);
        floppy.setBacking(backingInfo);
    }

    private VirtualDeviceConfigSpec createFloppy(VirtualDevice sioController, int unitNumber) {
        VirtualFloppy floppy = new VirtualFloppy();

        floppy.setControllerKey(sioController.getKey());
        floppy.setUnitNumber(unitNumber);

        VirtualDeviceConnectInfo info = new VirtualDeviceConnectInfo();
        info.setAllowGuestControl(true);
        info.setConnected(true);
        info.setStartConnected(true);
        floppy.setConnectable(info);

        VirtualFloppyDeviceBackingInfo backing = new VirtualFloppyDeviceBackingInfo();
        backing.setDeviceName(String.format("floppy-%d", unitNumber));
        floppy.setBacking(backing);

        VirtualDeviceConfigSpec spec = new VirtualDeviceConfigSpec();
        spec.setDevice(floppy);
        spec.setOperation(VirtualDeviceConfigSpecOperation.ADD);
        return spec;
    }

    private VirtualSIOController getFirstSioController(ArrayOfVirtualDevice devices) {
        for (VirtualDevice dev : devices.getVirtualDevice()) {
            if (dev instanceof VirtualSIOController) {
                return (VirtualSIOController) dev;
            }
        }

        throw new IllegalStateException("No SIO controller found");
    }

    private int findFreeUnit(VirtualDevice controller, List<VirtualDevice> devices) {
        // TODO better find the first free slot
        int max = 0;
        for (VirtualDevice dev : devices) {
            if (dev.getControllerKey() != null && controller.getKey() == dev
                    .getControllerKey()) {
                max = Math.max(dev.getUnitNumber(), max);
            }
        }

        return max;
    }

    /**
     * Increments the given unit number. Skips the number 6 which is reserved in scsi. IDE and SIO
     * go up to 2 so it is safe to use this method for all types of controllers.
     *
     * @param unitNumber
     * @return
     */
    private int nextUnitNumber(int unitNumber) {
        if (unitNumber == 6) {
            // unit 7 is reserved
            return 8;
        }
        return unitNumber + 1;
    }

    private VirtualDeviceConfigSpec createHdd(Integer controllerKey, int unitNumber, DiskState ds,
            String diskName, ManagedObjectReference datastore)
            throws FinderException, InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {

        VirtualDiskFlatVer2BackingInfo backing = new VirtualDiskFlatVer2BackingInfo();
        backing.setDiskMode(VirtualDiskMode.PERSISTENT.value());
        backing.setThinProvisioned(true);
        backing.setFileName(diskName);
        backing.setDatastore(datastore);

        VirtualDisk disk = new VirtualDisk();
        disk.setCapacityInKB(toKb(ds.capacityMBytes));
        disk.setBacking(backing);
        disk.setControllerKey(controllerKey);
        disk.setUnitNumber(unitNumber);
        disk.setKey(-1);

        VirtualDeviceConfigSpec change = new VirtualDeviceConfigSpec();
        change.setDevice(disk);
        change.setOperation(VirtualDeviceConfigSpecOperation.ADD);
        change.setFileOperation(VirtualDeviceConfigSpecFileOperation.CREATE);

        return change;
    }

    private VirtualDeviceConfigSpec resizeHdd(VirtualDisk sysdisk, DiskState ds)
            throws FinderException, InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {

        VirtualDiskFlatVer2BackingInfo oldbacking = (VirtualDiskFlatVer2BackingInfo) sysdisk
                .getBacking();
        VirtualDiskFlatVer2BackingInfo backing = new VirtualDiskFlatVer2BackingInfo();
        backing.setDiskMode(oldbacking.getDiskMode());
        backing.setThinProvisioned(true);
        backing.setFileName(oldbacking.getFileName());

        VirtualDisk disk = new VirtualDisk();
        disk.setCapacityInKB(toKb(ds.capacityMBytes));
        disk.setBacking(backing);
        disk.setControllerKey(sysdisk.getControllerKey());
        disk.setUnitNumber(sysdisk.getUnitNumber());
        disk.setKey(sysdisk.getKey());

        VirtualDeviceConfigSpec change = new VirtualDeviceConfigSpec();
        change.setDevice(disk);
        change.setOperation(VirtualDeviceConfigSpecOperation.EDIT);

        return change;
    }

    private String makePathToVmdkFile(String name, String dir) {
        String diskName = Paths.get(dir, name).toString();
        if (!diskName.endsWith(".vmdk")) {
            diskName += ".vmdk";
        }
        return diskName;
    }

    private VirtualIDEController getFirstIdeController(ArrayOfVirtualDevice devices) {
        for (VirtualDevice dev : devices.getVirtualDevice()) {
            if (dev instanceof VirtualIDEController) {
                return (VirtualIDEController) dev;
            }
        }

        throw new IllegalStateException("No IDE controller found");
    }

    private VirtualPCIController getFirstPciController(ArrayOfVirtualDevice devices) {
        for (VirtualDevice dev : devices.getVirtualDevice()) {
            if (dev instanceof VirtualPCIController) {
                return (VirtualPCIController) dev;
            }
        }

        return null;
    }

    private VirtualSCSIController getFirstScsiController(ArrayOfVirtualDevice devices) {
        for (VirtualDevice dev : devices.getVirtualDevice()) {
            if (dev instanceof VirtualSCSIController) {
                return (VirtualSCSIController) dev;
            }
        }

        throw new IllegalStateException("No SCSI controller found");
    }

    /**
     * Once a vm is provisioned this method collects vsphere-assigned properties and stores them in
     * the {@link ComputeState#customProperties}
     *
     * @param state
     * @throws InvalidPropertyFaultMsg
     * @throws RuntimeFaultFaultMsg
     */
    public VmOverlay enrichStateFromVm(ComputeState state)
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        Map<String, Object> props = this.get.entityProps(this.vm,
                VimPath.vm_config_instanceUuid,
                VimPath.vm_config_name,
                VimPath.vm_config_hardware_device,
                VimPath.vm_runtime_powerState,
                VimPath.vm_runtime_host,
                VimPath.vm_config_guestId,
                VimPath.vm_guest_net,
                VimPath.vm_summary_guest_ipAddress,
                VimPath.vm_summary_guest_hostName);

        VmOverlay overlay = new VmOverlay(this.vm, props);
        state.id = overlay.getInstanceUuid();
        state.primaryMAC = overlay.getPrimaryMac();
        state.powerState = overlay.getPowerState();
        state.address = overlay.guessPublicIpV4Address();
        state.name = overlay.getName();

        CustomProperties.of(state)
                .put(CustomProperties.MOREF, this.vm)
                .put(CustomProperties.TYPE, VimNames.TYPE_VM);

        return overlay;
    }

    /**
     * Creates a VM in vsphere. This method will block until the CreateVM_Task completes. The path
     * to the .vmx file is explicitly set and its existence is iterpreted as if the VM has been
     * successfully created and returns null.
     *
     * @return
     * @throws FinderException
     * @throws Exception
     */
    private ManagedObjectReference createVm() throws Exception {
        ManagedObjectReference folder = getVmFolder();
        ManagedObjectReference datastore = getDatastore();
        ManagedObjectReference resourcePool = getResourcePool();
        ManagedObjectReference host = getHost();

        String datastoreName = this.get.entityProp(datastore, "name");
        VirtualMachineConfigSpec spec = buildVirtualMachineConfigSpec(datastoreName);

        String gt = CustomProperties.of(this.state).getString(CustomProperties.GUEST_ID, null);
        if (gt != null) {
            try {
                gt = VirtualMachineGuestOsIdentifier.valueOf(gt).value();
            } catch (IllegalArgumentException e) {
                // silently default to generic 64 bit guest.
                gt = DEFAULT_GUEST_ID.value();
            }

            spec.setGuestId(gt);
        }

        populateCloudConfig(spec, null);
        ManagedObjectReference vmTask = getVimPort().createVMTask(folder, spec, resourcePool, host);

        TaskInfo info = waitTaskEnd(vmTask);

        if (info.getState() == TaskInfoState.ERROR) {
            MethodFault fault = info.getError().getFault();
            if (fault instanceof FileAlreadyExists) {
                // a .vmx file already exists, assume someone won the race to create the vm
                return null;
            } else {
                return VimUtils.rethrow(info.getError());
            }
        }

        return (ManagedObjectReference) info.getResult();
    }

    private boolean populateVAppProperties(VirtualMachineConfigSpec spec,
            ArrayOfVAppPropertyInfo currentProps) {
        if (this.disks == null || this.disks.size() == 0) {
            return false;
        }

        DiskState bootDisk = findBootDisk();

        if (bootDisk == null) {
            return false;
        }

        boolean customizationsApplied = false;
        int nextKey = 1;
        if (currentProps != null) {
            nextKey = currentProps.getVAppPropertyInfo().stream()
                    .mapToInt(VAppPropertyInfo::getKey)
                    .max()
                    .orElse(1);
        }

        String ovfEnv = getFileItemByPath(bootDisk, OVF_PROPERTY_ENV);
        if (ovfEnv != null) {
            @SuppressWarnings("unchecked")
            Map<String, String> map = Utils.fromJson(ovfEnv, Map.class);
            if (!map.isEmpty()) {
                customizationsApplied = true;
                VmConfigSpec configSpec = new VmConfigSpec();
                configSpec.getOvfEnvironmentTransport().add(OvfDeployer.TRANSPORT_ISO);
                if (currentProps == null) {
                    currentProps = new ArrayOfVAppPropertyInfo();
                }

                currentProps.getVAppPropertyInfo().forEach(pi -> {
                    if (map.containsKey(pi.getId())) {
                        VAppPropertySpec ps = new VAppPropertySpec();
                        ps.setOperation(ArrayUpdateOperation.EDIT);
                        pi.setValue(map.remove(pi.getId()));

                        ps.setInfo(pi);
                        configSpec.getProperty().add(ps);
                    }
                });

                // only new key/values
                for (Entry<String, String> entry : map.entrySet()) {
                    VAppPropertyInfo pi = new VAppPropertyInfo();
                    pi.setId(entry.getKey());
                    pi.setType("string");
                    pi.setKey(nextKey++);
                    pi.setValue(entry.getValue());

                    VAppPropertySpec ps = new VAppPropertySpec();
                    ps.setOperation(ArrayUpdateOperation.ADD);
                    ps.setInfo(pi);
                    configSpec.getProperty().add(ps);
                }
                spec.setVAppConfig(configSpec);
            }
        }
        return customizationsApplied;
    }

    /**
     * Puts the cloud-config user data in the OVF environment
     *
     * @param spec
     * @param currentProps
     */
    private boolean populateCloudConfig(VirtualMachineConfigSpec spec,
            ArrayOfVAppPropertyInfo currentProps) {
        if (this.disks == null || this.disks.size() == 0) {
            return false;
        }

        DiskState bootDisk = findBootDisk();

        if (bootDisk == null) {
            return false;
        }

        boolean customizationsApplied = false;
        int nextKey = 1;
        if (currentProps != null) {
            nextKey = currentProps.getVAppPropertyInfo().stream()
                    .mapToInt(VAppPropertyInfo::getKey)
                    .max()
                    .orElse(1);
        }

        VmConfigSpec configSpec = new VmConfigSpec();
        configSpec.getOvfEnvironmentTransport().add(OvfDeployer.TRANSPORT_ISO);

        String cloudConfig = getFileItemByPath(bootDisk, CLOUD_CONFIG_PROPERTY_USER_DATA);
        if (cloudConfig != null) {
            VAppPropertySpec propertySpec = new VAppPropertySpec();

            VAppPropertyInfo userDataInfo = null;
            if (currentProps != null) {
                userDataInfo = currentProps.getVAppPropertyInfo().stream()
                        .filter(p -> p.getId().equals(CLOUD_CONFIG_PROPERTY_USER_DATA))
                        .findFirst()
                        .orElse(null);
                if (userDataInfo == null) {
                    // try coreOS key
                    userDataInfo = currentProps.getVAppPropertyInfo().stream()
                            .filter(p -> p.getId().equals(COREOS_CLOUD_CONFIG_PROPERTY_USER_DATA))
                            .findFirst()
                            .orElse(null);
                    if (userDataInfo != null) {
                        VAppPropertyInfo coreosEncoding = currentProps.getVAppPropertyInfo()
                                .stream()
                                .filter(p -> p.getId()
                                        .equals(COREOS_CLOUD_CONFIG_PROPERTY_USER_DATA_ENCODING))
                                .findFirst().orElse(null);
                        if (coreosEncoding != null) {
                            VAppPropertySpec pSpec = new VAppPropertySpec();
                            coreosEncoding.setValue(CLOUD_CONFIG_BASE64_ENCODING);
                            pSpec.setOperation(ArrayUpdateOperation.EDIT);
                            pSpec.setInfo(coreosEncoding);
                            configSpec.getProperty().add(pSpec);
                        }
                    }
                }
            }

            if (userDataInfo != null) {
                propertySpec.setOperation(ArrayUpdateOperation.EDIT);
            } else {
                userDataInfo = new VAppPropertyInfo();
                userDataInfo.setId(CLOUD_CONFIG_PROPERTY_USER_DATA);
                userDataInfo.setType("string");
                userDataInfo.setKey(nextKey++);
                propertySpec.setOperation(ArrayUpdateOperation.ADD);
            }
            String encodedUserData = Base64.getEncoder().encodeToString(cloudConfig.getBytes());
            userDataInfo.setValue(encodedUserData);

            propertySpec.setInfo(userDataInfo);
            configSpec.getProperty().add(propertySpec);
            customizationsApplied = true;
        }

        String publicKeys = getFileItemByPath(bootDisk, CLOUD_CONFIG_PROPERTY_PUBLIC_KEYS);
        if (publicKeys != null) {
            VAppPropertySpec propertySpec = new VAppPropertySpec();

            VAppPropertyInfo sshKeyInfo = null;
            if (currentProps != null) {
                sshKeyInfo = currentProps.getVAppPropertyInfo().stream()
                        .filter(p -> p.getId().equals(CLOUD_CONFIG_PROPERTY_PUBLIC_KEYS))
                        .findFirst()
                        .orElse(null);
            }
            if (sshKeyInfo != null) {
                propertySpec.setOperation(ArrayUpdateOperation.EDIT);
            } else {
                sshKeyInfo = new VAppPropertyInfo();
                sshKeyInfo.setType("string");
                sshKeyInfo.setId(CLOUD_CONFIG_PROPERTY_PUBLIC_KEYS);
                sshKeyInfo.setKey(nextKey++);
                propertySpec.setOperation(ArrayUpdateOperation.ADD);
            }
            sshKeyInfo.setValue(publicKeys);

            propertySpec.setInfo(sshKeyInfo);
            configSpec.getProperty().add(propertySpec);
            customizationsApplied = true;
        }

        String hostname = getFileItemByPath(bootDisk, CLOUD_CONFIG_PROPERTY_HOSTNAME);
        if (hostname != null) {
            VAppPropertySpec propertySpec = new VAppPropertySpec();

            VAppPropertyInfo hostInfo = null;
            if (currentProps != null) {
                hostInfo = currentProps.getVAppPropertyInfo().stream()
                        .filter(p -> p.getId().equals(CLOUD_CONFIG_PROPERTY_HOSTNAME))
                        .findFirst()
                        .orElse(null);
                if (hostInfo == null) {
                    // try coreOS key
                    hostInfo = currentProps.getVAppPropertyInfo().stream()
                            .filter(p -> p.getId().equals(COREOS_CLOUD_CONFIG_PROPERTY_HOSTNAME))
                            .findFirst()
                            .orElse(null);
                }
            }

            if (hostInfo != null) {
                propertySpec.setOperation(ArrayUpdateOperation.EDIT);
            } else {
                hostInfo = new VAppPropertyInfo();
                hostInfo.setId(CLOUD_CONFIG_PROPERTY_USER_DATA);
                hostInfo.setType("string");
                hostInfo.setKey(nextKey++);
                propertySpec.setOperation(ArrayUpdateOperation.ADD);
            }
            hostInfo.setValue(hostname);

            propertySpec.setInfo(hostInfo);
            configSpec.getProperty().add(propertySpec);
            customizationsApplied = true;
        }

        if (customizationsApplied) {
            spec.setVAppConfig(configSpec);
        }

        return customizationsApplied;
    }

    private String getFileItemByPath(DiskState bootDisk, String fileName) {
        if (bootDisk != null && bootDisk.bootConfig != null && bootDisk.bootConfig.files != null) {
            for (FileEntry e : bootDisk.bootConfig.files) {
                if (Objects.equals(fileName, e.path)) {
                    return e.contents;
                }
            }
        }

        return null;
    }

    /**
     * Decides in which folder to put the newly created vm.
     *
     * @return
     * @throws InvalidPropertyFaultMsg
     * @throws RuntimeFaultFaultMsg
     * @throws FinderException
     */
    private ManagedObjectReference getVmFolder()
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg, FinderException {

        // look for a configured folder in compute state
        String folderPath = CustomProperties.of(this.state)
                .getString(RESOURCE_GROUP_NAME);

        if (folderPath == null) {
            // look for a configured folder in parent
            folderPath = CustomProperties.of(this.parent)
                    .getString(RESOURCE_GROUP_NAME);
        }

        if (folderPath == null) {
            return this.finder.vmFolder().object;
        } else {
            return this.finder.folder(folderPath).object;
        }
    }

    /**
     * Creates a spec used to create the VM.
     *
     * @param datastoreName
     * @return
     * @throws InvalidPropertyFaultMsg
     * @throws FinderException
     * @throws RuntimeFaultFaultMsg
     */
    private VirtualMachineConfigSpec buildVirtualMachineConfigSpec(String datastoreName)
            throws InvalidPropertyFaultMsg, FinderException, RuntimeFaultFaultMsg {
        String displayName = this.state.name;

        VirtualMachineConfigSpec spec = new VirtualMachineConfigSpec();
        spec.setName(displayName);
        spec.setNumCPUs((int) this.state.description.cpuCount);
        spec.setGuestId(VirtualMachineGuestOsIdentifier.OTHER_GUEST_64.value());
        spec.setMemoryMB(toMb(this.state.description.totalMemoryBytes));

        VirtualMachineFileInfo files = new VirtualMachineFileInfo();
        // Use a full path to the config file to avoid creating a VM with the same name
        String path = String.format("[%s] %s/%s.vmx", datastoreName, displayName, displayName);
        files.setVmPathName(path);
        spec.setFiles(files);

        for (NetworkInterfaceStateWithDetails ni : this.nics) {
            VirtualDevice nic = createNic(ni, null);
            addDeviceToVm(spec, nic);
        }

        VirtualDevice scsi = createScsiController();
        addDeviceToVm(spec, scsi);

        return spec;
    }

    private void addDeviceToVm(VirtualMachineConfigSpec spec, VirtualDevice dev) {
        VirtualDeviceConfigSpec change = new VirtualDeviceConfigSpec();
        change.setDevice(dev);
        change.setOperation(VirtualDeviceConfigSpecOperation.ADD);
        spec.getDeviceChange().add(change);
    }

    private void addRemoveDeviceFromVm(VirtualMachineConfigSpec spec, VirtualDevice dev) {
        VirtualDeviceConfigSpec change = new VirtualDeviceConfigSpec();
        change.setDevice(dev);
        change.setOperation(VirtualDeviceConfigSpecOperation.REMOVE);
        spec.getDeviceChange().add(change);
    }

    private VirtualDevice createScsiController() {
        VirtualLsiLogicController scsiCtrl = new VirtualLsiLogicController();
        scsiCtrl.setBusNumber(0);
        scsiCtrl.setKey(-1);
        scsiCtrl.setSharedBus(VirtualSCSISharing.NO_SHARING);

        return scsiCtrl;
    }

    private VirtualEthernetCard createNic(NetworkInterfaceStateWithDetails nicWithDetails,
            Integer controllerKey)
            throws FinderException, InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        VirtualEthernetCard nic = new VirtualE1000();
        nic.setAddressType(VirtualEthernetCardMacType.GENERATED.value());
        nic.setKey(-1);
        nic.setControllerKey(controllerKey);

        if (nicWithDetails.subnet != null) {
            // check if it is portgroup
            CustomProperties props = CustomProperties.of(nicWithDetails.subnet);
            if (!StringUtil.isNullOrEmpty(props.getString(DvsProperties.DVS_UUID))) {
                DistributedVirtualSwitchPortConnection port = new DistributedVirtualSwitchPortConnection();
                port.setSwitchUuid(props.getString(DvsProperties.DVS_UUID));
                port.setPortgroupKey(props.getString(DvsProperties.PORT_GROUP_KEY));

                VirtualEthernetCardDistributedVirtualPortBackingInfo backing = new VirtualEthernetCardDistributedVirtualPortBackingInfo();
                backing.setPort(port);
                nic.setBacking(backing);
            } else {
                // NSX-T logical switch
                VirtualEthernetCardOpaqueNetworkBackingInfo backing = new VirtualEthernetCardOpaqueNetworkBackingInfo();
                backing.setOpaqueNetworkId(nicWithDetails.subnet.id);
                backing.setOpaqueNetworkType(NsxProperties.NSX_LOGICAL_SWITCH);
                nic.setBacking(backing);
            }
        } else {
            // either network or OpaqueNetwork
            CustomProperties custProp = CustomProperties.of(nicWithDetails.network);
            if (VimNames.TYPE_OPAQUE_NETWORK
                    .equals(custProp.getString(CustomProperties.TYPE, null))) {
                // opaque network
                VirtualEthernetCardOpaqueNetworkBackingInfo backing = new VirtualEthernetCardOpaqueNetworkBackingInfo();
                backing.setOpaqueNetworkId(custProp.getString(NsxProperties.OPAQUE_NET_ID));
                backing.setOpaqueNetworkType(custProp.getString(NsxProperties.OPAQUE_NET_TYPE));
                nic.setBacking(backing);
            } else {
                // network
                VirtualEthernetCardNetworkBackingInfo backing = new VirtualEthernetCardNetworkBackingInfo();
                backing.setDeviceName(nicWithDetails.network.name);
                nic.setBacking(backing);
            }
        }

        return nic;
    }

    private Long toMb(long bytes) {
        return bytes / 1024 / 1024;
    }

    private Long toKb(long mb) {
        return mb * 1024;
    }

    /**
     * Finds the datastore to use for the VM from the ComputeState.description.datastoreId.
     *
     * @return
     * @throws RuntimeFaultFaultMsg
     * @throws InvalidPropertyFaultMsg
     * @throws FinderException
     */
    private ManagedObjectReference getDatastore()
            throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg, FinderException {
        if (this.datastore != null) {
            return this.datastore;
        }

        String datastorePath = this.state.description.dataStoreId;

        if (datastorePath == null) {
            ArrayOfManagedObjectReference datastores = findDatastoresForPlacement(
                    this.placementTarget);
            if (datastores == null || datastores.getManagedObjectReference().isEmpty()) {
                this.datastore = this.finder.defaultDatastore().object;
            } else {
                this.datastore = datastores.getManagedObjectReference().get(0);
            }
        } else {
            this.datastore = this.finder.datastore(datastorePath).object;
        }

        return this.datastore;
    }

    private ArrayOfManagedObjectReference findDatastoresForPlacement(ManagedObjectReference target)
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        if (VimNames.TYPE_RESOURCE_POOL.equals(target.getType())) {
            ManagedObjectReference owner = this.get.entityProp(target, VimNames.PROPERTY_OWNER);
            return findDatastoresForPlacement(owner);
        }
        // at this point a target is either host or ComputeResource: both have a property
        // "datastore"
        return this.get.entityProp(target, VimPath.res_datastore);
    }

    public ManagedObjectReference getResourcePool()
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        if (this.resourcePool != null) {
            return this.resourcePool;
        }

        if (VimNames.TYPE_HOST.equals(this.placementTarget.getType())) {
            // find the ComputeResource representing this host and use its root resource pool
            ManagedObjectReference parentCompute = this.get.entityProp(this.placementTarget,
                    VimPath.host_parent);
            this.resourcePool = this.get.entityProp(parentCompute, VimPath.res_resourcePool);
        } else if (VimNames.TYPE_CLUSTER_COMPUTE_RESOURCE.equals(this.placementTarget.getType()) ||
                VimNames.TYPE_COMPUTE_RESOURCE.equals(this.placementTarget.getType())) {
            // place in the root resource pool of a cluster
            this.resourcePool = this.get.entityProp(this.placementTarget, VimPath.res_resourcePool);
        } else if (VimNames.TYPE_RESOURCE_POOL.equals(this.placementTarget.getType())) {
            // place in the resource pool itself
            this.resourcePool = this.placementTarget;
        } else {
            throw new IllegalArgumentException("Cannot place instance on " +
                    VimUtils.convertMoRefToString(this.placementTarget));
        }

        return this.resourcePool;
    }

    public ManagedObjectReference getHost()
            throws InvalidPropertyFaultMsg, RuntimeFaultFaultMsg {
        if (this.host != null) {
            return this.host;
        }

        if (VimNames.TYPE_HOST.equals(this.placementTarget.getType())) {
            this.host = this.placementTarget;
        }

        return this.host;
    }

    public ManagedObjectReference getVm() {
        return this.vm;
    }

    public static class ClientException extends Exception {
        private static final long serialVersionUID = 1L;

        public ClientException(String message, Throwable cause) {
            super(message, cause);
        }

        public ClientException(Throwable cause) {
            super(cause);
        }

        public ClientException(String message) {
            super(message);
        }
    }
}
