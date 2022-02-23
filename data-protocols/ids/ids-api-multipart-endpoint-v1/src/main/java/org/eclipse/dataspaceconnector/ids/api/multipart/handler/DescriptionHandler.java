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
import org.eclipse.dataspaceconnector.ids.api.multipart.util.MessageFactory;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerRegistry;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DescriptionHandler implements Handler {
    private final Monitor monitor;
    private final TransformerRegistry transformerRegistry;
    private final ArtifactDescriptionRequestHandler artifactDescriptionRequestHandler;
    private final DataCatalogDescriptionRequestHandler dataCatalogDescriptionRequestHandler;
    private final RepresentationDescriptionRequestHandler representationDescriptionRequestHandler;
    private final ResourceDescriptionRequestHandler resourceDescriptionRequestHandler;
    private final ConnectorDescriptionRequestHandler connectorDescriptionRequestHandler;
    private final MessageFactory messageFactory;

    public DescriptionHandler(
            @NotNull Monitor monitor,
            @NotNull TransformerRegistry transformerRegistry,
            @NotNull ArtifactDescriptionRequestHandler artifactDescriptionRequestHandler,
            @NotNull DataCatalogDescriptionRequestHandler dataCatalogDescriptionRequestHandler,
            @NotNull RepresentationDescriptionRequestHandler representationDescriptionRequestHandler,
            @NotNull ResourceDescriptionRequestHandler resourceDescriptionRequestHandler,
            @NotNull ConnectorDescriptionRequestHandler connectorDescriptionRequestHandler,
            @NotNull MessageFactory messageFactory) {
        this.monitor = Objects.requireNonNull(monitor);
        this.transformerRegistry = Objects.requireNonNull(transformerRegistry);
        this.artifactDescriptionRequestHandler = Objects.requireNonNull(artifactDescriptionRequestHandler);
        this.dataCatalogDescriptionRequestHandler = Objects.requireNonNull(dataCatalogDescriptionRequestHandler);
        this.representationDescriptionRequestHandler = Objects.requireNonNull(representationDescriptionRequestHandler);
        this.resourceDescriptionRequestHandler = Objects.requireNonNull(resourceDescriptionRequestHandler);
        this.connectorDescriptionRequestHandler = Objects.requireNonNull(connectorDescriptionRequestHandler);
        this.messageFactory = Objects.requireNonNull(messageFactory);
    }

    @Override
    public boolean canHandle(@NotNull MultipartRequest multipartRequest) {
        Objects.requireNonNull(multipartRequest);

        return multipartRequest.getHeader() instanceof DescriptionRequestMessage;
    }

    @Override
    public MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest,
                                           @NotNull Result<ClaimToken> verificationResult) {
        Objects.requireNonNull(multipartRequest);
        Objects.requireNonNull(verificationResult);

        try {
            return handleRequestInternal(multipartRequest, verificationResult);
        } catch (EdcException exception) {
            monitor.severe(String.format("Could not handle multipart request: %s", exception.getMessage()), exception);
        }

        return createErrorMultipartResponse(multipartRequest.getHeader());
    }

    public MultipartResponse handleRequestInternal(@NotNull MultipartRequest multipartRequest,
                                                   @NotNull Result<ClaimToken> verificationResult) {
        Objects.requireNonNull(multipartRequest);
        Objects.requireNonNull(verificationResult);

        var descriptionRequestMessage = (DescriptionRequestMessage) multipartRequest.getHeader();

        var payload = multipartRequest.getPayload();

        var requestedElement = descriptionRequestMessage.getRequestedElement();
        IdsId idsId = null;
        if (requestedElement != null) {
            var result = transformerRegistry.transform(requestedElement, IdsId.class);
            if (result.failed() || (idsId = result.getContent()) == null) {
                monitor.warning(
                        String.format(
                                "Could not transform URI to IdsId: [%s]",
                                String.join(", ", result.getFailureMessages())
                        )
                );
                return createBadParametersErrorMultipartResponse(descriptionRequestMessage);
            }
        }

        IdsType type;
        if (idsId == null || (type = idsId.getType()) == IdsType.CONNECTOR) {
            return connectorDescriptionRequestHandler.handle(descriptionRequestMessage, verificationResult, payload);
        }

        switch (type) {
            case ARTIFACT:
                return artifactDescriptionRequestHandler.handle(descriptionRequestMessage, verificationResult, payload);
            case CATALOG:
                return dataCatalogDescriptionRequestHandler.handle(descriptionRequestMessage, verificationResult, payload);
            case REPRESENTATION:
                return representationDescriptionRequestHandler.handle(descriptionRequestMessage, verificationResult, payload);
            case RESOURCE:
                return resourceDescriptionRequestHandler.handle(descriptionRequestMessage, verificationResult, payload);
            default:
                return createErrorMultipartResponse(descriptionRequestMessage);
        }
    }

    private MultipartResponse createBadParametersErrorMultipartResponse(Message message) {
        return MultipartResponse.Builder.newInstance()
                .header(messageFactory.badParameters(message))
                .build();
    }

    private MultipartResponse createErrorMultipartResponse(Message message) {
        return MultipartResponse.Builder.newInstance()
                .header(messageFactory.messageTypeNotSupported(message))
                .build();
    }
}
