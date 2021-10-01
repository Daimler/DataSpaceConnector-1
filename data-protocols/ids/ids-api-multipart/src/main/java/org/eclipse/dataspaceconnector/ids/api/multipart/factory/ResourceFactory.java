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
import de.fraunhofer.iais.eis.util.TypedLiteral;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.OfferedAsset;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class ResourceFactory {

    private final ContractOfferFactory contractOfferFactory;

    private static final String UNKNOWN_FILE_EXTENSION = "bin";

    public ResourceFactory(ContractOfferFactory contractOfferFactory) {
        this.contractOfferFactory = contractOfferFactory;
    }

    /**
     * Maps the EDC ContractOffer to an IDS Resource:
     * - ContractOffer    ->      1 IDS Resource
     * - X Assets         ->      X IDS Representation+Artifact pairs
     *
     * <p>Please note, that an IDS Resource may only be created, if the whole EDC ContractOffer is map-able to IDS.
     *
     * @param contractOffer from the EDC model
     * @return IDS resource
     */
    public Optional<Resource> createResource(final ContractOffer contractOffer) {

        // first validate whether the offer details are map-able to IDS at all
        final Optional<de.fraunhofer.iais.eis.ContractOffer> idsOffer = contractOfferFactory.createContractOffer(contractOffer);
        if (idsOffer.isEmpty()) return Optional.empty();

        ArrayList<Representation> representations = new ArrayList<>();
        for (OfferedAsset offeredAsset : contractOffer.getAssets()) {
            Asset asset = offeredAsset.getAsset();

            // artifact
            // the artifact URI must be the similar as the one, the ContractOfferFramework generates for targets
            final ArtifactBuilder artifactBuilder = new ArtifactBuilder(IdsId.artifact(asset.getId()).toUri());
            Optional.ofNullable(asset.getFileName()).ifPresent(artifactBuilder::_fileName_);
            Optional.ofNullable(asset.getByteSize()).map(BigInteger::valueOf).ifPresent(artifactBuilder::_byteSize_);
            final Artifact artifact = artifactBuilder.build();

            // representation
            final RepresentationBuilder representationBuilder = new RepresentationBuilder(IdsId.representation(asset.getId()).toUri());
            Optional.ofNullable(asset.getFileExtension()).ifPresent(type -> representationBuilder._mediaType_(createMediaType(type)));
            representationBuilder._instance_(new ArrayList<>(Collections.singletonList(artifact)));
            final Representation representation = representationBuilder.build();

            representations.add(representation);
        }


        //  resource
        // set no ID, so that another connector may not send description requests, as we would not be able to map them back to the correct offer
        final ResourceBuilder resourceBuilder = new ResourceBuilder();
        resourceBuilder._representation_(representations);
        resourceBuilder._contractOffer_(new ArrayList<>(Collections.singletonList(idsOffer.get())));

        return Optional.of(resourceBuilder.build());
    }

    private static MediaType createMediaType(final String fileExtension) {
        final String type = Optional.ofNullable(fileExtension).orElse(UNKNOWN_FILE_EXTENSION);
        return new CustomMediaTypeBuilder()._filenameExtension_(type).build();
    }

    private static ArrayList<TypedLiteral> asLiterals(final String text) {
        final TypedLiteral literal = new TypedLiteral();
        literal.setValue(text);
        return new ArrayList<>(Collections.singletonList(literal));
    }
}
