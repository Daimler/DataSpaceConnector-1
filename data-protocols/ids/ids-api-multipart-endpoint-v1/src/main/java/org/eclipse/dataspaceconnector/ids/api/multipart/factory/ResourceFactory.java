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

import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import org.eclipse.dataspaceconnector.ids.core.transform.TransformerRegistryImpl;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerRegistry;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.OfferedAsset;

import java.util.ArrayList;
import java.util.Collections;

public class ResourceFactory {

    public ResourceFactory() {}

    /**
     * Maps the EDC ContractOffer to an IDS Resource.
     * <p>
     * E.g. 1 EDC ContractOffer with 5 Assets is mapped to 1 IDS Resource with 1 Offer and 5 Representation+Artifact pairs.
     * <p>
     * Please note, that an IDS Resource may only be created, if the whole EDC ContractOffer (including all permissions, constraints, etc.) is map-able to IDS.
     *
     * @param contractOffer from the EDC model
     * @return IDS resource
     */
    public Resource createResource(ContractOffer contractOffer, ContractOfferFactory contractOfferFactory) {
        // first validate whether the offer details are map-able to IDS at all
        de.fraunhofer.iais.eis.ContractOffer idsOffer = contractOfferFactory.createContractOffer(contractOffer);
        if (idsOffer == null) {
            return null;
        }

        TransformerRegistry transformerRegistry = new TransformerRegistryImpl();

        ArrayList<Representation> representations = new ArrayList<>();
        for (OfferedAsset offeredAsset : contractOffer.getAssets()) {
            Asset asset = offeredAsset.getAsset();
            Representation representation = transformerRegistry.transform(asset, Representation.class).getOutput();
            representations.add(representation);
        }

        ResourceBuilder resourceBuilder = new ResourceBuilder();
        resourceBuilder._representation_(representations);
        resourceBuilder._contractOffer_(new ArrayList<>(Collections.singletonList(idsOffer)));

        return resourceBuilder.build();
    }
}
