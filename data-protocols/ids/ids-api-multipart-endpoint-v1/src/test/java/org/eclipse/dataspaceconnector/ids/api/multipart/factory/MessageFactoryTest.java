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

package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import de.fraunhofer.iais.eis.Message;
import org.easymock.EasyMock;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

public class MessageFactoryTest {

    private static class Fixtures {
        public static final URI ID = URI.create("https://example.com/id");
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final URI MAINTAINER = URI.create("https://example.com/maintainer");
        public static final URI CURATOR = URI.create("https://example.com/curator");
        public static final URI CONNECTOR_ENDPOINT = URI.create("https://example.com/connector/endpoint");
        public static final IdsProtocolVersion OUTBOUND_PROTOCOL_VERSION = new IdsProtocolVersion("4.2.1");
        private static final URI MESSAGE_ID = URI.create("https://example.com/message");
        private static final URI MESSAGE_SENDER_AGENT = URI.create("https://example.com/sender/agent");
        private static final URI MESSAGE_ISSUER = URI.create("https://example.com/issuer");
    }


    // mocks
    private ConfigurationProvider configurationProvider;
    private IdsOutboundProtocolVersionProvider outboundProtocolVersionProvider;
    private Message message;

    @BeforeEach
    public void setup() {
        configurationProvider = EasyMock.createMock(ConfigurationProvider.class);
        outboundProtocolVersionProvider = EasyMock.createMock(IdsOutboundProtocolVersionProvider.class);
        message = EasyMock.createMock(Message.class);

        EasyMock.expect(configurationProvider.resolveId()).andReturn(Fixtures.ID).anyTimes();
        EasyMock.expect(configurationProvider.resolveTitle()).andReturn(Fixtures.TITLE).anyTimes();
        EasyMock.expect(configurationProvider.resolveDescription()).andReturn(Fixtures.DESCRIPTION).anyTimes();
        EasyMock.expect(configurationProvider.resolveMaintainer()).andReturn(Fixtures.MAINTAINER).anyTimes();
        EasyMock.expect(configurationProvider.resolveCurator()).andReturn(Fixtures.CURATOR).anyTimes();
        EasyMock.expect(configurationProvider.resolveConnectorEndpoint()).andReturn(Fixtures.CONNECTOR_ENDPOINT).anyTimes();
        EasyMock.expect(outboundProtocolVersionProvider.getIdsProtocolVersion()).andReturn(Fixtures.OUTBOUND_PROTOCOL_VERSION).anyTimes();
        EasyMock.expect(message.getId()).andReturn(Fixtures.MESSAGE_ID).anyTimes();
        EasyMock.expect(message.getSenderAgent()).andReturn(Fixtures.MESSAGE_SENDER_AGENT).anyTimes();
        EasyMock.expect(message.getIssuerConnector()).andReturn(Fixtures.MESSAGE_ISSUER).anyTimes();

        EasyMock.replay(configurationProvider, outboundProtocolVersionProvider, message);
    }

    @AfterEach
    public void teardown() {
        EasyMock.verify(configurationProvider, outboundProtocolVersionProvider);
    }

    @Test
    public void testMessageFactoryReturnsAsExpected() {
        // prepare
        MessageFactory messageFactory = new MessageFactory(configurationProvider, outboundProtocolVersionProvider);

        // invoke
        DescriptionResponseMessage response = messageFactory.createDescriptionResponseMessage(message);

        // verify
        var responseType = IdsId.parse(response.getId().toString()).getType();
        Assertions.assertEquals(IdsId.Type.MESSAGE, responseType);
        Assertions.assertEquals(Fixtures.ID, response.getIssuerConnector());
        Assertions.assertEquals(Fixtures.OUTBOUND_PROTOCOL_VERSION.getValue(), response.getContentVersion());
        Assertions.assertEquals(Fixtures.OUTBOUND_PROTOCOL_VERSION.getValue(), response.getModelVersion());
        Assertions.assertEquals(Fixtures.MESSAGE_ID, response.getCorrelationMessage());
        Assertions.assertEquals(Fixtures.MESSAGE_SENDER_AGENT, response.getRecipientAgent().get(0));
        Assertions.assertEquals(Fixtures.MESSAGE_ISSUER, response.getRecipientConnector().get(0));
    }
}
