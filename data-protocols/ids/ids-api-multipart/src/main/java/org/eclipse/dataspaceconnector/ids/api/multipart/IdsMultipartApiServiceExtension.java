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
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.MessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.MultipartRequestHandlerResolver;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.RejectionMultipartRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description.ConnectorDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description.DescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description.DescriptionRequestMessageHandlerRegistry;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.SelfDescriptionService;
import org.eclipse.dataspaceconnector.ids.api.multipart.version.ProtocolVersionProviderImpl;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.IdsOutboundProtocolVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.InboundProtocolVersionManager;
import org.eclipse.dataspaceconnector.ids.spi.version.ProtocolVersionProvider;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferService;
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
            ConfigurationProvider.class.getName()
    };

    private static final String[] PROVIDES = {
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
    public void initialize(final ServiceExtensionContext serviceExtensionContext) {
        monitor = serviceExtensionContext.getMonitor();

        registerComponents(serviceExtensionContext);
        registerControllers(serviceExtensionContext);

        monitor.info(String.format("Initialized %s", NAME));
    }

    private void registerComponents(final ServiceExtensionContext serviceExtensionContext) {
        final ProtocolVersionProvider protocolVersionProvider = new ProtocolVersionProviderImpl();
        final InboundProtocolVersionManager inboundProtocolVersionManager = serviceExtensionContext.getService(InboundProtocolVersionManager.class);
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

    private void registerControllers(final ServiceExtensionContext serviceExtensionContext) {
        final WebService webService = serviceExtensionContext.getService(WebService.class);
        final Monitor monitor = serviceExtensionContext.getMonitor();
        final ContractOfferService contractOfferService = serviceExtensionContext.getService(ContractOfferService.class);
        final ConfigurationProvider configurationProvider = serviceExtensionContext.getService(ConfigurationProvider.class);
        final InboundProtocolVersionManager inboundProtocolVersionManager = serviceExtensionContext.getService(InboundProtocolVersionManager.class);
        final IdsOutboundProtocolVersionProvider outboundProtocolVersionProvider = serviceExtensionContext.getService(IdsOutboundProtocolVersionProvider.class);
        final ConnectorVersionProvider connectorVersionProvider = serviceExtensionContext.getService(ConnectorVersionProvider.class);
        final SelfDescriptionService selfDescriptionService = new SelfDescriptionService(
                monitor, contractOfferService, configurationProvider, inboundProtocolVersionManager, connectorVersionProvider
        );
        final MessageFactory messageFactory = new MessageFactory(configurationProvider, outboundProtocolVersionProvider);
        final DescriptionRequestMessageHandlerRegistry descriptionRequestMessageHandlerRegistry = new DescriptionRequestMessageHandlerRegistry();
        descriptionRequestMessageHandlerRegistry.add(IdsId.Type.CONNECTOR, new ConnectorDescriptionRequestHandler(messageFactory, selfDescriptionService));
        final RejectionMultipartRequestHandler rejectionMultipartRequestHandler = new RejectionMultipartRequestHandler(messageFactory);
        final DescriptionRequestHandler descriptionRequestHandler = new DescriptionRequestHandler(descriptionRequestMessageHandlerRegistry, rejectionMultipartRequestHandler);
        final MultipartRequestHandlerResolver multipartRequestHandlerResolver = new MultipartRequestHandlerResolver(
                descriptionRequestHandler
        );
        final MultipartController multipartController = new MultipartController(multipartRequestHandlerResolver, rejectionMultipartRequestHandler);

        webService.registerController(multipartController);
    }

}
