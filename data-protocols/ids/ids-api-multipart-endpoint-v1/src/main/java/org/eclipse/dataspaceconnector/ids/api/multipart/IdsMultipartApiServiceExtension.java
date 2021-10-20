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
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description.ArtifactDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description.ConnectorDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description.DataCatalogDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description.DescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description.DescriptionRequestMessageHandlerRegistry;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description.RepresentationDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description.ResourceDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.ArtifactService;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.DataCatalogService;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.RepresentationService;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.ResourceService;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.SelfDescriptionService;
import org.eclipse.dataspaceconnector.ids.api.multipart.version.ProtocolVersionProviderImpl;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.IdsOutboundProtocolVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.InboundProtocolVersionManager;
import org.eclipse.dataspaceconnector.ids.spi.version.ProtocolVersionProvider;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
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
        WebService webService = serviceExtensionContext.getService(WebService.class);
        Monitor monitor = serviceExtensionContext.getMonitor();
        ContractOfferService contractOfferService = serviceExtensionContext.getService(ContractOfferService.class);
        ConfigurationProvider configurationProvider = serviceExtensionContext.getService(ConfigurationProvider.class);
        InboundProtocolVersionManager inboundProtocolVersionManager = serviceExtensionContext.getService(InboundProtocolVersionManager.class);
        IdsOutboundProtocolVersionProvider outboundProtocolVersionProvider = serviceExtensionContext.getService(IdsOutboundProtocolVersionProvider.class);
        ConnectorVersionProvider connectorVersionProvider = serviceExtensionContext.getService(ConnectorVersionProvider.class);
        SelfDescriptionService selfDescriptionService = new SelfDescriptionService(
                monitor, contractOfferService, configurationProvider, inboundProtocolVersionManager, connectorVersionProvider
        );
        MessageFactory messageFactory = new MessageFactory(configurationProvider, outboundProtocolVersionProvider);
        AssetIndex assetIndex = serviceExtensionContext.getService(AssetIndex.class);

        DescriptionRequestMessageHandlerRegistry descriptionRequestMessageHandlerRegistry = new DescriptionRequestMessageHandlerRegistry();
        ConnectorDescriptionRequestHandler connectorDescriptionRequestMessageHandler = new ConnectorDescriptionRequestHandler(messageFactory, selfDescriptionService, configurationProvider);
        descriptionRequestMessageHandlerRegistry.add(IdsId.Type.CONNECTOR, connectorDescriptionRequestMessageHandler);

        DataCatalogService dataCatalogService = new DataCatalogService(monitor, assetIndex);
        DataCatalogDescriptionRequestHandler dataCatalogDescriptionRequestHandler = new DataCatalogDescriptionRequestHandler(messageFactory, dataCatalogService);
        descriptionRequestMessageHandlerRegistry.add(IdsId.Type.CATALOG, dataCatalogDescriptionRequestHandler);

        ArtifactService artifactService = new ArtifactService(monitor, assetIndex);
        ArtifactDescriptionRequestHandler artifactDescriptionRequestHandler = new ArtifactDescriptionRequestHandler(messageFactory, artifactService);
        descriptionRequestMessageHandlerRegistry.add(IdsId.Type.ARTIFACT, artifactDescriptionRequestHandler);

        RepresentationService representationService = new RepresentationService(monitor, assetIndex);
        RepresentationDescriptionRequestHandler representationDescriptionRequestHandler = new RepresentationDescriptionRequestHandler(messageFactory, representationService);
        descriptionRequestMessageHandlerRegistry.add(IdsId.Type.REPRESENTATION, representationDescriptionRequestHandler);

        ResourceService resourceService = new ResourceService(monitor, assetIndex);
        ResourceDescriptionRequestHandler resourceDescriptionRequestHandler = new ResourceDescriptionRequestHandler(messageFactory, resourceService);
        descriptionRequestMessageHandlerRegistry.add(IdsId.Type.RESOURCE, resourceDescriptionRequestHandler);

        RejectionMultipartRequestHandler rejectionMultipartRequestHandler = new RejectionMultipartRequestHandler(messageFactory);
        DescriptionRequestHandler descriptionRequestHandler = new DescriptionRequestHandler(descriptionRequestMessageHandlerRegistry, rejectionMultipartRequestHandler);
        MultipartRequestHandlerResolver multipartRequestHandlerResolver = new MultipartRequestHandlerResolver(
                descriptionRequestHandler
        );
        MultipartController multipartController = new MultipartController(multipartRequestHandlerResolver, rejectionMultipartRequestHandler);

        webService.registerController(multipartController);
    }

}
