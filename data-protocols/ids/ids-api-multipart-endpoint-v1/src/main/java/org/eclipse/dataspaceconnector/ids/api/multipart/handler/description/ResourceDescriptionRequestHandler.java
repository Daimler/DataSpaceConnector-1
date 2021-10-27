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
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.Resource;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.DescriptionResponseMessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.ResourceService;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

import static org.eclipse.dataspaceconnector.ids.api.multipart.util.RejectionMessageUtil.badParameters;
import static org.eclipse.dataspaceconnector.ids.api.multipart.util.RejectionMessageUtil.notFound;

public class ResourceDescriptionRequestHandler implements DescriptionRequestHandler {
    private final ResourceDescriptionRequestHandlerSettings resourceDescriptionRequestHandlerSettings;
    private final ResourceService resourceService;
    private final TransformerRegistry transformerRegistry;
    private final DescriptionResponseMessageFactory descriptionResponseMessageFactory;

    public ResourceDescriptionRequestHandler(
            ResourceDescriptionRequestHandlerSettings resourceDescriptionRequestHandlerSettings,
            ResourceService resourceService,
            TransformerRegistry transformerRegistry,
            DescriptionResponseMessageFactory descriptionResponseMessageFactory) {
        this.resourceDescriptionRequestHandlerSettings = resourceDescriptionRequestHandlerSettings;
        this.resourceService = resourceService;
        this.transformerRegistry = transformerRegistry;
        this.descriptionResponseMessageFactory = descriptionResponseMessageFactory;
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
        if (Objects.requireNonNull(idsId).getType() != IdsType.RESOURCE) {
            return createBadParametersErrorMultipartResponse(descriptionRequestMessage);
        }

        Resource resource = resourceService.createResource(idsId.getValue());
        if (resource == null) {
            return createNotFoundErrorMultipartResponse(descriptionRequestMessage);
        }

        DescriptionResponseMessage descriptionResponseMessage = descriptionResponseMessageFactory
                .createDescriptionResponseMessage(descriptionRequestMessage);

        return MultipartResponse.Builder.newInstance()
                .header(descriptionResponseMessage)
                .payload(resource)
                .build();
    }

    private MultipartResponse createBadParametersErrorMultipartResponse(Message message) {
        return MultipartResponse.Builder.newInstance()
                .header(badParameters(message, resourceDescriptionRequestHandlerSettings.getId()))
                .build();
    }

    private MultipartResponse createNotFoundErrorMultipartResponse(Message message) {
        return MultipartResponse.Builder.newInstance()
                .header(notFound(message, resourceDescriptionRequestHandlerSettings.getId()))
                .build();
    }
}
