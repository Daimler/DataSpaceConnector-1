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
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import de.fraunhofer.iais.eis.Resource;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.MessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.ResourceService;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;

import java.net.URI;

public class ResourceDescriptionRequestHandler implements DescriptionRequestMessageHandler {
    private final MessageFactory messageFactory;
    private final ResourceService resourceService;

    public ResourceDescriptionRequestHandler(
            MessageFactory messageFactory,
            ResourceService resourceService) {
        this.messageFactory = messageFactory;
        this.resourceService = resourceService;
    }

    @Override
    public MultipartResponse handle(DescriptionRequestMessage descriptionRequestMessage, String payload) {

        URI uri = descriptionRequestMessage.getRequestedElement();
        if (uri == null) {
            return null;
        }

        IdsId idsId = IdsId.fromUri(uri);
        if (idsId.getType() != IdsId.Type.RESOURCE) {
            return null;
        }

        Resource resource = resourceService.createResource(idsId.getValue());
        if (resource == null) {
            return null;
        }

        DescriptionResponseMessage descriptionResponseMessage = messageFactory
                .createDescriptionResponseMessage(descriptionRequestMessage);
        return MultipartResponse.Builder.newInstance()
                .header(descriptionResponseMessage)
                .payload(resource)
                .build();
    }
}
