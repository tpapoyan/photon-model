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

package com.vmware.photon.controller.model.adapters.awsadapter;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static com.vmware.photon.controller.model.adapters.awsadapter.TestUtils.getExecutor;
import static com.vmware.photon.controller.model.tasks.ProvisionSecurityGroupTaskService.NETWORK_STATE_ID_PROP_NAME;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Vpc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vmware.photon.controller.model.PhotonModelMetricServices;
import com.vmware.photon.controller.model.PhotonModelServices;
import com.vmware.photon.controller.model.adapterapi.SecurityGroupInstanceRequest;
import com.vmware.photon.controller.model.adapters.awsadapter.util.AWSNetworkClient;
import com.vmware.photon.controller.model.adapters.awsadapter.util.AWSSecurityGroupClient;
import com.vmware.photon.controller.model.resources.ResourcePoolService.ResourcePoolState;
import com.vmware.photon.controller.model.resources.SecurityGroupService.SecurityGroupState;
import com.vmware.photon.controller.model.resources.SecurityGroupService.SecurityGroupState.Rule;
import com.vmware.photon.controller.model.tasks.PhotonModelTaskServices;
import com.vmware.photon.controller.model.tasks.ProvisionSecurityGroupTaskService;
import com.vmware.photon.controller.model.tasks.ProvisionSecurityGroupTaskService.ProvisionSecurityGroupTaskState;
import com.vmware.xenon.common.CommandLineArgumentParser;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.ServiceHost;
import com.vmware.xenon.common.ServiceHost.ServiceNotFoundException;
import com.vmware.xenon.common.TaskState;
import com.vmware.xenon.common.UriUtils;
import com.vmware.xenon.common.Utils;
import com.vmware.xenon.common.test.VerificationHost;
import com.vmware.xenon.services.common.AuthCredentialsService.AuthCredentialsServiceState;
import com.vmware.xenon.services.common.TenantService;

public class TestProvisionAWSSecurityGroup {

    /*
     * This test requires the following 4 command line variables. If they are not present the tests
     * will be ignored. Pass them into the test with the -Dxenon.variable=value syntax i.e
     * -Dxenon.region="us-east-1"
     *
     * privateKey & privateKeyId are credentials to an AWS VPC account region is the ec2 region
     * where the tests should be run (us-east-1), vpcId is the id of one of the VPCs
     */
    public String privateKey;
    public String privateKeyId;
    public String region;
    public String vpcId;

    private VerificationHost host;
    private URI provisionSecurityGroupFactory;
    private AWSNetworkClient netClient;
    private Vpc vpc;

    @Before
    public void setUp() throws Exception {
        CommandLineArgumentParser.parseFromProperties(this);

        // ignore if any of the required properties are missing
        org.junit.Assume.assumeTrue(
                TestUtils.isNull(this.privateKey, this.privateKeyId, this.region, this.vpcId));
        this.host = VerificationHost.create(0);
        try {
            this.host.start();
            PhotonModelServices.startServices(this.host);
            PhotonModelMetricServices.startServices(this.host);
            PhotonModelTaskServices.startServices(this.host);
            // start the aws fw service
            this.host.startService(
                    Operation.createPost(UriUtils.buildUri(this.host, AWSSecurityGroupService.class)),
                    new AWSSecurityGroupService());

            this.provisionSecurityGroupFactory = UriUtils.buildUri(this.host,
                    ProvisionSecurityGroupTaskService.FACTORY_LINK);

            this.netClient = new AWSNetworkClient(
                    TestUtils.getClient(this.privateKeyId, this.privateKey, this.region, false));

            this.vpc = this.netClient.getVPC(this.vpcId);
            assertNotNull(this.vpc);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    @After
    public void tearDown() throws InterruptedException {
        if (this.host == null) {
            return;
        }
        this.host.tearDownInProcessPeers();
        this.host.toggleNegativeTestMode(false);
        this.host.tearDown();
    }

    @Test
    public void testProvisionAWSSecurityGroup() throws Throwable {
        // create credentials
        Operation authResponse = new Operation();
        TestUtils.postCredentials(this.host, authResponse, this.privateKey, this.privateKeyId);
        AuthCredentialsServiceState creds = authResponse.getBody(AuthCredentialsServiceState.class);

        // create resource pool
        Operation poolResponse = new Operation();
        TestUtils.postResourcePool(this.host, poolResponse);
        ResourcePoolState pool = poolResponse.getBody(ResourcePoolState.class);

        // create sg service
        Operation securityGroupResponse = new Operation();
        SecurityGroupState initialSecurityGroupState = buildSecurityGroupState(creds, pool);

        TestUtils.postSecurityGroup(this.host, initialSecurityGroupState, securityGroupResponse);
        SecurityGroupState securityGroupState = securityGroupResponse.getBody(SecurityGroupState
                .class);

        // set up security group task state
        ProvisionSecurityGroupTaskState task = new ProvisionSecurityGroupTaskState();
        task.requestType = SecurityGroupInstanceRequest.InstanceRequestType.CREATE;
        task.securityGroupDescriptionLinks = Stream.of(securityGroupState.documentSelfLink)
                .collect(Collectors.toSet());
        task.customProperties = new HashMap<>();
        task.customProperties.put(NETWORK_STATE_ID_PROP_NAME, this.vpcId);

        Operation provision = new Operation();
        provisionSecurityGroup(task, provision);
        ProvisionSecurityGroupTaskState ps = provision.getBody(ProvisionSecurityGroupTaskState.class);
        waitForTaskCompletion(this.host, UriUtils.buildUri(this.host, ps.documentSelfLink));
        validateAWSArtifacts(securityGroupState.documentSelfLink, creds);

        // reuse previous task, but switch to a delete
        task.requestType = SecurityGroupInstanceRequest.InstanceRequestType.DELETE;
        Operation remove = new Operation();
        provisionSecurityGroup(task, remove);
        ProvisionSecurityGroupTaskState removeTask = remove.getBody(ProvisionSecurityGroupTaskState.class);
        waitForTaskCompletion(this.host, UriUtils.buildUri(this.host, removeTask.documentSelfLink));

        // verify security group state is gone
        try {
            getSecurityGroupState(securityGroupState.documentSelfLink);
        } catch (Exception ex) {
            assertTrue(ex instanceof ServiceNotFoundException);
        }
    }

    @Test
    public void testProvisionAWSSecurityGroupPartialFailure() throws Throwable {
        // create credentials
        Operation authResponse = new Operation();
        TestUtils.postCredentials(this.host, authResponse, this.privateKey, this.privateKeyId);
        AuthCredentialsServiceState creds = authResponse.getBody(AuthCredentialsServiceState.class);

        // create resource pool
        Operation poolResponse = new Operation();
        TestUtils.postResourcePool(this.host, poolResponse);
        ResourcePoolState pool = poolResponse.getBody(ResourcePoolState.class);

        // create two security groups
        Operation securityGroupResponse = new Operation();
        SecurityGroupState initialSecurityGroupState = buildSecurityGroupState(creds, pool);

        TestUtils.postSecurityGroup(this.host, initialSecurityGroupState, securityGroupResponse);
        SecurityGroupState securityGroupState1 = securityGroupResponse.getBody(SecurityGroupState
                .class);
        initialSecurityGroupState = buildSecurityGroupState(creds, pool);

        TestUtils.postSecurityGroup(this.host, initialSecurityGroupState, securityGroupResponse);
        SecurityGroupState securityGroupState2 = securityGroupResponse.getBody(SecurityGroupState
                .class);

        // delete the second security group to simulate failure
        TestUtils.deleteSecurityGroup(this.host, securityGroupState2.documentSelfLink);
        // verify the second security group is gone
        try {
            getSecurityGroupState(securityGroupState2.documentSelfLink);
        } catch (Exception ex) {
            assertTrue(ex instanceof ServiceNotFoundException);
        }

        // set up security group task state
        ProvisionSecurityGroupTaskState task = new ProvisionSecurityGroupTaskState();
        task.requestType = SecurityGroupInstanceRequest.InstanceRequestType.CREATE;
        task.securityGroupDescriptionLinks = Stream.of(securityGroupState1.documentSelfLink,
                securityGroupState2.documentSourceLink).collect(Collectors.toSet());
        task.customProperties = new HashMap<>();
        task.customProperties.put(NETWORK_STATE_ID_PROP_NAME, this.vpcId);

        Operation provision = new Operation();
        provisionSecurityGroup(task, provision);
        ProvisionSecurityGroupTaskState ps = provision.getBody(ProvisionSecurityGroupTaskState.class);
        waitForTaskFailure(this.host, UriUtils.buildUri(this.host, ps.documentSelfLink));
        validateAWSArtifacts(securityGroupState1.documentSelfLink, creds);

        // validate that the second security group was not created
        assertNull(getAWSSecurityGroup(securityGroupState2.name, creds));

        // reuse previous task, but switch to a delete
        task.requestType = SecurityGroupInstanceRequest.InstanceRequestType.DELETE;
        Operation remove = new Operation();
        provisionSecurityGroup(task, remove);
        ProvisionSecurityGroupTaskState removeTask = remove.getBody(ProvisionSecurityGroupTaskState.class);
        waitForTaskFailure(this.host, UriUtils.buildUri(this.host, removeTask.documentSelfLink));

        // verify security group state is gone
        try {
            getSecurityGroupState(securityGroupState1.documentSelfLink);
        } catch (Exception ex) {
            assertTrue(ex instanceof ServiceNotFoundException);
        }
    }

    @Test
    public void testInvalidAuthAWSSecurityGroup() throws Throwable {
        // create credentials
        Operation authResponse = new Operation();
        TestUtils.postCredentials(this.host, authResponse, this.privateKey, "invalid");
        AuthCredentialsServiceState creds = authResponse.getBody(AuthCredentialsServiceState.class);

        // create resource pool
        Operation poolResponse = new Operation();
        TestUtils.postResourcePool(this.host, poolResponse);
        ResourcePoolState pool = poolResponse.getBody(ResourcePoolState.class);

        // create sq service
        Operation securityGroupResponse = new Operation();
        SecurityGroupState securityGroupInitialState = buildSecurityGroupState(creds, pool);

        TestUtils.postSecurityGroup(this.host, securityGroupInitialState, securityGroupResponse);
        SecurityGroupState securityGroupState = securityGroupResponse.getBody(SecurityGroupState.class);

        // set up security group task state
        ProvisionSecurityGroupTaskState task = new ProvisionSecurityGroupTaskState();
        task.requestType = SecurityGroupInstanceRequest.InstanceRequestType.CREATE;
        task.securityGroupDescriptionLinks = Stream.of(securityGroupState.documentSelfLink)
                .collect(Collectors.toSet());
        task.customProperties = new HashMap<>();
        task.customProperties.put(NETWORK_STATE_ID_PROP_NAME, this.vpcId);

        Operation provision = new Operation();
        provisionSecurityGroup(task, provision);
        ProvisionSecurityGroupTaskState ps = provision.getBody(ProvisionSecurityGroupTaskState.class);
        waitForTaskFailure(this.host, UriUtils.buildUri(this.host, ps.documentSelfLink));

    }

    private void validateAWSArtifacts(String securityGroupDescriptionLink,
            AuthCredentialsServiceState creds) throws Throwable {

        SecurityGroupState securityGroup = getSecurityGroupState(securityGroupDescriptionLink);

        AWSSecurityGroupClient client = new AWSSecurityGroupClient(
                AWSUtils.getAsyncClient(creds, this.region, getExecutor()));
        // if any artifact is not present then an error will be thrown
        SecurityGroup sg = client.getSecurityGroupById(
                securityGroup.customProperties.get(AWSSecurityGroupService.SECURITY_GROUP_ID));
        assertNotNull(sg);
        assertNotNull(sg.getIpPermissions());
        assertTrue(sg.getIpPermissions().size() == 1);
        assertNotNull(sg.getIpPermissionsEgress());
        // there are two egress rules (one that was added as part of this test, and the default one)
        assertTrue(sg.getIpPermissionsEgress().size() == 2);
    }

    private SecurityGroup getAWSSecurityGroup(String name, AuthCredentialsServiceState creds)
            throws Throwable {

        AWSSecurityGroupClient client = new AWSSecurityGroupClient(
                AWSUtils.getAsyncClient(creds, this.region, getExecutor()));
        // if any artifact is not present then an error will be thrown
        return client.getSecurityGroup(name, this.vpcId);
    }

    private SecurityGroupState getSecurityGroupState(String securityGroupLink) throws Throwable {
        Operation response = new Operation();
        getSecurityGroupState(securityGroupLink, response);
        return response.getBody(SecurityGroupState.class);
    }

    private void provisionSecurityGroup(ProvisionSecurityGroupTaskState ps, Operation response)
            throws Throwable {
        this.host.testStart(1);
        Operation startPost = Operation.createPost(this.provisionSecurityGroupFactory)
                .setBody(ps)
                .setCompletion((o, e) -> {
                    if (e != null) {
                        this.host.failIteration(e);
                        return;
                    }
                    response.setBody(o.getBody(ProvisionSecurityGroupTaskState.class));
                    this.host.completeIteration();
                });
        this.host.send(startPost);
        this.host.testWait();

    }

    private void getSecurityGroupState(String securityGroupLink, Operation response) throws Throwable {

        this.host.testStart(1);
        URI securityGroupURI = UriUtils.buildUri(this.host, securityGroupLink);
        Operation startGet = Operation.createGet(securityGroupURI)
                .setCompletion((o, e) -> {
                    if (e != null) {
                        this.host.failIteration(e);
                        return;
                    }
                    response.setBody(o.getBody(SecurityGroupState.class));
                    this.host.completeIteration();
                });
        this.host.send(startGet);
        this.host.testWait();

    }

    private SecurityGroupState buildSecurityGroupState(AuthCredentialsServiceState creds,
            ResourcePoolState pool) {
        URI tenantFactoryURI = UriUtils.buildFactoryUri(this.host, TenantService.class);
        SecurityGroupState securityGroup = new SecurityGroupState();
        securityGroup.id = UUID.randomUUID().toString();
        securityGroup.name = "test-sg-" + securityGroup.id;

        securityGroup.tenantLinks = new ArrayList<>();
        securityGroup.tenantLinks.add(UriUtils.buildUriPath(tenantFactoryURI.getPath(), "tenantA"));
        securityGroup.ingress = getGlobalSSHRule();
        securityGroup.egress = getGlobalSSHRule();
        securityGroup.egress.get(0).ipRangeCidr = this.vpc.getCidrBlock();
        securityGroup.authCredentialsLink = creds.documentSelfLink;
        securityGroup.resourcePoolLink = pool.documentSelfLink;
        securityGroup.regionId = this.region;
        securityGroup.instanceAdapterReference = UriUtils.buildUri(ServiceHost.LOCAL_HOST,
                this.host.getPort(),
                AWSUriPaths.AWS_SECURITY_GROUP_ADAPTER,
                null);

        return securityGroup;
    }

    public static void waitForTaskCompletion(VerificationHost host, URI provisioningTaskUri)
            throws Throwable {

        Date expiration = host.getTestExpiration();

        ProvisionSecurityGroupTaskState provisioningTask;

        do {
            provisioningTask = host.getServiceState(null,
                    ProvisionSecurityGroupTaskState.class,
                    provisioningTaskUri);

            if (provisioningTask.taskInfo.stage == TaskState.TaskStage.FAILED) {
                throw new IllegalStateException(
                        "Task failed:" + Utils.toJsonHtml(provisioningTask));
            }

            if (provisioningTask.taskInfo.stage == TaskState.TaskStage.FINISHED) {
                return;
            }

            Thread.sleep(1000);
        } while (new Date().before(expiration));

        host.log("Pending task:\n%s", Utils.toJsonHtml(provisioningTask));

        throw new TimeoutException("Some tasks never finished");
    }

    public static void waitForTaskFailure(VerificationHost host, URI provisioningTaskUri)
            throws Throwable {

        Date expiration = host.getTestExpiration();

        ProvisionSecurityGroupTaskState provisioningTask;

        do {
            provisioningTask = host.getServiceState(null,
                    ProvisionSecurityGroupTaskState.class,
                    provisioningTaskUri);

            if (provisioningTask.taskInfo.stage == TaskState.TaskStage.FAILED) {
                return;
            }

            Thread.sleep(1000);
        } while (new Date().before(expiration));

        host.log("Pending task:\n%s", Utils.toJsonHtml(provisioningTask));

        throw new TimeoutException("Some tasks never finished");
    }

    private static ArrayList<Rule> getGlobalSSHRule() {
        ArrayList<Rule> rules = new ArrayList<>();

        Rule ssh = new Rule();
        ssh.name = "ssh-allow";
        ssh.protocol = "tcp";
        ssh.ipRangeCidr = "0.0.0.0/0";
        ssh.ports = "22";
        rules.add(ssh);

        return rules;
    }

}
