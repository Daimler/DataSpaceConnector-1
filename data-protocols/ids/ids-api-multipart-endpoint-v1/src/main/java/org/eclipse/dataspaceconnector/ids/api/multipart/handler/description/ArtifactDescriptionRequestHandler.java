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
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformResult;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerRegistry;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

import static org.eclipse.dataspaceconnector.ids.api.multipart.util.RejectionMessageUtil.badParameters;
import static org.eclipse.dataspaceconnector.ids.api.multipart.util.RejectionMessageUtil.notFound;

public class ArtifactDescriptionRequestHandler extends AbstractDescriptionRequestHandler implements DescriptionRequestHandler {
    private final Monitor monitor;
    private final ArtifactDescriptionRequestHandlerSettings artifactDescriptionRequestHandlerSettings;
    private final AssetIndex assetIndex;
    private final TransformerRegistry transformerRegistry;

    public ArtifactDescriptionRequestHandler(
            @NotNull Monitor monitor,
            @NotNull ArtifactDescriptionRequestHandlerSettings artifactDescriptionRequestHandlerSettings,
            @NotNull AssetIndex assetIndex,
            @NotNull TransformerRegistry transformerRegistry) {
        super(artifactDescriptionRequestHandlerSettings.getId(), transformerRegistry);
        this.monitor = monitor;
        this.artifactDescriptionRequestHandlerSettings = Objects.requireNonNull(artifactDescriptionRequestHandlerSettings);
        this.assetIndex = Objects.requireNonNull(assetIndex);
        this.transformerRegistry = Objects.requireNonNull(transformerRegistry);
    }

    @Override
    public MultipartResponse handle(@NotNull DescriptionRequestMessage descriptionRequestMessage, @Nullable String payload) {
        Objects.requireNonNull(descriptionRequestMessage);

        URI uri = descriptionRequestMessage.getRequestedElement();
        if (uri == null) {
            return createBadParametersErrorMultipartResponse(descriptionRequestMessage);
        }

        var result = transformerRegistry.transform(uri, IdsId.class);
        if (result.hasProblems()) {
            // TODO log problems
            return createBadParametersErrorMultipartResponse(descriptionRequestMessage);
        }

        IdsId idsId = result.getOutput();
        if (Objects.requireNonNull(idsId).getType() != IdsType.ARTIFACT) {
            return createBadParametersErrorMultipartResponse(descriptionRequestMessage);
        }

        Asset asset = assetIndex.findById(idsId.getValue());
        if (asset == null) {
            return createNotFoundErrorMultipartResponse(descriptionRequestMessage);
        }

        TransformResult<Artifact> transformResult = transformerRegistry.transform(asset, Artifact.class);
        if (transformResult.hasProblems()) {
            // TODO log
            return createBadParametersErrorMultipartResponse(descriptionRequestMessage);
        }

        Artifact artifact = transformResult.getOutput();

        DescriptionResponseMessage descriptionResponseMessage = createDescriptionResponseMessage(descriptionRequestMessage);

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
