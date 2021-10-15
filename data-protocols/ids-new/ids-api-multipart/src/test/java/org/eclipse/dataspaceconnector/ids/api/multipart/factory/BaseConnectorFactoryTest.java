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

import de.fraunhofer.iais.eis.BaseConnector;
import org.assertj.core.api.Assertions;
import org.easymock.EasyMock;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;
import org.eclipse.dataspaceconnector.ids.spi.types.SecurityProfile;
import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.IdsProtocolVersion;
import org.eclipse.dataspaceconnector.ids.spi.version.InboundProtocolVersionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class BaseConnectorFactoryTest {

    private static class Fixtures {
        public static final URI ID = URI.create("https://example.com/connector");
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final URI MAINTAINER = URI.create("https://example.com/maintainer");
        public static final URI CURATOR = URI.create("https://example.com/curator");
        public static final URI CONNECTOR_ENDPOINT = URI.create("https://example.com/connector/endpoint");
        public static final SecurityProfile SECURITY_PROFILE = SecurityProfile.BASE_SECURITY_PROFILE;
        public static final List<IdsProtocolVersion> INBOUND_PROTOCOL_VERSIONS = Collections.singletonList(new IdsProtocolVersion("4.2.1"));
        public static final String CONNECTOR_VERSION = "0.0.1";
    }

    // Mocks
    private ConfigurationProvider configurationProvider;
    private InboundProtocolVersionManager inboundProtocolVersionManager;
    private ConnectorVersionProvider connectorVersionProvider;

    @BeforeEach
    public void setUp() {
        // prepare/instantiate mock instances
        configurationProvider = EasyMock.createMock(ConfigurationProvider.class);
        inboundProtocolVersionManager = EasyMock.createMock(InboundProtocolVersionManager.class);
        connectorVersionProvider = EasyMock.createMock(ConnectorVersionProvider.class);
    }

    @AfterEach
    public void tearDown() {
        // verify - no more invocations on mock
        EasyMock.verify(configurationProvider, inboundProtocolVersionManager, connectorVersionProvider);
    }

    @Test
    void testBaseConnectorFactoryReturnsAsExpected() {
        // prepare
        final BaseConnectorFactory baseConnectorFactory = new BaseConnectorFactory(
                configurationProvider,
                inboundProtocolVersionManager,
                connectorVersionProvider
        );

        EasyMock.expect(configurationProvider.resolveId()).andReturn(Optional.of(Fixtures.ID)).times(1);
        EasyMock.expect(configurationProvider.resolveTitle()).andReturn(Optional.of(Fixtures.TITLE)).times(1);
        EasyMock.expect(configurationProvider.resolveDescription()).andReturn(Optional.of(Fixtures.DESCRIPTION)).times(1);
        EasyMock.expect(configurationProvider.resolveMaintainer()).andReturn(Optional.of(Fixtures.MAINTAINER)).times(1);
        EasyMock.expect(configurationProvider.resolveCurator()).andReturn(Optional.of(Fixtures.CURATOR)).times(1);
        EasyMock.expect(configurationProvider.resolveConnectorEndpoint()).andReturn(Optional.of(Fixtures.CONNECTOR_ENDPOINT)).times(1);
        EasyMock.expect(configurationProvider.resolveSecurityProfile()).andReturn(Optional.of(Fixtures.SECURITY_PROFILE)).times(1);

        EasyMock.expect(inboundProtocolVersionManager.getInboundProtocolVersions()).andReturn(Fixtures.INBOUND_PROTOCOL_VERSIONS).times(1);

        EasyMock.expect(connectorVersionProvider.getVersion()).andReturn(Optional.of(Fixtures.CONNECTOR_VERSION)).times(1);

        EasyMock.replay(configurationProvider, inboundProtocolVersionManager, connectorVersionProvider);

        // invoke
        final BaseConnector connector = baseConnectorFactory.createBaseConnector();

        // verify
        Assertions.assertThat(Fixtures.TITLE).isEqualTo(connector.getTitle().get(0).getValue());
        Assertions.assertThat(Fixtures.DESCRIPTION).isEqualTo(connector.getDescription().get(0).getValue());
        Assertions.assertThat(Fixtures.CONNECTOR_ENDPOINT).isEqualTo(connector.getHasDefaultEndpoint().getAccessURL());
        Assertions.assertThat(Fixtures.MAINTAINER).isEqualTo(connector.getMaintainer());
        Assertions.assertThat(Fixtures.CURATOR).isEqualTo(connector.getCurator());

        Assertions.assertThat(Fixtures.INBOUND_PROTOCOL_VERSIONS.stream().map(IdsProtocolVersion::getValue).collect(Collectors.toList()))
                .containsAll(connector.getInboundModelVersion());

        Assertions.assertThat(Fixtures.CONNECTOR_VERSION).isEqualTo(connector.getVersion());
    }
}