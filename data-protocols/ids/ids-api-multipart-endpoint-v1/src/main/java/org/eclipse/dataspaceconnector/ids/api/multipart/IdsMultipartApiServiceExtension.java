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
import org.eclipse.dataspaceconnector.ids.api.multipart.controller.MultipartControllerSettingsFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.*;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.DescriptionHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.DescriptionHandlerSettingsFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.*;
import org.eclipse.dataspaceconnector.ids.api.multipart.service.*;
import org.eclipse.dataspaceconnector.ids.core.configuration.SettingResolver;
import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
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

        registerControllers(serviceExtensionContext);

        monitor.info(String.format("Initialized %s", NAME));
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
        var connectorVersionProvider = serviceExtensionContext.getService(ConnectorVersionProvider.class);

        // First create all objects that may return errors and ensure success
        var settingResolver = new SettingResolver(serviceExtensionContext);
        var multipartControllerSettingsFactory = new MultipartControllerSettingsFactory(settingResolver);
        var multipartControllerSettingsFactoryResult = multipartControllerSettingsFactory.createRejectionMessageFactorySettings();
        var artifactDescriptionRequestHandlerSettingsFactory = new ArtifactDescriptionRequestHandlerSettingsFactory(settingResolver);
        var artifactDescriptionRequestHandlerSettingsFactoryResult = artifactDescriptionRequestHandlerSettingsFactory.createArtifactDescriptionRequestHandlerSettings();
        var representationDescriptionRequestHandlerSettingsFactory = new RepresentationDescriptionRequestHandlerSettingsFactory(settingResolver);
        var representationDescriptionRequestHandlerSettingsFactoryResult = representationDescriptionRequestHandlerSettingsFactory.createRepresentationDescriptionRequestHandlerSettings();
        var resourceDescriptionRequestHandlerSettingsFactory = new ResourceDescriptionRequestHandlerSettingsFactory(settingResolver);
        var resourceDescriptionRequestHandlerSettingsFactoryResult = resourceDescriptionRequestHandlerSettingsFactory.createResourceDescriptionRequestHandlerSettings();
        var dataCatalogDescriptionRequestHandlerSettingsFactory = new DataCatalogDescriptionRequestHandlerSettingsFactory(settingResolver);
        var dataCatalogDescriptionRequestHandlerSettingsFactoryResult = dataCatalogDescriptionRequestHandlerSettingsFactory.createDataCatalogDescriptionRequestHandlerSettings();
        var descriptionHandlerSettingsFactory = new DescriptionHandlerSettingsFactory(settingResolver);
        var descriptionHandlerSettingsFactoryResult = descriptionHandlerSettingsFactory.createDescriptionHandlerSettings();
        var baseConnectorFactorySettingsFactory = new BaseConnectorFactorySettingsFactory(settingResolver);
        var baseConnectorFactorySettingsFactoryResult = baseConnectorFactorySettingsFactory.createBaseConnectorFactorySettings();
        var descriptionResponseMessageFactorySettingsFactory = new DescriptionResponseMessageFactorySettingsFactory(settingResolver);
        var descriptionResponseMessageFactorySettingsFactoryResult = descriptionResponseMessageFactorySettingsFactory.createDescriptionResponseMessageFactorySettings();
        var connectorDescriptionRequestHandlerSettingsFactory = new ConnectorDescriptionRequestHandlerSettingsFactory(settingResolver);
        var connectorDescriptionRequestHandlerSettingsFactoryResult = connectorDescriptionRequestHandlerSettingsFactory.createConnectorDescriptionRequestHandlerSettings();

        var allErrorsDistinct = new HashSet<String>();
        allErrorsDistinct.addAll(multipartControllerSettingsFactoryResult.getErrors());
        allErrorsDistinct.addAll(descriptionHandlerSettingsFactoryResult.getErrors());
        allErrorsDistinct.addAll(artifactDescriptionRequestHandlerSettingsFactoryResult.getErrors());
        allErrorsDistinct.addAll(representationDescriptionRequestHandlerSettingsFactoryResult.getErrors());
        allErrorsDistinct.addAll(resourceDescriptionRequestHandlerSettingsFactoryResult.getErrors());
        allErrorsDistinct.addAll(dataCatalogDescriptionRequestHandlerSettingsFactoryResult.getErrors());
        allErrorsDistinct.addAll(baseConnectorFactorySettingsFactoryResult.getErrors());
        allErrorsDistinct.addAll(descriptionResponseMessageFactorySettingsFactoryResult.getErrors());
        allErrorsDistinct.addAll(connectorDescriptionRequestHandlerSettingsFactoryResult.getErrors());
        if (!allErrorsDistinct.isEmpty()) {
            throw new EdcException(String.join(", ", allErrorsDistinct));
        }

        var baseConnectorFactorySettings = baseConnectorFactorySettingsFactoryResult.getBaseConnectorFactorySettings();
        if (baseConnectorFactorySettings == null) {
            throw new EdcException("BaseConnectorFactorySettingsFactoryResult empty");
        }
        var baseConnectorFactory = new BaseConnectorFactory(baseConnectorFactorySettings, connectorVersionProvider);

        var resourceCatalogFactory = new ResourceCatalogFactory();
        var connectorDescriptionService = new ConnectorDescriptionServiceImpl(baseConnectorFactory, resourceCatalogFactory);

        var descriptionResponseMessageFactorySettings = descriptionResponseMessageFactorySettingsFactoryResult.getDescriptionResponseMessageFactorySettings();
        if (descriptionResponseMessageFactorySettings == null) {
            throw new EdcException("DescriptionResponseMessageFactorySettingsFactoryResult empty");
        }
        var descriptionResponseMessageFactory = new DescriptionResponseMessageFactory(descriptionResponseMessageFactorySettings);
        var connectorDescriptionRequestHandlerSettings = connectorDescriptionRequestHandlerSettingsFactoryResult.getConnectorDescriptionRequestHandlerSettings();
        if (connectorDescriptionRequestHandlerSettings == null) {
            throw new EdcException("ConnectorDescriptionRequestHandlerSettingsFactoryResult empty");
        }

        var descriptionHandlerSettings = descriptionHandlerSettingsFactoryResult.getDescriptionHandlerSettings();
        var connectorDescriptionRequestHandler = new ConnectorDescriptionRequestHandler(descriptionResponseMessageFactory, connectorDescriptionService, connectorDescriptionRequestHandlerSettings);

        AssetIndex assetIndex = serviceExtensionContext.getService(AssetIndex.class);
        var artifactService = new ArtifactServiceImpl(monitor, assetIndex);
        var artifactDescriptionHandlerSettings = artifactDescriptionRequestHandlerSettingsFactoryResult.getArtifactDescriptionRequestHandlerSettings();
        var artifactDescriptionRequestHandler = new ArtifactDescriptionRequestHandler(artifactDescriptionHandlerSettings, artifactService, descriptionResponseMessageFactory);

        var dataCatalogService = new DataCatalogServiceImpl(monitor, assetIndex);
        var dataCatalogDescriptionHandlerSettings = dataCatalogDescriptionRequestHandlerSettingsFactoryResult.getDataCatalogDescriptionRequestHandlerSettings();
        var dataCatalogDescriptionRequestHandler = new DataCatalogDescriptionRequestHandler(dataCatalogDescriptionHandlerSettings, dataCatalogService, descriptionResponseMessageFactory);

        var representationService = new RepresentationServiceImpl(monitor, assetIndex);
        var representationDescriptionHandlerSettings = representationDescriptionRequestHandlerSettingsFactoryResult.getRepresentationDescriptionRequestHandlerSettings();
        var representationDescriptionRequestHandler = new RepresentationDescriptionRequestHandler(representationDescriptionHandlerSettings, representationService, descriptionResponseMessageFactory);

        var resourceService = new ResourceServiceImpl(monitor, assetIndex);
        var resourceDescriptionHandlerSettings = resourceDescriptionRequestHandlerSettingsFactoryResult.getResourceDescriptionRequestHandlerSettings();
        var resourceDescriptionRequestHandler = new ResourceDescriptionRequestHandler(resourceDescriptionHandlerSettings, resourceService, descriptionResponseMessageFactory);

        var descriptionRequestHandler = new DescriptionHandler(descriptionHandlerSettings,
                artifactDescriptionRequestHandler,
                dataCatalogDescriptionRequestHandler,
                representationDescriptionRequestHandler,
                resourceDescriptionRequestHandler,
                connectorDescriptionRequestHandler);

        var multipartControllerSettings = multipartControllerSettingsFactoryResult.getRejectionMessageFactorySettings();
        if (multipartControllerSettings == null) {
            throw new EdcException("RejectionMessageFactorySettingsFactoryResult empty");
        }
        var multipartController = new MultipartController(multipartControllerSettings, identityService, Collections.singletonList(descriptionRequestHandler));

        webService.registerController(multipartController);
    }

}
