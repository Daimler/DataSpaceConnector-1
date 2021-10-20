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
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.DescriptionResponseMessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.http.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.ConnectorDescriptionService;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;

import java.net.URI;

public class ConnectorDescriptionRequestHandler implements DescriptionRequestMessageHandler {
    private final DescriptionResponseMessageFactory descriptionResponseMessageFactory;
    private final ConnectorDescriptionService connectorDescriptionService;
    private final ConfigurationProvider configurationProvider;

    public ConnectorDescriptionRequestHandler(
            DescriptionResponseMessageFactory descriptionResponseMessageFactory,
            ConnectorDescriptionService connectorDescriptionService,
            ConfigurationProvider configurationProvider) {
        this.descriptionResponseMessageFactory = descriptionResponseMessageFactory;
        this.connectorDescriptionService = connectorDescriptionService;
        this.configurationProvider = configurationProvider;
    }

    @Override
    public MultipartResponse handle(DescriptionRequestMessage descriptionRequestMessage, String payload) {
        if (!isRequestingCurrentConnectorsDescription(descriptionRequestMessage)) {
            return null;
        }

        DescriptionResponseMessage descriptionResponseMessage = descriptionResponseMessageFactory
                .createDescriptionResponseMessage(descriptionRequestMessage);

        Connector connector = connectorDescriptionService.createSelfDescription();

        return MultipartResponse.Builder.newInstance()
                .header(descriptionResponseMessage)
                .payload(connector)
                .build();
    }

    private boolean isRequestingCurrentConnectorsDescription(DescriptionRequestMessage descriptionRequestMessage) {
        URI requestedConnectorId = descriptionRequestMessage.getRequestedElement();
        URI connectorId = configurationProvider.resolveId();

        if (requestedConnectorId == null) {
            return true;
        }

        return requestedConnectorId.equals(connectorId);
    }
}
