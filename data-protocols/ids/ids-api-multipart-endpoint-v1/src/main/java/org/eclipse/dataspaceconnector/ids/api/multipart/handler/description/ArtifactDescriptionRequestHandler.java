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

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import de.fraunhofer.iais.eis.Message;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.DescriptionResponseMessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.ArtifactService;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.net.URI;
import java.util.Objects;

import static org.eclipse.dataspaceconnector.ids.api.multipart.util.RejectionMessageUtil.*;

public class ArtifactDescriptionRequestHandler implements DescriptionRequestHandler {
    private final ArtifactDescriptionRequestHandlerSettings artifactDescriptionRequestHandlerSettings;
    private final ArtifactService artifactService;
    private final DescriptionResponseMessageFactory descriptionResponseMessageFactory;

    public ArtifactDescriptionRequestHandler(
            ArtifactDescriptionRequestHandlerSettings artifactDescriptionRequestHandlerSettings,
            ArtifactService artifactService,
            DescriptionResponseMessageFactory descriptionResponseMessageFactory) {
        this.artifactDescriptionRequestHandlerSettings = artifactDescriptionRequestHandlerSettings;
        this.artifactService = artifactService;
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
        if (idsId.getType() != IdsType.ARTIFACT) {
            return createBadParametersErrorMultipartResponse(descriptionRequestMessage);
        }

        Artifact artifact = artifactService.createArtifact(idsId.getValue());
        if (artifact == null) {
            return createNotFoundErrorMultipartResponse(descriptionRequestMessage);
        }

        DescriptionResponseMessage descriptionResponseMessage = descriptionResponseMessageFactory
                .createDescriptionResponseMessage(descriptionRequestMessage);

        return MultipartResponse.Builder.newInstance()
                .header(descriptionResponseMessage)
                .payload(artifact)
                .build();
    }

    private MultipartResponse createBadParametersErrorMultipartResponse(Message message) {
        return MultipartResponse.Builder.newInstance()
                .header(badParameters(message, artifactDescriptionRequestHandlerSettings.getId()))
                .build();
    }

    private MultipartResponse createNotFoundErrorMultipartResponse(Message message) {
        return MultipartResponse.Builder.newInstance()
                .header(notFound(message, artifactDescriptionRequestHandlerSettings.getId()))
                .build();
    }
}
