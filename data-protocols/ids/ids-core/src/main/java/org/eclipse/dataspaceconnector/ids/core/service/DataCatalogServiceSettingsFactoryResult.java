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

package org.eclipse.dataspaceconnector.ids.core.service;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DataCatalogServiceSettingsFactoryResult {
    private final DataCatalogServiceSettings settings;
    private final List<String> errors;

    private DataCatalogServiceSettingsFactoryResult(
            @NotNull DataCatalogServiceSettings settings,
            @NotNull List<String> errors) {
        this.settings = Objects.requireNonNull(settings);
        this.errors = Objects.requireNonNull(errors);
    }

    @NotNull
    public DataCatalogServiceSettings getSettings() {
        return settings;
    }

    @NotNull
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public static final class Builder {
        private DataCatalogServiceSettings settings;
        private List<String> errors;

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder settings(DataCatalogServiceSettings settings) {
            this.settings = settings;
            return this;
        }

        public Builder errors(List<String> errors) {
            this.errors = errors;
            return this;
        }

        public DataCatalogServiceSettingsFactoryResult build() {
            return new DataCatalogServiceSettingsFactoryResult(settings, errors);
        }
    }
}
