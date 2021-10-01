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

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.Representation;
import org.easymock.EasyMock;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.OfferedAsset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

public class ResourceFactoryTest {

    private static final class Fixtures {
        private static final String ASSET_ID = "162147a7-2c45-4b48-b4bf-8dcf8f9fc3d5";
        private static final String ASSET_FILE_EXTENSION = "txt";
        private static final int ASSET_BYTE_SIZE = 1000;
        private static final String ASSET_FILE_NAME = "test";
    }

    // mocks
    private Asset asset;
    private OfferedAsset offeredAsset;
    private ContractOffer contractOffer;
    private ContractOfferFactory contractOfferFactory;
    private de.fraunhofer.iais.eis.ContractOffer idsContractOffer;

    @BeforeEach
    void setup() {
        asset = EasyMock.createMock(Asset.class);
        offeredAsset = EasyMock.createMock(OfferedAsset.class);
        contractOffer = EasyMock.createMock(ContractOffer.class);
        idsContractOffer = EasyMock.createMock(de.fraunhofer.iais.eis.ContractOffer.class);
        contractOfferFactory = EasyMock.createMock(ContractOfferFactory.class);

        EasyMock.expect(asset.getId()).andReturn(Fixtures.ASSET_ID).anyTimes();
        EasyMock.expect(asset.getFileName()).andReturn(Fixtures.ASSET_FILE_NAME).anyTimes();
        EasyMock.expect(asset.getFileExtension()).andReturn(Fixtures.ASSET_FILE_EXTENSION).anyTimes();
        EasyMock.expect(asset.getByteSize()).andReturn(Fixtures.ASSET_BYTE_SIZE).anyTimes();

        EasyMock.expect(offeredAsset.getAsset()).andReturn(asset);
        EasyMock.expect(contractOffer.getAssets()).andReturn(Collections.singletonList(offeredAsset)).anyTimes();

        EasyMock.expect(contractOfferFactory.createContractOffer(contractOffer))
                .andReturn(Optional.of(idsContractOffer));

        EasyMock.replay(asset, offeredAsset, idsContractOffer, contractOffer, contractOfferFactory);
    }

    @AfterEach
    public void teardown() {
        EasyMock.verify(asset, offeredAsset, idsContractOffer, contractOffer, contractOfferFactory);
    }

    @Test
    public void testResourceCreatedAsExpected() {

        // prepare
        final ResourceFactory resourceFactory = new ResourceFactory(contractOfferFactory);

        // invoke
        de.fraunhofer.iais.eis.Resource resource = resourceFactory.createResource(contractOffer).get();

        // verify offers
        Assertions.assertEquals(1, resource.getContractOffer().size());

        // verify resource
        Assertions.assertEquals(1, resource.getRepresentation().size());

        // verify representation
        final Representation representation = resource.getRepresentation().get(0);
        Assertions.assertEquals(1, representation.getInstance().size());
        Assertions.assertEquals(IdsId.representation(asset.getId()).toUri(), representation.getId());
        Assertions.assertEquals(Fixtures.ASSET_FILE_EXTENSION, representation.getMediaType().getFilenameExtension());

        // verify artifact
        final Artifact artifact = (Artifact) representation.getInstance().get(0);
        Assertions.assertEquals(Fixtures.ASSET_FILE_NAME, artifact.getFileName());
        Assertions.assertEquals(IdsId.artifact(asset.getId()).toUri(), artifact.getId());
        Assertions.assertEquals(Fixtures.ASSET_BYTE_SIZE, artifact.getByteSize().intValue());
    }
}
