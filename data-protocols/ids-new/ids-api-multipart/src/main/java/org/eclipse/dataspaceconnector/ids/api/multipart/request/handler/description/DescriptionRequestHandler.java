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

package org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description;

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartRequest;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.MultipartRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.RejectionMultipartRequestHandler;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Optional;


public class DescriptionRequestHandler implements MultipartRequestHandler {
    private final DescriptionRequestMessageHandlerRegistry descriptionRequestMessageHandlerRegistry;
    private final RejectionMultipartRequestHandler rejectionMultipartRequestHandler;

    public DescriptionRequestHandler(
            final DescriptionRequestMessageHandlerRegistry descriptionRequestMessageHandlerRegistry,
            final RejectionMultipartRequestHandler rejectionMultipartRequestHandler) {
        this.rejectionMultipartRequestHandler = rejectionMultipartRequestHandler;
        this.descriptionRequestMessageHandlerRegistry = descriptionRequestMessageHandlerRegistry;
    }

    @Override
    public boolean canHandle(final MultipartRequest multipartRequest) {
        return multipartRequest.getHeader() instanceof DescriptionRequestMessage;
    }

    @Override
    @NotNull
    public MultipartResponse handleRequest(final MultipartRequest multipartRequest) {
        final DescriptionRequestMessage descriptionRequestMessage = (DescriptionRequestMessage) multipartRequest.getHeader();

        final URI requestedElement = descriptionRequestMessage.getRequestedElement();
        final IdsId idsId = IdsId.fromUri(requestedElement);

        // TODO fix problem when a non EDC compliant connector ID is configured
        return Optional.ofNullable(descriptionRequestMessageHandlerRegistry.get(idsId.getType()))
                .map(h -> h.handle(descriptionRequestMessage, multipartRequest.getPayload()))
                .orElseGet(() -> reject(multipartRequest));
    }

    private MultipartResponse reject(final MultipartRequest multipartRequest) {
        return rejectionMultipartRequestHandler.handleRequest(multipartRequest);
    }
}
