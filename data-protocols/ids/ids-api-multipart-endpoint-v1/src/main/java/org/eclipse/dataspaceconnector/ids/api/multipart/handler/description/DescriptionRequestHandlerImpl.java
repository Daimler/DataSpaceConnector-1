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

package org.eclipse.dataspaceconnector.ids.api.multipart.handler.description;

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.RequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartRequest;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DescriptionRequestHandlerImpl implements RequestHandler {
    private final Map<IdsId.Type, DescriptionRequestHandler> descriptionRequestMessageHandlers = new HashMap<>();

    @Override
    public boolean canHandle(@NotNull MultipartRequest multipartRequest) {
        Objects.requireNonNull(multipartRequest);
        return multipartRequest.getHeader() instanceof DescriptionRequestMessage;
    }

    @Override
    public MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest) {
        Objects.requireNonNull(multipartRequest);
        var descriptionRequestMessage = (DescriptionRequestMessage) multipartRequest.getHeader();

        var requestedElement = descriptionRequestMessage.getRequestedElement();
        IdsId.Type type = null;
        if (requestedElement != null) {
            type = IdsId.fromUri(requestedElement).getType();
        }

        var descriptionRequestMessageHandler = descriptionRequestMessageHandlers.get(type);
        if (descriptionRequestMessageHandler != null) {
            return descriptionRequestMessageHandler.handle(descriptionRequestMessage, multipartRequest.getPayload());
        }

        return null;
    }

    public void add(IdsId.Type type, @NotNull DescriptionRequestHandler descriptionRequestMessageHandler) {
        Objects.requireNonNull(descriptionRequestMessageHandler);

        descriptionRequestMessageHandlers.put(type, descriptionRequestMessageHandler);
    }
}
