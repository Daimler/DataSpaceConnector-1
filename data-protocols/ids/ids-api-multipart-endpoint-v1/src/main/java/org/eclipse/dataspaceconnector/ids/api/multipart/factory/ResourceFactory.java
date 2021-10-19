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
import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.CustomMediaTypeBuilder;
import de.fraunhofer.iais.eis.MediaType;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.types.domain.IdsAsset;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.OfferedAsset;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

public class ResourceFactory {

    private final ContractOfferFactory contractOfferFactory;

    public ResourceFactory(ContractOfferFactory contractOfferFactory) {
        this.contractOfferFactory = contractOfferFactory;
    }

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
    public Resource createResource(ContractOffer contractOffer) {

        // first validate whether the offer details are map-able to IDS at all
        de.fraunhofer.iais.eis.ContractOffer idsOffer = contractOfferFactory.createContractOffer(contractOffer);
        if (idsOffer == null) {
            return null;
        }

        ArrayList<Representation> representations = new ArrayList<>();
        for (OfferedAsset offeredAsset : contractOffer.getAssets()) {
            Asset asset = offeredAsset.getAsset();
            IdsAsset idsAsset = IdsAsset.Builder.newInstance(asset).build();

            // artifact
            // the artifact URI must be the similar as the one, the ContractOfferFramework generates for targets
            ArtifactBuilder artifactBuilder = new ArtifactBuilder(IdsId.artifact(asset.getId()).toUri());

            String fileName = idsAsset.getFileName();
            if (fileName != null) {
                artifactBuilder._fileName_(fileName);
            }

            Integer byteSize = idsAsset.getByteSize();
            if (byteSize != null) {
                artifactBuilder._byteSize_(BigInteger.valueOf(byteSize));
            }

            Artifact artifact = artifactBuilder.build();

            // representation
            RepresentationBuilder representationBuilder = new RepresentationBuilder(IdsId.representation(asset.getId()).toUri());

            String fileExtension = idsAsset.getFileExtension();
            if (fileExtension != null) {
                representationBuilder._mediaType_(createMediaType(fileExtension));
            }

            representationBuilder._instance_(new ArrayList<>(Collections.singletonList(artifact)));
            Representation representation = representationBuilder.build();

            representations.add(representation);
        }

        //  resource
        ResourceBuilder resourceBuilder = new ResourceBuilder();
        resourceBuilder._representation_(representations);
        resourceBuilder._contractOffer_(new ArrayList<>(Collections.singletonList(idsOffer)));

        return resourceBuilder.build();
    }

    private static MediaType createMediaType(@NotNull String fileExtension) {
        return new CustomMediaTypeBuilder()._filenameExtension_(fileExtension).build();
    }
}
