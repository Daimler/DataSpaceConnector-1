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

package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResourceCatalogFactory {

    public ResourceCatalog createResourceCatalogBuilder(List<Resource> resources) {
        ResourceCatalogBuilder resourceCatalogBuilder = new ResourceCatalogBuilder();

        Optional.ofNullable(resources)
                .map(ArrayList::new)
                .ifPresent(resourceCatalogBuilder::_offeredResource_);

        return resourceCatalogBuilder.build();
    }
}
