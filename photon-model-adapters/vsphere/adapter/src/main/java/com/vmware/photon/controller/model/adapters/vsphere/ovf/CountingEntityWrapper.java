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

package com.vmware.photon.controller.model.adapters.vsphere.ovf;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

/**
 * An entity the remembers how many bytes it writes. It advances
 * a LeaseProgressUpdater.
 */
public class CountingEntityWrapper extends HttpEntityWrapper {
    private final LeaseProgressUpdater updater;

    public CountingEntityWrapper(HttpEntity wrappedEntity, LeaseProgressUpdater updater) {
        super(wrappedEntity);
        this.updater = updater;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        super.writeTo(new FilterOutputStream(out) {
            @Override
            public void write(int b) throws IOException {
                this.out.write(b);
                CountingEntityWrapper.this.updater.advance(1);
            }

            @Override
            public void write(byte[] b) throws IOException {
                this.out.write(b);
                CountingEntityWrapper.this.updater.advance(b.length);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                this.out.write(b, off, len);
                CountingEntityWrapper.this.updater.advance(len - off);
            }
        });
    }
}