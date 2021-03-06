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

package com.vmware.photon.controller.model;

import com.vmware.photon.controller.model.monitoring.InMemoryResourceMetricService;
import com.vmware.photon.controller.model.resources.ComputeDescriptionService;
import com.vmware.photon.controller.model.resources.ComputeService;
import com.vmware.photon.controller.model.resources.DiskService;
import com.vmware.photon.controller.model.resources.EndpointService;
import com.vmware.photon.controller.model.resources.FirewallService;
import com.vmware.photon.controller.model.resources.IPAddressService;
import com.vmware.photon.controller.model.resources.ImageService;
import com.vmware.photon.controller.model.resources.LoadBalancerDescriptionService;
import com.vmware.photon.controller.model.resources.LoadBalancerService;
import com.vmware.photon.controller.model.resources.NetworkInterfaceDescriptionService;
import com.vmware.photon.controller.model.resources.NetworkInterfaceService;
import com.vmware.photon.controller.model.resources.NetworkService;
import com.vmware.photon.controller.model.resources.ResourceDescriptionService;
import com.vmware.photon.controller.model.resources.ResourceGroupService;
import com.vmware.photon.controller.model.resources.ResourcePoolService;
import com.vmware.photon.controller.model.resources.SecurityGroupService;
import com.vmware.photon.controller.model.resources.SnapshotService;
import com.vmware.photon.controller.model.resources.StorageDescriptionService;
import com.vmware.photon.controller.model.resources.SubnetRangeService;
import com.vmware.photon.controller.model.resources.SubnetService;
import com.vmware.photon.controller.model.resources.TagFactoryService;
import com.vmware.photon.controller.model.resources.TagService;
import com.vmware.xenon.common.ServiceHost;

/**
 * Helper class that starts all the photon model provisioning services
 */
public class PhotonModelServices {

    public static final String[] LINKS = {
            ComputeDescriptionService.FACTORY_LINK,
            ComputeService.FACTORY_LINK,
            ResourcePoolService.FACTORY_LINK,
            ResourceDescriptionService.FACTORY_LINK,
            DiskService.FACTORY_LINK,
            SnapshotService.FACTORY_LINK,
            NetworkInterfaceService.FACTORY_LINK,
            NetworkInterfaceDescriptionService.FACTORY_LINK,
            ResourceGroupService.FACTORY_LINK,
            NetworkService.FACTORY_LINK,
            SecurityGroupService.FACTORY_LINK,
            FirewallService.FACTORY_LINK,
            StorageDescriptionService.FACTORY_LINK,
            InMemoryResourceMetricService.FACTORY_LINK,
            EndpointService.FACTORY_LINK,
            ImageService.FACTORY_LINK,
            TagService.FACTORY_LINK,
            LoadBalancerDescriptionService.FACTORY_LINK,
            LoadBalancerService.FACTORY_LINK };

    public static void startServices(ServiceHost host) throws Throwable {
        host.startFactory(new ComputeDescriptionService());
        host.startFactory(new ComputeService());
        host.startFactory(new ResourcePoolService());
        host.startFactory(new ResourceDescriptionService());
        host.startFactory(new DiskService());
        host.startFactory(new SnapshotService());
        host.startFactory(new NetworkInterfaceService());
        host.startFactory(new NetworkInterfaceDescriptionService());
        host.startFactory(new SubnetService());
        host.startFactory(new SubnetRangeService());
        host.startFactory(new IPAddressService());
        host.startFactory(new ResourceGroupService());
        host.startFactory(new NetworkService());
        host.startFactory(new FirewallService());
        host.startFactory(new SecurityGroupService());
        host.startFactory(new StorageDescriptionService());
        host.startFactory(new EndpointService());
        host.startFactory(new ImageService());
        host.startFactory(new InMemoryResourceMetricService());
        host.startFactory(TagService.class, TagFactoryService::new);
        host.startFactory(new LoadBalancerDescriptionService());
        host.startFactory(new LoadBalancerService());
    }
}
