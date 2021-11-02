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
import org.eclipse.dataspaceconnector.ids.api.multipart.controller.MultipartControllerSettings;
import org.eclipse.dataspaceconnector.ids.api.multipart.controller.MultipartControllerSettingsFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.controller.MultipartControllerSettingsFactoryResult;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.DescriptionHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.DescriptionHandlerSettings;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.Handler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ArtifactDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ArtifactDescriptionRequestHandlerSettings;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ConnectorDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ConnectorDescriptionRequestHandlerSettings;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.DataCatalogDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.DataCatalogDescriptionRequestHandlerSettings;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.RepresentationDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.RepresentationDescriptionRequestHandlerSettings;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ResourceDescriptionRequestHandler;
import org.eclipse.dataspaceconnector.ids.api.multipart.handler.description.ResourceDescriptionRequestHandlerSettings;
import org.eclipse.dataspaceconnector.ids.core.configuration.SettingResolver;
import org.eclipse.dataspaceconnector.ids.spi.service.ConnectorService;
import org.eclipse.dataspaceconnector.ids.spi.service.DataCatalogService;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerRegistry;
import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.protocol.web.WebService;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * ServiceExtension providing IDS multipart related API controllers
 */
public final class IdsMultipartApiServiceExtension implements ServiceExtension {
    private static final String NAME = "IDS Multipart API extension";

    private static final String[] REQUIRES = {
            IdentityService.FEATURE,
            "edc:ids:core",
            "edc:ids:transform:v1"
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
        WebService webService = serviceExtensionContext.getService(WebService.class);
        IdentityService identityService = serviceExtensionContext.getService(IdentityService.class);
        ConnectorVersionProvider connectorVersionProvider = serviceExtensionContext.getService(ConnectorVersionProvider.class);

        DataCatalogService dataCatalogService = serviceExtensionContext.getService(DataCatalogService.class);
        ConnectorService connectorService = serviceExtensionContext.getService(ConnectorService.class);
        AssetIndex assetIndex = serviceExtensionContext.getService(AssetIndex.class);
        TransformerRegistry transformerRegistry = serviceExtensionContext.getService(TransformerRegistry.class);
        SettingResolver settingResolver = new SettingResolver(serviceExtensionContext);

        List<Handler> handlers = new LinkedList<>();
        MultipartController multipartController = createMultipartController(settingResolver, identityService, handlers);
        webService.registerController(multipartController);


        DescriptionHandler descriptionHandler = createDescriptionHandler(
                settingResolver,
                connectorService,
                dataCatalogService,
                assetIndex,
                transformerRegistry
        );
        handlers.add(descriptionHandler);


//        // First create all objects that may return errors and ensure success
//        SettingResolver settingResolver = new SettingResolver(serviceExtensionContext);
//
//        ArtifactDescriptionRequestHandlerSettingsFactory artifactDescriptionRequestHandlerSettingsFactory = new ArtifactDescriptionRequestHandlerSettingsFactory(settingResolver);
//        ArtifactDescriptionRequestHandlerSettingsFactoryResult artifactDescriptionRequestHandlerSettingsFactoryResult = artifactDescriptionRequestHandlerSettingsFactory.createArtifactDescriptionRequestHandlerSettings();
//
//        DescriptionResponseMessageFactorySettingsFactory descriptionResponseMessageFactorySettingsFactory = new DescriptionResponseMessageFactorySettingsFactory(settingResolver);
//        DescriptionResponseMessageFactorySettingsFactoryResult descriptionResponseMessageFactorySettingsFactoryResult = descriptionResponseMessageFactorySettingsFactory.createDescriptionResponseMessageFactorySettings();
//        ConnectorDescriptionRequestHandlerSettingsFactory connectorDescriptionRequestHandlerSettingsFactory = new ConnectorDescriptionRequestHandlerSettingsFactory(settingResolver);
//        ConnectorDescriptionRequestHandlerSettingsFactoryResult connectorDescriptionRequestHandlerSettingsFactoryResult = connectorDescriptionRequestHandlerSettingsFactory.createConnectorDescriptionRequestHandlerSettings();
//
//        Set<String> allErrorsDistinct = new HashSet<String>();
//        allErrorsDistinct.addAll(artifactDescriptionRequestHandlerSettingsFactoryResult.getErrors());
//        allErrorsDistinct.addAll(descriptionResponseMessageFactorySettingsFactoryResult.getErrors());
//        allErrorsDistinct.addAll(connectorDescriptionRequestHandlerSettingsFactoryResult.getErrors());
//        if (!allErrorsDistinct.isEmpty()) {
//            throw new EdcException(String.join(", ", allErrorsDistinct));
//        }
//
//        BaseConnectorFactorySettingsFactory baseConnectorFactorySettingsFactory = new BaseConnectorFactorySettingsFactory(settingResolver);
//        BaseConnectorFactorySettingsFactoryResult baseConnectorFactorySettingsFactoryResult = baseConnectorFactorySettingsFactory.createBaseConnectorFactorySettings();
//        BaseConnectorFactorySettings baseConnectorFactorySettings = baseConnectorFactorySettingsFactoryResult.getBaseConnectorFactorySettings();
//        if (baseConnectorFactorySettings == null) {
//            throw new EdcException("BaseConnectorFactorySettingsFactoryResult empty");
//        }
//        BaseConnectorFactory baseConnectorFactory = new BaseConnectorFactory(baseConnectorFactorySettings, connectorVersionProvider);
//
//        ConnectorService connectorService = new ConnectorServiceImpl(baseConnectorFactory, resourceCatalogFactory);
//
//        DescriptionResponseMessageFactorySettings descriptionResponseMessageFactorySettings = descriptionResponseMessageFactorySettingsFactoryResult.getDescriptionResponseMessageFactorySettings();
//        if (descriptionResponseMessageFactorySettings == null) {
//            throw new EdcException("DescriptionResponseMessageFactorySettingsFactoryResult empty");
//        }
//
//        DescriptionResponseMessageFactory descriptionResponseMessageFactory = new DescriptionResponseMessageFactory(descriptionResponseMessageFactorySettings, transformerRegistry);
//        ConnectorDescriptionRequestHandlerSettings connectorDescriptionRequestHandlerSettings = connectorDescriptionRequestHandlerSettingsFactoryResult.getConnectorDescriptionRequestHandlerSettings();
//        if (connectorDescriptionRequestHandlerSettings == null) {
//            throw new EdcException("ConnectorDescriptionRequestHandlerSettingsFactoryResult empty");
//        }
//
//        ConnectorDescriptionRequestHandler connectorDescriptionRequestHandler = new ConnectorDescriptionRequestHandler(descriptionResponseMessageFactory, connectorService, connectorDescriptionRequestHandlerSettings);
//        ArtifactDescriptionRequestHandler artifactDescriptionRequestHandler = createArtifactDescriptionRequestHandler(settingResolver, transformerRegistry, descriptionResponseMessageFactory, assetIndex);
//        DataCatalogDescriptionRequestHandler dataCatalogDescriptionRequestHandler = createDataCatalogDescriptionRequestHandler(settingResolver, transformerRegistry, descriptionResponseMessageFactory, assetIndex);
//        RepresentationDescriptionRequestHandler representationDescriptionRequestHandler = createRepresentationDescriptionRequestHandler(settingResolver, transformerRegistry, descriptionResponseMessageFactory, assetIndex);
//        ResourceDescriptionRequestHandler resourceDescriptionRequestHandler = createResourceDescriptionRequestHandler(settingResolver, transformerRegistry, descriptionResponseMessageFactory, assetIndex);
//
//        DescriptionHandler descriptionRequestHandler = createDescriptionHandler(
//                settingResolver,
//                transformerRegistry,
//                artifactDescriptionRequestHandler,
//                dataCatalogDescriptionRequestHandler,
//                representationDescriptionRequestHandler,
//                resourceDescriptionRequestHandler,
//                connectorDescriptionRequestHandler);
//
//        MultipartController multipartController = createMultipartController(settingResolver, identityService, Collections.singletonList(descriptionRequestHandler));
//
//        webService.registerController(multipartController);
    }

    private DescriptionHandler createDescriptionHandler(
            SettingResolver settingResolver,
            ConnectorService connectorService,
            DataCatalogService dataCatalogService,
            AssetIndex assetIndex,
            TransformerRegistry transformerRegistry) {
        DescriptionHandlerSettings descriptionHandlerSettings = null; // TODO

        ArtifactDescriptionRequestHandler artifactDescriptionRequestHandler = createArtifactDescriptionRequestHandler(settingResolver, assetIndex, transformerRegistry);
        DataCatalogDescriptionRequestHandler dataCatalogDescriptionRequestHandler = createDataCatalogDescriptionRequestHandler(settingResolver, dataCatalogService, transformerRegistry);
        RepresentationDescriptionRequestHandler representationDescriptionRequestHandler = createRepresentationDescriptionRequestHandler(settingResolver, assetIndex, transformerRegistry);
        ResourceDescriptionRequestHandler resourceDescriptionRequestHandler = createResourceDescriptionRequestHandler(settingResolver, assetIndex, transformerRegistry);
        ConnectorDescriptionRequestHandler connectorDescriptionRequestHandler = createConnectorDescriptionRequestHandler(settingResolver, connectorService, transformerRegistry);

        DescriptionHandler descriptionRequestHandler = new DescriptionHandler(
                monitor,
                descriptionHandlerSettings,
                transformerRegistry,
                artifactDescriptionRequestHandler,
                dataCatalogDescriptionRequestHandler,
                representationDescriptionRequestHandler,
                resourceDescriptionRequestHandler,
                connectorDescriptionRequestHandler);

        return descriptionRequestHandler;
    }

    private ConnectorDescriptionRequestHandler createConnectorDescriptionRequestHandler(
            SettingResolver settingResolver,
            ConnectorService connectorService,
            TransformerRegistry transformerRegistry) {
        ConnectorDescriptionRequestHandlerSettings connectorDescriptionRequestHandlerSettings = null; // TODO

        ConnectorDescriptionRequestHandler connectorDescriptionRequestHandler = new ConnectorDescriptionRequestHandler(
                monitor,
                connectorDescriptionRequestHandlerSettings,
                connectorService,
                transformerRegistry
        );

        return connectorDescriptionRequestHandler;
    }

    private ResourceDescriptionRequestHandler createResourceDescriptionRequestHandler(
            SettingResolver settingResolver,
            AssetIndex assetIndex,
            TransformerRegistry transformerRegistry) {
        ResourceDescriptionRequestHandlerSettings resourceDescriptionRequestHandlerSettings = null; // TODO

        ResourceDescriptionRequestHandler resourceDescriptionRequestHandler = new ResourceDescriptionRequestHandler(
                monitor,
                resourceDescriptionRequestHandlerSettings,
                assetIndex,
                transformerRegistry
        );

        return resourceDescriptionRequestHandler;
    }

    private RepresentationDescriptionRequestHandler createRepresentationDescriptionRequestHandler(
            SettingResolver settingResolver,
            AssetIndex assetIndex,
            TransformerRegistry transformerRegistry) {
        RepresentationDescriptionRequestHandlerSettings representationDescriptionRequestHandlerSettings = null; // TODO

        RepresentationDescriptionRequestHandler representationDescriptionRequestHandler = new RepresentationDescriptionRequestHandler(
                monitor,
                representationDescriptionRequestHandlerSettings,
                assetIndex,
                transformerRegistry
        );

        return representationDescriptionRequestHandler;
    }

    private DataCatalogDescriptionRequestHandler createDataCatalogDescriptionRequestHandler(
            SettingResolver settingResolver,
            DataCatalogService dataCatalogService,
            TransformerRegistry transformerRegistry) {
        DataCatalogDescriptionRequestHandlerSettings dataCatalogDescriptionRequestHandlerSettings = null; // TODO

        DataCatalogDescriptionRequestHandler dataCatalogDescriptionRequestHandler = new DataCatalogDescriptionRequestHandler(
                monitor, dataCatalogDescriptionRequestHandlerSettings, dataCatalogService, transformerRegistry
        );

        return dataCatalogDescriptionRequestHandler;
    }

    private ArtifactDescriptionRequestHandler createArtifactDescriptionRequestHandler(
            SettingResolver settingResolver,
            AssetIndex assetIndex,
            TransformerRegistry transformerRegistry
    ) {
        ArtifactDescriptionRequestHandlerSettings artifactDescriptionRequestHandlerSettings = null;

        ArtifactDescriptionRequestHandler artifactDescriptionRequestHandler = new ArtifactDescriptionRequestHandler(
                monitor,
                artifactDescriptionRequestHandlerSettings,
                assetIndex,
                transformerRegistry
        );

        return artifactDescriptionRequestHandler;
    }

    private MultipartController createMultipartController(SettingResolver settingResolver, IdentityService identityService, List<Handler> handlers) {
        MultipartControllerSettingsFactory multipartControllerSettingsFactory = new MultipartControllerSettingsFactory(settingResolver);
        MultipartControllerSettingsFactoryResult multipartControllerSettingsFactoryResult = multipartControllerSettingsFactory.createRejectionMessageFactorySettings();
        MultipartControllerSettings multipartControllerSettings = multipartControllerSettingsFactoryResult.getRejectionMessageFactorySettings();

        if (multipartControllerSettings == null) {
            throw new EdcException("RejectionMessageFactorySettingsFactoryResult empty");
        }

        return new MultipartController(multipartControllerSettings, identityService, handlers);
    }
//
//    private DescriptionHandler createDescriptionHandler(
//            SettingResolver settingResolver,
//            TransformerRegistry transformerRegistry,
//            ArtifactDescriptionRequestHandler artifactDescriptionRequestHandler,
//            DataCatalogDescriptionRequestHandler dataCatalogDescriptionRequestHandler,
//            RepresentationDescriptionRequestHandler representationDescriptionRequestHandler,
//            ResourceDescriptionRequestHandler resourceDescriptionRequestHandler,
//            ConnectorDescriptionRequestHandler connectorDescriptionRequestHandler
//    ) {
//        DescriptionHandlerSettingsFactory descriptionHandlerSettingsFactory = new DescriptionHandlerSettingsFactory(settingResolver);
//        DescriptionHandlerSettingsFactoryResult descriptionHandlerSettingsFactoryResult = descriptionHandlerSettingsFactory.createDescriptionHandlerSettings();
//
//        if (descriptionHandlerSettingsFactoryResult == null) { // TODO rework
//            throw new EdcException("DescriptionHandlerSettingsFactoryResult empty");
//        }
//
//        DescriptionHandlerSettings descriptionHandlerSettings = descriptionHandlerSettingsFactoryResult.getDescriptionHandlerSettings();
//
//        return new DescriptionHandler(
//                monitor,
//                descriptionHandlerSettings,
//                transformerRegistry,
//                artifactDescriptionRequestHandler,
//                dataCatalogDescriptionRequestHandler,
//                representationDescriptionRequestHandler,
//                resourceDescriptionRequestHandler,
//                connectorDescriptionRequestHandler);
//    }
//
//    private ArtifactDescriptionRequestHandler createArtifactDescriptionRequestHandler(
//            SettingResolver settingResolver,
//            TransformerRegistry transformerRegistry,
//            DescriptionResponseMessageFactory descriptionResponseMessageFactory,
//            AssetIndex assetIndex) {
//        ArtifactDescriptionRequestHandlerSettingsFactory artifactDescriptionRequestHandlerSettingsFactory = new ArtifactDescriptionRequestHandlerSettingsFactory(settingResolver);
//        ArtifactDescriptionRequestHandlerSettingsFactoryResult artifactDescriptionRequestHandlerSettingsFactoryResult = artifactDescriptionRequestHandlerSettingsFactory.createArtifactDescriptionRequestHandlerSettings();
//        ArtifactDescriptionRequestHandlerSettings artifactDescriptionHandlerSettings = artifactDescriptionRequestHandlerSettingsFactoryResult.getArtifactDescriptionRequestHandlerSettings();
//        ArtifactService artifactService = new ArtifactServiceImpl(monitor, assetIndex);
//
//        return new ArtifactDescriptionRequestHandler(artifactDescriptionHandlerSettings, artifactService, transformerRegistry, descriptionResponseMessageFactory);
//    }
//
//    private DataCatalogDescriptionRequestHandler createDataCatalogDescriptionRequestHandler(
//            SettingResolver settingResolver,
//            TransformerRegistry transformerRegistry,
//            DescriptionResponseMessageFactory descriptionResponseMessageFactory,
//            AssetIndex assetIndex) {
//        DataCatalogDescriptionRequestHandlerSettingsFactory dataCatalogDescriptionRequestHandlerSettingsFactory = new DataCatalogDescriptionRequestHandlerSettingsFactory(settingResolver);
//        DataCatalogDescriptionRequestHandlerSettingsFactoryResult dataCatalogDescriptionRequestHandlerSettingsFactoryResult = dataCatalogDescriptionRequestHandlerSettingsFactory.createDataCatalogDescriptionRequestHandlerSettings();
//        DataCatalogDescriptionRequestHandlerSettings dataCatalogDescriptionHandlerSettings = dataCatalogDescriptionRequestHandlerSettingsFactoryResult.getDataCatalogDescriptionRequestHandlerSettings();
//        DataCatalogService dataCatalogService = new DataCatalogServiceImpl(monitor, assetIndex);
//
//        return new DataCatalogDescriptionRequestHandler(dataCatalogDescriptionHandlerSettings, dataCatalogService, transformerRegistry, descriptionResponseMessageFactory);
//    }
//
//    private RepresentationDescriptionRequestHandler createRepresentationDescriptionRequestHandler(
//            SettingResolver settingResolver,
//            TransformerRegistry transformerRegistry,
//            DescriptionResponseMessageFactory descriptionResponseMessageFactory,
//            AssetIndex assetIndex) {
//        RepresentationDescriptionRequestHandlerSettingsFactory representationDescriptionRequestHandlerSettingsFactory = new RepresentationDescriptionRequestHandlerSettingsFactory(settingResolver);
//        RepresentationDescriptionRequestHandlerSettingsFactoryResult representationDescriptionRequestHandlerSettingsFactoryResult = representationDescriptionRequestHandlerSettingsFactory.createRepresentationDescriptionRequestHandlerSettings();
//        RepresentationDescriptionRequestHandlerSettings representationDescriptionHandlerSettings = representationDescriptionRequestHandlerSettingsFactoryResult.getRepresentationDescriptionRequestHandlerSettings();
//
//        RepresentationService representationService = new RepresentationServiceImpl(monitor, assetIndex);
//
//        return new RepresentationDescriptionRequestHandler(representationDescriptionHandlerSettings, representationService, transformerRegistry, descriptionResponseMessageFactory);
//    }
//
//    private ResourceDescriptionRequestHandler createResourceDescriptionRequestHandler(
//            SettingResolver settingResolver,
//            TransformerRegistry transformerRegistry,
//            DescriptionResponseMessageFactory descriptionResponseMessageFactory,
//            AssetIndex assetIndex) {
//        ResourceDescriptionRequestHandlerSettingsFactory resourceDescriptionRequestHandlerSettingsFactory = new ResourceDescriptionRequestHandlerSettingsFactory(settingResolver);
//        ResourceDescriptionRequestHandlerSettingsFactoryResult resourceDescriptionRequestHandlerSettingsFactoryResult = resourceDescriptionRequestHandlerSettingsFactory.createResourceDescriptionRequestHandlerSettings();
//        ResourceDescriptionRequestHandlerSettings resourceDescriptionHandlerSettings = resourceDescriptionRequestHandlerSettingsFactoryResult.getResourceDescriptionRequestHandlerSettings();
//
//        if (!resourceDescriptionRequestHandlerSettingsFactoryResult.getErrors().isEmpty()) {
//            throw new EdcException(String.format("Could not set up ResourceDescriptionRequestHandler: %s", String.join(", ", resourceDescriptionRequestHandlerSettingsFactoryResult.getErrors())));
//        }
//
//        ResourceService resourceService = new ResourceServiceImpl(monitor, assetIndex);
//
//        return new ResourceDescriptionRequestHandler(resourceDescriptionHandlerSettings, resourceService, transformerRegistry, descriptionResponseMessageFactory);
//    }
}
