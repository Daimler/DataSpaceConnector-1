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
import de.fraunhofer.iais.eis.Representation;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.MessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.RepresentationService;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;

import java.net.URI;

public class RepresentationDescriptionRequestHandler implements DescriptionRequestMessageHandler {
    private final MessageFactory messageFactory;
    private final RepresentationService representationService;

    public RepresentationDescriptionRequestHandler(
            MessageFactory messageFactory,
            RepresentationService representationService) {
        this.messageFactory = messageFactory;
        this.representationService = representationService;
    }

    @Override
    public MultipartResponse handle(DescriptionRequestMessage descriptionRequestMessage, String payload) {

        URI uri = descriptionRequestMessage.getRequestedElement();
        if (uri == null) {
            return null;
        }

        IdsId idsId = IdsId.fromUri(uri);
        if (idsId.getType() != IdsId.Type.REPRESENTATION) {
            return null;
        }

        Representation representation = representationService.createRepresentation(idsId.getValue());
        if (representation == null) {
            return null;
        }

        DescriptionResponseMessage descriptionResponseMessage = messageFactory
                .createDescriptionResponseMessage(descriptionRequestMessage);
        return MultipartResponse.Builder.newInstance()
                .header(descriptionResponseMessage)
                .payload(representation)
                .build();
    }
}
