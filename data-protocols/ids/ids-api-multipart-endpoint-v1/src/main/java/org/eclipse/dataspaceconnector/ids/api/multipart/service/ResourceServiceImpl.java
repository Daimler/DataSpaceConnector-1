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

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.Resource;
import org.eclipse.dataspaceconnector.ids.api.multipart.factory.ResourceFactory;
import org.eclipse.dataspaceconnector.ids.core.transform.TransformerRegistryImpl;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerRegistry;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

/**
 * The IDS service is able to create IDS compliant descriptions of resources.
 * These descriptions may be used to create a self-description or answer a Description Request Message.
 */
public class ResourceServiceImpl implements ResourceService {
    private final Monitor monitor;
    private final AssetIndex assetIndex;

    public ResourceServiceImpl(final Monitor monitor, final AssetIndex assetIndex) {
        this.monitor = monitor;
        this.assetIndex = assetIndex;
    }

    /**
     * Provides the connector object, which may be used by the IDS self-description of the connector.
     *
     * @return connector description
     */
    @Override
    public Resource createResource(@NotNull String id) {
        ResourceFactory resourceFactory = new ResourceFactory();

        Asset asset = assetIndex.findById(id);
        if (asset == null) {
            return null;
        }

        TransformerRegistry transformerRegistry = new TransformerRegistryImpl();
        return transformerRegistry.transform(asset, Resource.class).getOutput();
    }
}
