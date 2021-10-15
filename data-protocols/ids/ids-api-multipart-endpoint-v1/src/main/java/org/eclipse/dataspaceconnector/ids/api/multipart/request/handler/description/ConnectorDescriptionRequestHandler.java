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

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.MessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.SelfDescriptionService;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;

import java.net.URI;
import java.util.Optional;

public class ConnectorDescriptionRequestHandler implements DescriptionRequestMessageHandler {
    private final MessageFactory messageFactory;
    private final SelfDescriptionService selfDescriptionService;
    private final ConfigurationProvider configurationProvider;

    public ConnectorDescriptionRequestHandler(
            MessageFactory messageFactory,
            SelfDescriptionService connectorDescriptionService,
            ConfigurationProvider configurationProvider) {
        this.messageFactory = messageFactory;
        this.selfDescriptionService = connectorDescriptionService;
        this.configurationProvider = configurationProvider;
    }

    @Override
    public MultipartResponse handle(DescriptionRequestMessage descriptionRequestMessage, String payload) {

        // reject requests for other connector descriptions
        if (!isAskingForSelfDescription(descriptionRequestMessage)) {
            return MultipartResponse.Builder.newInstance()
                    .header(messageFactory
                            .createRejectionMessage(descriptionRequestMessage))
                    .build();
        }

        // return self description
        DescriptionResponseMessage descriptionResponseMessage = messageFactory
                .createDescriptionResponseMessage(descriptionRequestMessage);
        Connector connector = selfDescriptionService.createSelfDescription();
        return MultipartResponse.Builder.newInstance()
                .header(descriptionResponseMessage)
                .payload(connector)
                .build();
    }

    private boolean isAskingForSelfDescription(DescriptionRequestMessage descriptionRequestMessage) {
        Optional<URI> connectorId = configurationProvider.resolveId();
        return connectorId.isPresent() &&
                connectorId.get() == descriptionRequestMessage.getRequestedElement();
    }
}
