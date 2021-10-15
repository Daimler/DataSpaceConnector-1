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

package org.eclipse.dataspaceconnector.ids.spi.types.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;

import java.util.Map;
import java.util.function.Consumer;

@JsonDeserialize(builder = IdsAsset.Builder.class)
public class IdsAsset extends Asset {
    private String fileName;
    private Integer byteSize;
    private String fileExtension;

    public String getFileName() {
        return fileName;
    }

    public Integer getByteSize() {
        return byteSize;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder extends Asset.Builder {
        private static final String KEY_ASSET_FILE_NAME = "ids:fileName";
        private static final String KEY_ASSET_FILE_EXTENSION = "ids:fileExtension";
        private static final String KEY_ASSET_BYTE_SIZE = "ids:byteSize";

        private String fileName;
        private Integer byteSize;
        private String fileExtension;

        private Builder() {
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        @JsonIgnore
        public static Builder newInstance(Asset asset) {
            Builder builder = new Builder();

            builder.id(asset.getId());
            builder.name(asset.getName());
            builder.version(asset.getVersion());
            builder.properties(asset.getProperties());

            extract(asset, KEY_ASSET_FILE_NAME, String.class, builder::fileName);
            extract(asset, KEY_ASSET_FILE_EXTENSION, String.class, builder::fileExtension);
            extract(asset, KEY_ASSET_BYTE_SIZE, Integer.class, builder::byteSize);

            return builder;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            this.property(KEY_ASSET_FILE_NAME, fileName);
            return this;
        }

        public Builder byteSize(Integer byteSize) {
            this.byteSize = byteSize;
            this.property(KEY_ASSET_BYTE_SIZE, byteSize);
            return this;
        }

        public Builder fileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
            this.property(KEY_ASSET_FILE_EXTENSION, fileExtension);
            return this;
        }

        public IdsAsset build() {
            IdsAsset asset = new IdsAsset();
            // core properties
            asset.id = id;
            asset.name = title;
            asset.version = version;
            asset.properties = properties;
            // ids asset specific properties
            asset.fileName = fileName;
            asset.byteSize = byteSize;
            asset.fileExtension = fileExtension;
            return asset;
        }

        private static <T> void extract(Asset asset, String key, Class<T> clz, Consumer<T> consumer) {
            if (asset == null || key == null || clz == null || consumer == null) {
                return;
            }

            Map<String, Object> properties = asset.getProperties();
            if (properties == null) {
                return;
            }

            Object source = properties.get(key);
            if (source != null && clz.isAssignableFrom(source.getClass())) {
                consumer.accept(clz.cast(source));
            }
        }
    }
}
