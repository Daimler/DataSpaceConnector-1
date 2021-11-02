package org.eclipse.dataspaceconnector.ids.core.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class DataCatalogServiceSettingsFactoryResult {
    private final DataCatalogServiceSettings dataCatalogServiceSettings;
    private final List<String> errors;

    private DataCatalogServiceSettingsFactoryResult(
            @Nullable DataCatalogServiceSettings dataCatalogServiceSettings,
            @Nullable List<String> errors) {
        this.dataCatalogServiceSettings = dataCatalogServiceSettings;
        this.errors = errors;
    }

    @Nullable
    public DataCatalogServiceSettings getDataCatalogServiceSettings() {
        return dataCatalogServiceSettings;
    }

    @NotNull
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors != null ? errors : Collections.emptyList());
    }

    public static final class Builder {
        private DataCatalogServiceSettings dataCatalogServiceSettings;
        private List<String> errors;

        public static DataCatalogServiceSettingsFactoryResult.Builder newInstance() {
            return new DataCatalogServiceSettingsFactoryResult.Builder();
        }

        public DataCatalogServiceSettingsFactoryResult.Builder dataCatalogServiceSettings(@Nullable DataCatalogServiceSettings dataCatalogServiceSettings) {
            this.dataCatalogServiceSettings = dataCatalogServiceSettings;
            return this;
        }

        public DataCatalogServiceSettingsFactoryResult.Builder errors(@Nullable List<String> errors) {
            this.errors = errors;
            return this;
        }

        public DataCatalogServiceSettingsFactoryResult build() {
            return new DataCatalogServiceSettingsFactoryResult(dataCatalogServiceSettings, errors);
        }
    }
}
