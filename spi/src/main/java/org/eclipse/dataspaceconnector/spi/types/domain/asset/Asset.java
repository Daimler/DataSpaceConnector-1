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

package org.eclipse.dataspaceconnector.spi.types.domain.asset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link Asset} contains the metadata and describes the data itself or a collection of data.
 */
@JsonDeserialize(builder = Asset.Builder.class)
public class Asset {
    protected String id;
    protected String name;
    protected String version;
    protected Map<String, Object> properties;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        protected String id;
        protected String title;
        protected String version;
        protected Map<String, Object> properties = new HashMap<>();

        protected Builder() {
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String title) {
            this.title = title;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public Builder property(String key, Object value) {
            this.properties.put(key, value);
            return this;
        }

        public Asset build() {
            Asset asset = new Asset();
            asset.id = id;
            asset.name = title;
            asset.version = version;
            asset.properties = properties;
            return asset;
        }
    }
}