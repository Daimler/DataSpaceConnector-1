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

public class ConnectorDescriptionRequestHandler implements DescriptionRequestMessageHandler {
    private final MessageFactory messageFactory;
    private final SelfDescriptionService selfDescriptionService;

    public ConnectorDescriptionRequestHandler(
            final MessageFactory messageFactory,
            final SelfDescriptionService connectorDescriptionService) {
        this.messageFactory = messageFactory;
        this.selfDescriptionService = connectorDescriptionService;
    }

    @Override
    public MultipartResponse handle(final DescriptionRequestMessage descriptionRequestMessage, final String payload) {
        final DescriptionResponseMessage descriptionResponseMessage = messageFactory
                .createDescriptionResponseMessage(descriptionRequestMessage);

        // TODO add payload only if the requested ID matches with the connector ID
        final Connector connector = selfDescriptionService.createSelfDescription();

        return MultipartResponse.Builder.newInstance()
                .header(descriptionResponseMessage)
                .payload(connector)
                .build();
    }
}
