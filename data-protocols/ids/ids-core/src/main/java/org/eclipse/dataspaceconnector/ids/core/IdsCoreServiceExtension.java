/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.ids.core;

import org.eclipse.dataspaceconnector.ids.core.configuration.ConfigurationProviderImpl;
import org.eclipse.dataspaceconnector.ids.core.version.ConnectorVersionProviderImpl;
import org.eclipse.dataspaceconnector.ids.core.version.InboundProtocolVersionManagerImpl;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.InboundProtocolVersionManager;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.util.Set;

public class IdsCoreServiceExtension implements ServiceExtension {
    private static final String[] REQUIRES = {
    };
    private static final String[] PROVIDES = {
            ConfigurationProvider.class.getName(),
            InboundProtocolVersionManager.class.getName(),
            ConnectorVersionProvider.class.getName()
    };
    private Monitor monitor;

    @Override
    public final Set<String> provides() {
        return Set.of(PROVIDES);
    }

    @Override
    public final Set<String> requires() {
        return Set.of(REQUIRES);
    }

    @Override
    public void initialize(final ServiceExtensionContext serviceExtensionContext) {
        monitor = serviceExtensionContext.getMonitor();

        registerConfigurationProvider(serviceExtensionContext);
        registerProtocolVersionManager(serviceExtensionContext);
        registerConnectorVersionProvider(serviceExtensionContext);

        monitor.info("Initialized IDS Core extension");
    }

    @Override
    public void start() {
        monitor.info("Started IDS Core extension");
    }

    @Override
    public void shutdown() {
        monitor.info("Shutdown IDS Core extension");
    }

    private void registerConfigurationProvider(final ServiceExtensionContext serviceExtensionContext) {
        final ConfigurationProvider configurationProvider = new ConfigurationProviderImpl(serviceExtensionContext);
        serviceExtensionContext.registerService(ConfigurationProvider.class, configurationProvider);
    }

    private void registerProtocolVersionManager(final ServiceExtensionContext serviceExtensionContext) {
        final InboundProtocolVersionManager inboundProtocolVersionManager = new InboundProtocolVersionManagerImpl();
        serviceExtensionContext.registerService(InboundProtocolVersionManager.class, inboundProtocolVersionManager);
    }

    private void registerConnectorVersionProvider(final ServiceExtensionContext serviceExtensionContext) {
        final ConnectorVersionProvider connectorVersionProvider = new ConnectorVersionProviderImpl();
        serviceExtensionContext.registerService(ConnectorVersionProvider.class, connectorVersionProvider);
    }
}
