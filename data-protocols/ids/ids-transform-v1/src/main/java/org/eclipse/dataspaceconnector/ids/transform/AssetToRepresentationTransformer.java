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
import de.fraunhofer.iais.eis.CustomMediaTypeBuilder;
import de.fraunhofer.iais.eis.MediaType;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class AssetToRepresentationTransformer implements IdsTypeTransformer<Asset, Representation> {
    public static final String KEY_ASSET_FILE_EXTENSION = "ids:fileExtension";

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
        Objects.requireNonNull(context);
        if (object == null) {
            return null;
        }

        Artifact artifact = context.transform(object, Artifact.class);
        URI uri = context.transform(object.getId(), URI.class);

        RepresentationBuilder representationBuilder = new RepresentationBuilder(uri);

        var properties = object.getProperties();
        if (properties == null) {
            context.reportProblem("Asset properties null");
        } else {
            extractProperty(context, properties, KEY_ASSET_FILE_EXTENSION, String.class, (value) -> {
                representationBuilder._mediaType_(createMediaType(value));
            });
        }

        representationBuilder._instance_(new ArrayList<>(Collections.singletonList(artifact)));

        return representationBuilder.build();
    }

    private static MediaType createMediaType(@NotNull String fileExtension) {
        return new CustomMediaTypeBuilder()._filenameExtension_(fileExtension).build();
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
