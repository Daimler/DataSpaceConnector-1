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

package org.eclipse.dataspaceconnector.ids.api.multipart.service;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ResourceCatalog;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.BaseConnectorFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.ResourceCatalogFactory;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.InboundProtocolVersionManager;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;

import java.util.Collections;

/**
 * The IDS service is able to create IDS compliant descriptions of resources.
 * These descriptions may be used to create a self-description or answer a Description Request Message.
 */
public class ConnectorDescriptionService {
    private final Monitor monitor;
    private final ConfigurationProvider configurationProvider;
    private final InboundProtocolVersionManager inboundProtocolVersionManager;
    private final ConnectorVersionProvider connectorVersionProvider;

    public ConnectorDescriptionService(Monitor monitor,
                                       ConfigurationProvider configurationProvider,
                                       InboundProtocolVersionManager inboundProtocolVersionManager,
                                       ConnectorVersionProvider connectorVersionProvider) {
        this.monitor = monitor;
        this.configurationProvider = configurationProvider;
        this.inboundProtocolVersionManager = inboundProtocolVersionManager;
        this.connectorVersionProvider = connectorVersionProvider;
    }


    /**
     * Provides the connector object, which may be used by the IDS self-description of the connector.
     *
     * @return connector description
     */
    public Connector createSelfDescription() {
        BaseConnectorFactory baseConnectorBuilderFactory = new BaseConnectorFactory(configurationProvider, inboundProtocolVersionManager, connectorVersionProvider);
        ResourceCatalogFactory resourceCatalogFactory = new ResourceCatalogFactory();

        ResourceCatalog resourceCatalog = resourceCatalogFactory.createResourceCatalogBuilder(Collections.emptyList());

        return baseConnectorBuilderFactory.createBaseConnector(resourceCatalog);
    }
}
