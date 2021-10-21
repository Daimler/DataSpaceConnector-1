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

package org.eclipse.dataspaceconnector.ids.api.multipart;

import org.eclipse.dataspaceconnector.ids.api.multipart.controller.MultipartController;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.DescriptionResponseMessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.RejectionMessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.MultipartRequestHandlerResolver;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.RejectionMultipartRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ConnectorDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.DescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.DescriptionRequestMessageHandlerRegistry;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.ConnectorDescriptionService;
import org.eclipse.dataspaceconnector.ids.api.multipart.version.ProtocolVersionProviderImpl;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.IdsOutboundProtocolVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.InboundProtocolVersionManager;
import org.eclipse.dataspaceconnector.ids.spi.version.ProtocolVersionProvider;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.protocol.web.WebService;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.util.Set;

/**
 * ServiceExtension providing IDS multipart related API controllers
 */
public final class IdsMultipartApiServiceExtension implements ServiceExtension {
    private static final String NAME = "IDS Multipart API extension";

    private static final String[] REQUIRES = {
            // TODO Add IDENTITY SERVICE
            "edc:ids:core"
    };

    private static final String[] PROVIDES = {
            "edc:ids:api:multipart:endpoint:v1"
    };

    private Monitor monitor;

    @Override
    public Set<String> requires() {
        return Set.of(REQUIRES);
    }

    @Override
    public Set<String> provides() {
        return Set.of(PROVIDES);
    }

    @Override
    public void initialize(ServiceExtensionContext serviceExtensionContext) {
        monitor = serviceExtensionContext.getMonitor();

        registerComponents(serviceExtensionContext);
        registerControllers(serviceExtensionContext);

        monitor.info(String.format("Initialized %s", NAME));
    }

    private void registerComponents(ServiceExtensionContext serviceExtensionContext) {
        ProtocolVersionProvider protocolVersionProvider = new ProtocolVersionProviderImpl();
        InboundProtocolVersionManager inboundProtocolVersionManager = serviceExtensionContext.getService(InboundProtocolVersionManager.class);
        inboundProtocolVersionManager.addInboundProtocolVersionProvider(protocolVersionProvider);
    }

    @Override
    public void start() {
        monitor.info(String.format("Started %s", NAME));
    }

    @Override
    public void shutdown() {
        monitor.info(String.format("Shutdown %s", NAME));
    }

    private void registerControllers(ServiceExtensionContext serviceExtensionContext) {
        Monitor monitor = serviceExtensionContext.getMonitor();
        WebService webService = serviceExtensionContext.getService(WebService.class);

        IdentityService identityService = serviceExtensionContext.getService(IdentityService.class);
        ConfigurationProvider configurationProvider = serviceExtensionContext.getService(ConfigurationProvider.class);
        InboundProtocolVersionManager inboundProtocolVersionManager = serviceExtensionContext.getService(InboundProtocolVersionManager.class);
        IdsOutboundProtocolVersionProvider outboundProtocolVersionProvider = serviceExtensionContext.getService(IdsOutboundProtocolVersionProvider.class);
        ConnectorVersionProvider connectorVersionProvider = serviceExtensionContext.getService(ConnectorVersionProvider.class);

        RejectionMessageFactory rejectionMessageFactory = new RejectionMessageFactory(configurationProvider, outboundProtocolVersionProvider);
        RejectionMultipartRequestHandler rejectionMultipartRequestHandler = new RejectionMultipartRequestHandler(rejectionMessageFactory);

        DescriptionRequestMessageHandlerRegistry descriptionRequestMessageHandlerRegistry = new DescriptionRequestMessageHandlerRegistry();
        DescriptionRequestHandler descriptionRequestHandler = new DescriptionRequestHandler(descriptionRequestMessageHandlerRegistry);
        MultipartRequestHandlerResolver multipartRequestHandlerResolver = new MultipartRequestHandlerResolver(
                descriptionRequestHandler
        );

        ConnectorDescriptionService connectorDescriptionService = new ConnectorDescriptionService(monitor, configurationProvider, inboundProtocolVersionManager, connectorVersionProvider);
        DescriptionResponseMessageFactory descriptionResponseMessageFactory = new DescriptionResponseMessageFactory(configurationProvider, outboundProtocolVersionProvider);
        ConnectorDescriptionRequestHandler connectorDescriptionRequestHandler = new ConnectorDescriptionRequestHandler(descriptionResponseMessageFactory, connectorDescriptionService, configurationProvider);
        descriptionRequestMessageHandlerRegistry.add(null, connectorDescriptionRequestHandler);
        descriptionRequestMessageHandlerRegistry.add(IdsId.Type.CONNECTOR, connectorDescriptionRequestHandler);

        MultipartController multipartController = new MultipartController(monitor, identityService, multipartRequestHandlerResolver, rejectionMultipartRequestHandler);

        webService.registerController(multipartController);
    }

}
