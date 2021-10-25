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

import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import de.fraunhofer.iais.eis.Message;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.DescriptionResponseMessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.RepresentationService;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

import static org.eclipse.dataspaceconnector.ids.api.multipart.util.RejectionMessageUtil.badParameters;
import static org.eclipse.dataspaceconnector.ids.api.multipart.util.RejectionMessageUtil.notFound;

public class RepresentationDescriptionRequestHandler implements DescriptionRequestHandler {
    private final RepresentationDescriptionRequestHandlerSettings representationDescriptionRequestHandlerSettings;
    private final RepresentationService representationService;
    private final DescriptionResponseMessageFactory descriptionResponseMessageFactory;

    public RepresentationDescriptionRequestHandler(
            RepresentationDescriptionRequestHandlerSettings representationDescriptionRequestHandlerSettings,
            RepresentationService representationService,
            DescriptionResponseMessageFactory descriptionResponseMessageFactory) {
        this.representationDescriptionRequestHandlerSettings = representationDescriptionRequestHandlerSettings;
        this.representationService = representationService;
        this.descriptionResponseMessageFactory = descriptionResponseMessageFactory;
    }

    @Override
    public MultipartResponse handle(@NotNull DescriptionRequestMessage descriptionRequestMessage, @Nullable String payload) {
        Objects.requireNonNull(descriptionRequestMessage);

        URI uri = descriptionRequestMessage.getRequestedElement();
        if (uri == null) {
            return createBadParametersErrorMultipartResponse(descriptionRequestMessage);
        }

        IdsId idsId = IdsId.fromUri(uri);
        if (idsId.getType() != IdsId.Type.ARTIFACT) {
            return createBadParametersErrorMultipartResponse(descriptionRequestMessage);
        }

        Representation representation = representationService.createRepresentation(idsId.getValue());
        if (representation == null) {
            return createNotFoundErrorMultipartResponse(descriptionRequestMessage);
        }

        DescriptionResponseMessage descriptionResponseMessage = descriptionResponseMessageFactory
                .createDescriptionResponseMessage(descriptionRequestMessage);

        return MultipartResponse.Builder.newInstance()
                .header(descriptionResponseMessage)
                .payload(representation)
                .build();
    }

    private MultipartResponse createBadParametersErrorMultipartResponse(Message message) {
        return MultipartResponse.Builder.newInstance()
                .header(badParameters(message, representationDescriptionRequestHandlerSettings.getId()))
                .build();
    }

    private MultipartResponse createNotFoundErrorMultipartResponse(Message message) {
        return MultipartResponse.Builder.newInstance()
                .header(notFound(message, representationDescriptionRequestHandlerSettings.getId()))
                .build();
    }
}
