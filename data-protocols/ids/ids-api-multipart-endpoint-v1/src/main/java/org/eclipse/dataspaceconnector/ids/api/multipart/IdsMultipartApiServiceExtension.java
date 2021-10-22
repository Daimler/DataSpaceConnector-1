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
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.BaseConnectorFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.BaseConnectorFactorySettingsFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.DescriptionResponseMessageFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.DescriptionResponseMessageFactorySettingsFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.RejectionMessageFactorySettingsFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.ResourceCatalogFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ConnectorDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ConnectorDescriptionRequestHandlerSettingsFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.DescriptionRequestHandlerImpl;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.ConnectorDescriptionService;
import org.eclipse.dataspaceconnector.ids.api.multipart.version.ProtocolVersionProviderImpl;
import org.eclipse.dataspaceconnector.ids.core.configuration.SettingResolver;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.IdsOutboundProtocolVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.InboundProtocolVersionManager;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.protocol.web.WebService;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * ServiceExtension providing IDS multipart related API controllers
 */
public final class IdsMultipartApiServiceExtension implements ServiceExtension {
    private static final String NAME = "IDS Multipart API extension";

    private static final String[] REQUIRES = {
            IdentityService.FEATURE,
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
        var protocolVersionProvider = new ProtocolVersionProviderImpl();
        var inboundProtocolVersionManager = serviceExtensionContext.getService(InboundProtocolVersionManager.class);
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
        var monitor = serviceExtensionContext.getMonitor();
        var webService = serviceExtensionContext.getService(WebService.class);

        var identityService = serviceExtensionContext.getService(IdentityService.class);
        var inboundProtocolVersionManager = serviceExtensionContext.getService(InboundProtocolVersionManager.class);
        var outboundProtocolVersionProvider = serviceExtensionContext.getService(IdsOutboundProtocolVersionProvider.class);
        var connectorVersionProvider = serviceExtensionContext.getService(ConnectorVersionProvider.class);

        // First create all objects that may return errors and ensure success
        var settingResolver = new SettingResolver(serviceExtensionContext);
        var rejectionMessageFactorySettingsFactory = new RejectionMessageFactorySettingsFactory(settingResolver);
        var rejectionMessageFactorySettingsFactoryResult = rejectionMessageFactorySettingsFactory.createRejectionMessageFactorySettings();
        var baseConnectorFactorySettingsFactory = new BaseConnectorFactorySettingsFactory(settingResolver);
        var baseConnectorFactorySettingsFactoryResult = baseConnectorFactorySettingsFactory.createBaseConnectorFactorySettings();
        var descriptionResponseMessageFactorySettingsFactory = new DescriptionResponseMessageFactorySettingsFactory(settingResolver);
        var descriptionResponseMessageFactorySettingsFactoryResult = descriptionResponseMessageFactorySettingsFactory.createDescriptionResponseMessageFactorySettings();
        var connectorDescriptionRequestHandlerSettingsFactory = new ConnectorDescriptionRequestHandlerSettingsFactory(settingResolver);
        var connectorDescriptionRequestHandlerSettingsFactoryResult = connectorDescriptionRequestHandlerSettingsFactory.createConnectorDescriptionRequestHandlerSettings();

        var allErrorsDistinct = new HashSet<String>();
        allErrorsDistinct.addAll(rejectionMessageFactorySettingsFactoryResult.getErrors());
        allErrorsDistinct.addAll(baseConnectorFactorySettingsFactoryResult.getErrors());
        allErrorsDistinct.addAll(descriptionResponseMessageFactorySettingsFactoryResult.getErrors());
        allErrorsDistinct.addAll(connectorDescriptionRequestHandlerSettingsFactoryResult.getErrors());
        if (!allErrorsDistinct.isEmpty()) {
            throw new EdcException(String.join(", ", allErrorsDistinct));
        }

        var rejectionMessageFactorySettings = rejectionMessageFactorySettingsFactoryResult.getRejectionMessageFactorySettings();
        if (rejectionMessageFactorySettings == null) {
            throw new EdcException("RejectionMessageFactorySettingsFactoryResult empty");
        }

        var baseConnectorFactorySettings = baseConnectorFactorySettingsFactoryResult.getBaseConnectorFactorySettings();
        if (baseConnectorFactorySettings == null) {
            throw new EdcException("BaseConnectorFactorySettingsFactoryResult empty");
        }
        var baseConnectorFactory = new BaseConnectorFactory(baseConnectorFactorySettings, inboundProtocolVersionManager, connectorVersionProvider);

        var resourceCatalogFactory = new ResourceCatalogFactory();
        var connectorDescriptionService = new ConnectorDescriptionService(baseConnectorFactory, resourceCatalogFactory);

        var descriptionResponseMessageFactorySettings = descriptionResponseMessageFactorySettingsFactoryResult.getDescriptionResponseMessageFactorySettings();
        if (descriptionResponseMessageFactorySettings == null) {
            throw new EdcException("DescriptionResponseMessageFactorySettingsFactoryResult empty");
        }
        var descriptionResponseMessageFactory = new DescriptionResponseMessageFactory(descriptionResponseMessageFactorySettings, outboundProtocolVersionProvider);
        var connectorDescriptionRequestHandlerSettings = connectorDescriptionRequestHandlerSettingsFactoryResult.getConnectorDescriptionRequestHandlerSettings();
        if (connectorDescriptionRequestHandlerSettings == null) {
            throw new EdcException("ConnectorDescriptionRequestHandlerSettingsFactoryResult empty");
        }
        var connectorDescriptionRequestHandler = new ConnectorDescriptionRequestHandler(descriptionResponseMessageFactory, connectorDescriptionService, connectorDescriptionRequestHandlerSettings);

        var descriptionRequestHandler = new DescriptionRequestHandlerImpl();
        descriptionRequestHandler.add(null, connectorDescriptionRequestHandler);
        descriptionRequestHandler.add(IdsId.Type.CONNECTOR, connectorDescriptionRequestHandler);

        var multipartController = new MultipartController(monitor, identityService, Collections.singletonList(descriptionRequestHandler));

        webService.registerController(multipartController);
    }
}
