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
import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResourceCatalogFactoryTest {

    private ResourceCatalogFactory resourceCatalogFactory;

    @BeforeEach
    void beforeEach() {
        resourceCatalogFactory = new ResourceCatalogFactory();
    }

    @Test
    void doesNotThrowOnArgumentNull() {
        Assertions.assertDoesNotThrow(() -> resourceCatalogFactory.createResourceCatalogBuilder(null));
    }

    @Test
    void addsResourcesToCatalog() {
        Resource expected = EasyMock.mock(Resource.class);
        List<Resource> expectedResources = new ArrayList<>(Collections.singletonList(expected));

        ResourceCatalog catalog = resourceCatalogFactory.createResourceCatalogBuilder(expectedResources);
        ArrayList<? extends Resource> resources = catalog.getOfferedResource();

        Assertions.assertNotNull(resources);
        Assertions.assertEquals(1, resources.size());
        Assertions.assertEquals(expected, resources.get(0));
    }
}
