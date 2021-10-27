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
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class AssetToArtifactTransformer implements IdsTypeTransformer<Asset, Artifact> {
    public static final String KEY_ASSET_FILE_NAME = "ids:fileName";
    public static final String KEY_ASSET_BYTE_SIZE = "ids:byteSize";

    @Override
    public Class<Asset> getInputType() {
        return Asset.class;
    }

    @Override
    public Class<Artifact> getOutputType() {
        return Artifact.class;
    }

    @Override
    public @Nullable Artifact transform(Asset object, TransformerContext context) {
        Objects.requireNonNull(context);
        if (object == null) {
            return null;
        }

        URI uri = context.transform(object.getId(), URI.class);

        ArtifactBuilder artifactBuilder = new ArtifactBuilder(uri);

        var properties = object.getProperties();
        if (properties == null) {
            context.reportProblem("Asset properties null");
            return artifactBuilder.build();
        }

        extractProperty(context, properties, KEY_ASSET_FILE_NAME, String.class, artifactBuilder::_fileName_);
        extractProperty(context, properties, KEY_ASSET_BYTE_SIZE, BigInteger.class, artifactBuilder::_byteSize_);

        return artifactBuilder.build();
    }

    private <T> void extractProperty(TransformerContext context, Map<String, Object> properties, String propertyKey, Class<T> targetType, Consumer<T> consumer) {
        var propertyValue = properties.get(propertyKey);
        if (propertyValue == null) {
            context.reportProblem(String.format("Asset property %s is null", propertyKey));
        } else {
            if (targetType.isAssignableFrom(propertyValue.getClass())) {
                consumer.accept(targetType.cast(propertyValue));
            } else {
                T convertedPropertyValue;
                if ((convertedPropertyValue = context.transform(propertyValue, targetType)) != null) {
                    consumer.accept(convertedPropertyValue);
                } else {
                    context.reportProblem(String.format("Asset property %s not convertible to %s", propertyKey, targetType.getSimpleName()));
                }
            }
        }
    }
}
