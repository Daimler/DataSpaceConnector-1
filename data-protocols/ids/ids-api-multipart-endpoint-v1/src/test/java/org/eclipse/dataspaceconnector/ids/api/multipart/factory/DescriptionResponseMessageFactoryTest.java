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

import de.fraunhofer.iais.eis.Message;
import org.easymock.EasyMock;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.version.IdsOutboundProtocolVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.IdsProtocol;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

public class DescriptionResponseMessageFactoryTest {

    private static class Fixtures {
        public static final URI ID = URI.create("https://example.com/id");
        public static final IdsProtocol OUTBOUND_PROTOCOL_VERSION = new IdsProtocol("4.2.1");
        private static final URI MESSAGE_ID = URI.create("https://example.com/message");
        private static final URI MESSAGE_SENDER_AGENT = URI.create("https://example.com/sender/agent");
        private static final URI MESSAGE_ISSUER = URI.create("https://example.com/issuer");
    }

    // mocks
    private DescriptionResponseMessageFactorySettings descriptionResponseMessageFactorySettings;
    private IdsOutboundProtocolVersionProvider outboundProtocolVersionProvider;
    private Message message;

    @BeforeEach
    public void setup() {
        descriptionResponseMessageFactorySettings = EasyMock.createMock(DescriptionResponseMessageFactorySettings.class);
        outboundProtocolVersionProvider = EasyMock.createMock(IdsOutboundProtocolVersionProvider.class);
        message = EasyMock.createMock(Message.class);

        EasyMock.expect(descriptionResponseMessageFactorySettings.getId()).andReturn(Fixtures.ID).anyTimes();
        EasyMock.expect(outboundProtocolVersionProvider.getIdsProtocolVersion()).andReturn(Fixtures.OUTBOUND_PROTOCOL_VERSION).anyTimes();
        EasyMock.expect(message.getId()).andReturn(Fixtures.MESSAGE_ID).anyTimes();
        EasyMock.expect(message.getSenderAgent()).andReturn(Fixtures.MESSAGE_SENDER_AGENT).anyTimes();
        EasyMock.expect(message.getIssuerConnector()).andReturn(Fixtures.MESSAGE_ISSUER).anyTimes();

        EasyMock.replay(descriptionResponseMessageFactorySettings, outboundProtocolVersionProvider, message);
    }

    @AfterEach
    public void teardown() {
        EasyMock.verify(descriptionResponseMessageFactorySettings, outboundProtocolVersionProvider);
    }

    @Test
    public void testMessageFactoryReturnsAsExpected() {
        // prepare
        var descriptionResponseMessageFactory = new DescriptionResponseMessageFactory(descriptionResponseMessageFactorySettings, outboundProtocolVersionProvider);

        // invoke
        var response = descriptionResponseMessageFactory.createDescriptionResponseMessage(message);

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
