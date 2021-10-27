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

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.ArtifactBuilder;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;

public class AssetToArtifactTransformer implements IdsTypeTransformer<Asset, Artifact> {
    private static final String KEY_ASSET_FILE_NAME = "ids:fileName";
    private static final String KEY_ASSET_BYTE_SIZE = "ids:byteSize";

    @Override
    public Class<Asset> getInputType() {
        return Asset.class;
    }

    @Override
    public Class<Artifact> getOutputType() {
        return Artifact.class;
    }

    @Override
    public @Nullable Artifact transform(@NotNull Asset object, TransformerContext context) {

        Artifact artifact = IdsId.Builder.newInstance().type(IdsType.ARTIFACT).value(object.get).build();
        ArtifactBuilder artifactBuilder = new ArtifactBuilder(IdsId.artifact(object.getId()).toUri());

        String fileName = (String) object.getProperties().get(KEY_ASSET_FILE_NAME);
        if (fileName != null) {
            artifactBuilder._fileName_(fileName);
        }

        Integer byteSize = (Integer) object.getProperties().get(KEY_ASSET_BYTE_SIZE);
        if (byteSize != null) {
            artifactBuilder._byteSize_(BigInteger.valueOf(byteSize));
        }

        return artifactBuilder.build();
    }
}
