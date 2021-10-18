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

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.MessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.ArtifactService;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;

import java.net.URI;

public class ArtifactDescriptionRequestHandler implements DescriptionRequestMessageHandler {
    private final MessageFactory messageFactory;
    private final ArtifactService artifactService;

    public ArtifactDescriptionRequestHandler(
            MessageFactory messageFactory,
            ArtifactService artifactService) {
        this.messageFactory = messageFactory;
        this.artifactService = artifactService;
    }

    @Override
    public MultipartResponse handle(DescriptionRequestMessage descriptionRequestMessage, String payload) {

        URI uri = descriptionRequestMessage.getRequestedElement();
        if (uri == null) {
            return null;
        }

        IdsId idsId = IdsId.fromUri(uri);
        if (idsId.getType() != IdsId.Type.ARTIFACT) {
            return null;
        }

        Artifact artifact = artifactService.createArtifact(idsId.getValue());
        if (artifact == null) {
            return null;
        }

        DescriptionResponseMessage descriptionResponseMessage = messageFactory
                .createDescriptionResponseMessage(descriptionRequestMessage);
        return MultipartResponse.Builder.newInstance()
                .header(descriptionResponseMessage)
                .payload(artifact)
                .build();
    }
}
