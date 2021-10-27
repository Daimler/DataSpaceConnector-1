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
 *       Daimler TSS GmbH - Initial Implementation
 *
 */

package org.eclipse.dataspaceconnector.ids.transform;

import de.fraunhofer.iais.eis.*;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.ids.spi.types.domain.IdsAsset;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class AssetToRepresentationTransformer implements IdsTypeTransformer<Asset, Representation> {

    @Override
    public Class<Asset> getInputType() {
        return Asset.class;
    }

    @Override
    public Class<Representation> getOutputType() {
        return Representation.class;
    }

    @Override
    public @Nullable Representation transform(Asset object, TransformerContext context) {
        Artifact result = context.transform(object, Artifact.class);

        RepresentationBuilder representationBuilder = new RepresentationBuilder(IdsId.representation(object.getId()).toUri());

        IdsAsset idsAsset = IdsAsset.Builder.newInstance(object).build();
        String fileExtension = idsAsset.getFileExtension();
        if (fileExtension != null) {
            representationBuilder._mediaType_(createMediaType(fileExtension));
        }

        representationBuilder._instance_(new ArrayList<>(Collections.singletonList(result)));
        return representationBuilder.build();
    }

    private static MediaType createMediaType(@NotNull String fileExtension) {
        return new CustomMediaTypeBuilder()._filenameExtension_(fileExtension).build();
    }
}
