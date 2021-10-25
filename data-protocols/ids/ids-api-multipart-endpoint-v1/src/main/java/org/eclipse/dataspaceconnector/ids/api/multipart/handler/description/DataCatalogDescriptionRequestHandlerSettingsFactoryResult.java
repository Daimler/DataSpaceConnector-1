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

package org.eclipse.dataspaceconnector.ids.api.multipart.handler.description;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class DataCatalogDescriptionRequestHandlerSettingsFactoryResult {
    private final DataCatalogDescriptionRequestHandlerSettings dataCatalogDescriptionRequestHandlerSettings;
    private final List<String> errors;

    private DataCatalogDescriptionRequestHandlerSettingsFactoryResult(
            @Nullable DataCatalogDescriptionRequestHandlerSettings dataCatalogDescriptionRequestHandlerSettings,
            @Nullable List<String> errors) {
        this.dataCatalogDescriptionRequestHandlerSettings = dataCatalogDescriptionRequestHandlerSettings;
        this.errors = errors;
    }

    @Nullable
    public DataCatalogDescriptionRequestHandlerSettings getDataCatalogDescriptionRequestHandlerSettings() {
        return dataCatalogDescriptionRequestHandlerSettings;
    }

    @NotNull
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors != null ? errors : Collections.emptyList());
    }

    public static final class Builder {
        private DataCatalogDescriptionRequestHandlerSettings dataCatalogDescriptionRequestHandlerSettings;
        private List<String> errors;

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder dataCatalogDescriptionRequestHandlerSettings(@Nullable DataCatalogDescriptionRequestHandlerSettings dataCatalogDescriptionRequestHandlerSettings) {
            this.dataCatalogDescriptionRequestHandlerSettings = dataCatalogDescriptionRequestHandlerSettings;
            return this;
        }

        public Builder errors(@Nullable List<String> errors) {
            this.errors = errors;
            return this;
        }

        public DataCatalogDescriptionRequestHandlerSettingsFactoryResult build() {
            return new DataCatalogDescriptionRequestHandlerSettingsFactoryResult(dataCatalogDescriptionRequestHandlerSettings, errors);
        }
    }
}
