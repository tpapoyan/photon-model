/*
 * Copyright (c) 2017 VMware, Inc. All Rights Reserved.
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

package com.vmware.photon.controller.model.security.util;

import static com.vmware.photon.controller.model.constants.PhotonModelConstants.CUSTOM_PROP_CREDENTIALS_SCOPE;

import java.util.function.Predicate;

import com.vmware.photon.controller.model.constants.PhotonModelConstants.CredentialsScope;
import com.vmware.xenon.common.FactoryService;
import com.vmware.xenon.common.Operation;
import com.vmware.xenon.common.OperationProcessingChain;
import com.vmware.xenon.common.Service;
import com.vmware.xenon.common.Service.Action;
import com.vmware.xenon.services.common.AuthCredentialsService;
import com.vmware.xenon.services.common.AuthCredentialsService.AuthCredentialsServiceState;

/**
 * This processing chain:
 * - Prevents deletion of a {@link AuthCredentialsServiceState} if its in use by a
 * {@link ComputeState}.
 * - Encrypts the private key field of a {@link AuthCredentialsServiceState} when needed if the
 * encryption is enabled.
 */
public class AuthCredentialsOperationProcessingChain extends OperationProcessingChain {

    public static final String CREDENTIALS_IN_USE_MESSAGE = "Credentials are in use";
    public static final String CREDENTIALS_IN_USE_MESSAGE_CODE = "host.credentials.in.use";

    public AuthCredentialsOperationProcessingChain(FactoryService service) {
        super(service);
        this.add(new Predicate<Operation>() {
            @Override
            public boolean test(Operation op) {
                if (op.getAction() == Action.POST) {
                    return handlePatchPostPut(service, op);
                }
                return true;
            }
        });
    }

    public AuthCredentialsOperationProcessingChain(AuthCredentialsService service) {
        super(service);
        this.add(new Predicate<Operation>() {
            @Override
            public boolean test(Operation op) {
                switch (op.getAction()) {
                case DELETE:
                    return handleDelete(service, op, this);
                case PATCH:
                case POST:
                case PUT:
                    return handlePatchPostPut(service, op);
                default:
                    return true;
                }
            }
        });
    }

    protected boolean handlePatchPostPut(Service service, Operation op) {
        AuthCredentialsServiceState body = op.getBody(AuthCredentialsServiceState.class);

        // Credentials with SYSTEM scope need the password in plain text or they can't be used to
        // login into Xenon!
        boolean isSystemScope = (body.customProperties != null)
                && CredentialsScope.SYSTEM.toString().equals(
                        body.customProperties.get(CUSTOM_PROP_CREDENTIALS_SCOPE));

        if (!isSystemScope) {
            body.privateKey = EncryptionUtils.encrypt(body.privateKey);
            op.setBodyNoCloning(body);
        }
        return true;
    }

    protected boolean handleDelete(AuthCredentialsService service, Operation op,
            Predicate<Operation> invokingFilter) {
        // TODO - Prevent deletion of a {@link AuthCredentialsServiceState} if its in use by any
        // state.
        return true;
    }

}
