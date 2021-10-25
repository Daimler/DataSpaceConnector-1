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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class ResourceFactoryTest {

    private Asset asset;

    // mocks
    private OfferedAsset offeredAsset;
    private ContractOffer contractOffer;
    private ContractOfferFactory contractOfferFactory;
    private de.fraunhofer.iais.eis.ContractOffer idsContractOffer;

    @BeforeEach
    void setup() {
        asset = TestDataFactory.createAsset();
        offeredAsset = EasyMock.createMock(OfferedAsset.class);
        contractOffer = EasyMock.createMock(ContractOffer.class);
        idsContractOffer = EasyMock.createMock(de.fraunhofer.iais.eis.ContractOffer.class);
        contractOfferFactory = EasyMock.createMock(ContractOfferFactory.class);
    }

    @Test
    public void testResourceCreatedFromContractOffer() {
        // mock
        EasyMock.expect(offeredAsset.getAsset()).andReturn(asset);
        EasyMock.expect(contractOffer.getAssets()).andReturn(Collections.singletonList(offeredAsset)).anyTimes();
        EasyMock.expect(contractOfferFactory.createContractOffer(contractOffer))
                .andReturn(idsContractOffer);
        EasyMock.replay(offeredAsset, idsContractOffer, contractOffer, contractOfferFactory);

        // prepare
        ResourceFactory resourceFactory = new ResourceFactory();

        // invoke
        de.fraunhofer.iais.eis.Resource resource = resourceFactory.createResource(contractOffer, contractOfferFactory);

        // verify offers
        Assertions.assertEquals(1, resource.getContractOffer().size());

        // verify resource
        Assertions.assertEquals(1, resource.getRepresentation().size());

        // verify representation
        Representation representation = resource.getRepresentation().get(0);
        Assertions.assertEquals(1, representation.getInstance().size());
        Assertions.assertEquals(IdsId.representation(asset.getId()).toUri(), representation.getId());
        Assertions.assertEquals(TestDataFactory.IDS_ASSET_FILE_EXTENSION, representation.getMediaType().getFilenameExtension());

        // verify artifact
        Artifact artifact = (Artifact) representation.getInstance().get(0);
        Assertions.assertEquals(TestDataFactory.IDS_ASSET_FILE_NAME, artifact.getFileName());
        Assertions.assertEquals(IdsId.artifact(asset.getId()).toUri(), artifact.getId());
        Assertions.assertEquals(TestDataFactory.IDS_ASSET_BYTE_SIZE, artifact.getByteSize().intValue());

        // verify function calls
        EasyMock.verify(offeredAsset, idsContractOffer, contractOffer, contractOfferFactory);
    }

    @Test
    public void testResourceCreatedFromAsset() {
        // prepare
        ResourceFactory resourceFactory = new ResourceFactory();

        // invoke
        de.fraunhofer.iais.eis.Resource resource = resourceFactory.createResource(asset);

        //verify resource
        Assertions.assertEquals(1, resource.getRepresentation().size());
    }

    @Test
    public void testGetRepresentation() {
        // prepare
        ResourceFactory resourceFactory = new ResourceFactory();

        // invoke
        Representation representation = resourceFactory.getRepresentation(asset);

        // verify representation
        Assertions.assertNotNull(representation);
    }

    @Test
    public void testGetArtifact() {
        // prepare
        ResourceFactory resourceFactory = new ResourceFactory();

        // invoke
        Artifact artifact = resourceFactory.getArtifact(asset);

        // verify artifact
        Assertions.assertNotNull(artifact);
    }
}
