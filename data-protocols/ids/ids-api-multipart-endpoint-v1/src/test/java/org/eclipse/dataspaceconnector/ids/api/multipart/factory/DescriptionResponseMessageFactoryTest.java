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
import org.eclipse.dataspaceconnector.ids.spi.IdsIdParser;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformResult;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

public class DescriptionResponseMessageFactoryTest {

    private static class Fixtures {
        public static final URI ID = URI.create("https://example.com/id");
        private static final URI MESSAGE_ID = URI.create("urn:message:12345");
        private static final URI MESSAGE_SENDER_AGENT = URI.create("https://example.com/sender/agent");
        private static final URI MESSAGE_ISSUER = URI.create("https://example.com/issuer");
    }

    // subject
    private DescriptionResponseMessageFactory descriptionResponseMessageFactory;

    // mocks
    private DescriptionResponseMessageFactorySettings descriptionResponseMessageFactorySettings;
    private TransformerRegistry transformerRegistry;
    private Message message;

    @BeforeEach
    public void setup() {
        descriptionResponseMessageFactorySettings = EasyMock.createMock(DescriptionResponseMessageFactorySettings.class);
        transformerRegistry = EasyMock.createMock(TransformerRegistry.class);
        message = EasyMock.createMock(Message.class);
        descriptionResponseMessageFactory = new DescriptionResponseMessageFactory(descriptionResponseMessageFactorySettings, transformerRegistry);
    }

    @Test
    public void testMessageFactoryReturnsAsExpected() {
        // prepare
        TransformResult<URI> result = new TransformResult<>(Fixtures.MESSAGE_ID);

        EasyMock.expect(descriptionResponseMessageFactorySettings.getId()).andReturn(Fixtures.ID).anyTimes();
        EasyMock.expect(transformerRegistry.transform(EasyMock.anyObject(IdsId.class), EasyMock.eq(URI.class))).andReturn(result);
        EasyMock.expect(message.getId()).andReturn(Fixtures.MESSAGE_ID).anyTimes();
        EasyMock.expect(message.getSenderAgent()).andReturn(Fixtures.MESSAGE_SENDER_AGENT).anyTimes();
        EasyMock.expect(message.getIssuerConnector()).andReturn(Fixtures.MESSAGE_ISSUER).anyTimes();

        EasyMock.replay(descriptionResponseMessageFactorySettings, transformerRegistry, message);

        // invoke
        var response = descriptionResponseMessageFactory.createDescriptionResponseMessage(message);

        // verify
        var responseType = IdsIdParser.parse(response.getId().toString()).getType();
        Assertions.assertEquals(IdsType.MESSAGE, responseType);
        Assertions.assertEquals(Fixtures.ID, response.getIssuerConnector());
        Assertions.assertEquals(Fixtures.MESSAGE_ID, response.getCorrelationMessage());
        Assertions.assertEquals(Fixtures.MESSAGE_SENDER_AGENT, response.getRecipientAgent().get(0));
        Assertions.assertEquals(Fixtures.MESSAGE_ISSUER, response.getRecipientConnector().get(0));
    }

    @AfterEach
    public void teardown() {
        EasyMock.verify(descriptionResponseMessageFactorySettings);
    }
}
