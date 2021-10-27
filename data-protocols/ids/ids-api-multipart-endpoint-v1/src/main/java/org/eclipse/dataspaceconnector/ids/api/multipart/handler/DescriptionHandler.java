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

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.Message;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ArtifactDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ConnectorDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.DataCatalogDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.RepresentationDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ResourceDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartRequest;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static org.eclipse.dataspaceconnector.ids.api.multipart.util.RejectionMessageUtil.badParameters;
import static org.eclipse.dataspaceconnector.ids.api.multipart.util.RejectionMessageUtil.messageTypeNotSupported;

public class DescriptionHandler implements Handler {
    private final DescriptionHandlerSettings descriptionHandlerSettings;
    private final TransformerRegistry transformerRegistry;
    private final ArtifactDescriptionRequestHandler artifactDescriptionRequestHandler;
    private final DataCatalogDescriptionRequestHandler dataCatalogDescriptionRequestHandler;
    private final RepresentationDescriptionRequestHandler representationDescriptionRequestHandler;
    private final ResourceDescriptionRequestHandler resourceDescriptionRequestHandler;
    private final ConnectorDescriptionRequestHandler connectorDescriptionRequestHandler;

    public DescriptionHandler(
            DescriptionHandlerSettings descriptionHandlerSettings,
            TransformerRegistry transformerRegistry,
            ArtifactDescriptionRequestHandler artifactDescriptionRequestHandler,
            DataCatalogDescriptionRequestHandler dataCatalogDescriptionRequestHandler,
            RepresentationDescriptionRequestHandler representationDescriptionRequestHandler,
            ResourceDescriptionRequestHandler resourceDescriptionRequestHandler,
            ConnectorDescriptionRequestHandler connectorDescriptionRequestHandler) {
        this.descriptionHandlerSettings = descriptionHandlerSettings;
        this.transformerRegistry = transformerRegistry;
        this.artifactDescriptionRequestHandler = artifactDescriptionRequestHandler;
        this.dataCatalogDescriptionRequestHandler = dataCatalogDescriptionRequestHandler;
        this.representationDescriptionRequestHandler = representationDescriptionRequestHandler;
        this.resourceDescriptionRequestHandler = resourceDescriptionRequestHandler;
        this.connectorDescriptionRequestHandler = connectorDescriptionRequestHandler;
    }

    @Override
    public boolean canHandle(@NotNull MultipartRequest multipartRequest) {
        Objects.requireNonNull(multipartRequest);

        return multipartRequest.getHeader() instanceof DescriptionRequestMessage;
    }

    @Override
    public MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest) {
        Objects.requireNonNull(multipartRequest);

        var descriptionRequestMessage = (DescriptionRequestMessage) multipartRequest.getHeader();

        var payload = multipartRequest.getPayload();

        var requestedElement = descriptionRequestMessage.getRequestedElement();
        IdsType type = null;
        if (requestedElement != null) {
            var result = transformerRegistry.transform(requestedElement, IdsType.class);
            if (result.hasProblems()) {
                // TODO: log problems
                return createBadParametersErrorMultipartResponse(descriptionRequestMessage);
            }

            type = result.getOutput();
        }

        if (type == null || type == IdsType.CONNECTOR) {
            return connectorDescriptionRequestHandler.handle(descriptionRequestMessage, payload);
        }

        switch (type) {
            case ARTIFACT:
                return artifactDescriptionRequestHandler.handle(descriptionRequestMessage, payload);
            case CATALOG:
                return dataCatalogDescriptionRequestHandler.handle(descriptionRequestMessage, payload);
            case REPRESENTATION:
                return representationDescriptionRequestHandler.handle(descriptionRequestMessage, payload);
            case RESOURCE:
                return resourceDescriptionRequestHandler.handle(descriptionRequestMessage, payload);
            default:
                return createErrorMultipartResponse(descriptionRequestMessage);
        }
    }

    private MultipartResponse createBadParametersErrorMultipartResponse(Message message) {
        return MultipartResponse.Builder.newInstance()
                .header(badParameters(message, descriptionHandlerSettings.getId()))
                .build();
    }

    private MultipartResponse createErrorMultipartResponse(Message message) {
        return MultipartResponse.Builder.newInstance()
                .header(messageTypeNotSupported(message, descriptionHandlerSettings.getId()))
                .build();
    }
}
