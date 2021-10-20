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
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.jetbrains.annotations.NotNull;

import java.net.URI;


public class DescriptionRequestHandler implements MultipartRequestHandler {
    private final DescriptionRequestMessageHandlerRegistry descriptionRequestMessageHandlerRegistry;

    public DescriptionRequestHandler(
            DescriptionRequestMessageHandlerRegistry descriptionRequestMessageHandlerRegistry) {
        this.descriptionRequestMessageHandlerRegistry = descriptionRequestMessageHandlerRegistry;
    }

    @Override
    public boolean canHandle(@NotNull MultipartRequest multipartRequest) {
        return multipartRequest.getHeader() instanceof DescriptionRequestMessage;
    }

    @Override
    public MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest) {
        DescriptionRequestMessage descriptionRequestMessage = (DescriptionRequestMessage) multipartRequest.getHeader();

        URI requestedElement = descriptionRequestMessage.getRequestedElement();
        IdsId.Type type = null;
        if (requestedElement != null) {
            type = IdsId.fromUri(requestedElement).getType();
        }

        DescriptionRequestMessageHandler descriptionRequestMessageHandler = descriptionRequestMessageHandlerRegistry.get(type);
        if (descriptionRequestMessageHandler != null) {
            return descriptionRequestMessageHandler.handle(descriptionRequestMessage, multipartRequest.getPayload());
        }

        return null;
    }
}
