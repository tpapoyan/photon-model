/*
 * Copyright (c) 2015-2017 VMware, Inc. All Rights Reserved.
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

package com.vmware.photon.controller.model.support;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of instance types.
 * The content is endpoint specific but the model is the same.
 */
public class InstanceTypeList {
    public static class InstanceType {
        /**
         * The internal identification used by the endpoint.
         */
        public String id;
        /**
         * Human readable description of the instance type.
         */
        public String name;

        /**
         * Constructs an instance type instance.
         * @param id
         * @param name
         */
        public InstanceType(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    /**
     * List of multiple instance types.
     */
    public List<InstanceType> instanceTypes = new ArrayList<>();

    /**
     * A list of tenant links that can access this instance type list.
     */
    public List<String> tenantLinks;

}