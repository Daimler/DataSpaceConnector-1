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
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalog;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.BaseConnectorFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.ContractOfferFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.ResourceCatalogFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.ResourceFactory;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.InboundProtocolVersionManager;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferQuery;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferQueryResponse;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferService;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The IDS service is able to create IDS compliant descriptions of resources.
 * These descriptions may be used to create a self-description or answer a Description Request Message.
 */
public class SelfDescriptionService {
    private final ContractOfferService contractOfferService;
    private final ConfigurationProvider configurationProvider;
    private final InboundProtocolVersionManager inboundProtocolVersionManager;
    private final ConnectorVersionProvider connectorVersionProvider;
    private final Monitor monitor;

    public SelfDescriptionService(final Monitor monitor,
                                  final ContractOfferService contractOfferService,
                                  final ConfigurationProvider configurationProvider,
                                  final InboundProtocolVersionManager inboundProtocolVersionManager,
                                  final ConnectorVersionProvider connectorVersionProvider) {
        this.monitor = monitor;
        this.contractOfferService = contractOfferService;
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
        // factories
        final BaseConnectorFactory baseConnectorBuilderFactory = new BaseConnectorFactory(configurationProvider, inboundProtocolVersionManager, connectorVersionProvider);
        final ContractOfferFactory contractOfferFactory = new ContractOfferFactory(monitor);
        final ResourceFactory resourceFactory = new ResourceFactory(contractOfferFactory);
        final ResourceCatalogFactory resourceCatalogFactory = new ResourceCatalogFactory();

        // resources
        final ContractOfferQuery contractOfferQuery = ContractOfferQuery.builder().build();
        final ContractOfferQueryResponse contractOfferQueryResponse = contractOfferService.queryContractOffers(contractOfferQuery);
        final Stream<ContractOffer> contractOffers = contractOfferQueryResponse.getContractOfferStream();

        final List<Resource> resources = contractOffers
                .map(resourceFactory::createResource)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableList());

        // connector description
        final ResourceCatalog resourceCatalog = resourceCatalogFactory.createResourceCatalogBuilder(resources);

        return baseConnectorBuilderFactory.createBaseConnector(resourceCatalog);
    }
}
