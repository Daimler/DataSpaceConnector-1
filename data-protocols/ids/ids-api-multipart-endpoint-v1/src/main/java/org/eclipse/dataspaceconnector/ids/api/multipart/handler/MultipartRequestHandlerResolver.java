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

package org.eclipse.dataspaceconnector.ids.api.multipart.handler;

import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MultipartRequestHandlerResolver {
    private final List<MultipartRequestHandler> multipartRequestHandlers = new LinkedList<>();

    public MultipartRequestHandlerResolver(MultipartRequestHandler... multipartRequestHandlers) {
        this(Arrays.asList(multipartRequestHandlers));
    }

    public MultipartRequestHandlerResolver(
            List<MultipartRequestHandler> multipartRequestHandlers) {
        multipartRequestHandlers = multipartRequestHandlers != null ? multipartRequestHandlers : Collections.emptyList();
        for (MultipartRequestHandler multipartRequestHandler : multipartRequestHandlers) {
            if (multipartRequestHandler != null) {
                this.multipartRequestHandlers.add(multipartRequestHandler);
            }
        }
    }

    public MultipartRequestHandler resolveHandler(MultipartRequest multipartRequest) {
        for (MultipartRequestHandler multipartRequestHandler : multipartRequestHandlers) {
            if (multipartRequestHandler.canHandle(multipartRequest)) {
                return multipartRequestHandler;
            }
        }

        return null;
    }
}
