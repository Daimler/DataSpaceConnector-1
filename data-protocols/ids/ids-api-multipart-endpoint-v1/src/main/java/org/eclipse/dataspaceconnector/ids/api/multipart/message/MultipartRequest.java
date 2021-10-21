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

package org.eclipse.dataspaceconnector.ids.api.multipart.message;

import de.fraunhofer.iais.eis.RequestMessage;

import java.util.Objects;

public class MultipartRequest {

    private final RequestMessage header;
    private final String payload;

    private MultipartRequest(RequestMessage header, String payload) {
        this.header = Objects.requireNonNull(header);
        this.payload = payload;
    }

    public RequestMessage getHeader() {
        return header;
    }

    public String getPayload() {
        return payload;
    }

    public static class Builder {

        private RequestMessage header;
        private String payload;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder header(RequestMessage header) {
            this.header = header;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public MultipartRequest build() {
            return new MultipartRequest(header, payload);
        }
    }
}
