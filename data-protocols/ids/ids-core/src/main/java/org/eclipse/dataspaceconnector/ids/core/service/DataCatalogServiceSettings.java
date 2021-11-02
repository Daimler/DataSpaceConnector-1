package org.eclipse.dataspaceconnector.ids.core.service;

import org.jetbrains.annotations.Nullable;

public class DataCatalogServiceSettings {
    private final String catalogId;

    private DataCatalogServiceSettings(@Nullable String catalogId) {
        this.catalogId = catalogId;
    }

    @Nullable
    public String getCatalogId() {
        return catalogId;
    }

    public static class Builder {
        private String catalogId;

        public static Builder newInstance() {
            return new Builder();
        }

        private Builder() {
        }

        public Builder catalogId(@Nullable String catalogId) {
            this.catalogId = catalogId;
            return this;
        }

        public DataCatalogServiceSettings build() {
            return new DataCatalogServiceSettings(catalogId);
        }
    }
}
