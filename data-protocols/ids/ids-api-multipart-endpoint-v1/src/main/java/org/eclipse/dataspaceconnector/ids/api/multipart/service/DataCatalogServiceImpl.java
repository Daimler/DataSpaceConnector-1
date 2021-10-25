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

import de.fraunhofer.iais.eis.Catalog;
import de.fraunhofer.iais.eis.Resource;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.ResourceCatalogFactory;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.ResourceFactory;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The IDS service is able to create IDS compliant descriptions of resources.
 * These descriptions may be used to create a self-description or answer a Description Request Message.
 */
public class DataCatalogServiceImpl implements DataCatalogService {
    private final Monitor monitor;
    private final AssetIndex assetIndex;

    public DataCatalogServiceImpl(Monitor monitor, AssetIndex assetIndex) {
        this.monitor = monitor;
        this.assetIndex = assetIndex;
    }

    /**
     * Provides the connector object, which may be used by the IDS self-description of the connector.
     *
     * @return connector description
     */
    @Override
    public Catalog createDataCatalog() {
        // factories
        ResourceFactory resourceFactory = new ResourceFactory();
        ResourceCatalogFactory resourceCatalogFactory = new ResourceCatalogFactory();

        Stream<Asset> assetStream = assetIndex.queryAssets(AssetSelectorExpression.Builder.newInstance().build());
        List<Resource> resources = assetStream.map(resourceFactory::createResource).collect(Collectors.toList());

        // connector description
        return resourceCatalogFactory.createResourceCatalogBuilder(resources);
    }
}
