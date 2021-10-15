/*
 *  Copyright (c) 2021 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial API and Implementation
 *
 */

package org.eclipse.dataspaceconnector.ids.api.multipart.http;

import de.fraunhofer.iais.eis.Message;

import java.util.Objects;

public class MultipartResponse {

    private final Message header;
    private final Object payload;

    private MultipartResponse(Message header, Object payload) {
        this.header = header;
        this.payload = payload;
    }

    public Message getHeader() {
        return header;
    }

    public Object getPayload() {
        return payload;
    }

    public static class Builder {

        private Message header;
        private Object payload;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder header(Message header) {
            this.header = header;
            return this;
        }

        public Builder payload(Object payload) {
            this.payload = payload;
            return this;
        }

        public MultipartResponse build() {
            Objects.requireNonNull(header);
            return new MultipartResponse(header, payload);
        }
    }
}
