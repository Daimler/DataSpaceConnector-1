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

package org.eclipse.dataspaceconnector.ids.api.multipart.request;

import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartRequest;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.MultipartRequestHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MultipartRequestHandlerResolver {
    private final List<MultipartRequestHandler> multipartRequestHandlers = new LinkedList<>();

    public MultipartRequestHandlerResolver(MultipartRequestHandler... multipartRequestHandlers) {
        this(Arrays.asList(multipartRequestHandlers));
    }

    public MultipartRequestHandlerResolver(
            List<MultipartRequestHandler> multipartRequestHandlers) {
        Optional.ofNullable(multipartRequestHandlers).orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .forEach(this.multipartRequestHandlers::add);
    }

    @NotNull
    public Optional<MultipartRequestHandler> resolveHandler(MultipartRequest multipartRequest) {
        return multipartRequestHandlers
                .stream()
                .filter(s -> s.canHandle(multipartRequest))
                .findFirst();
    }
}
