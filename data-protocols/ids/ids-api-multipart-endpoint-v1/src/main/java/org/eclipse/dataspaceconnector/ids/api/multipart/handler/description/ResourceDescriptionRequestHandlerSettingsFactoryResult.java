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

public class ResourceDescriptionRequestHandlerSettingsFactoryResult {
    private final ResourceDescriptionRequestHandlerSettings resourceDescriptionRequestHandlerSettings;
    private final List<String> errors;

    private ResourceDescriptionRequestHandlerSettingsFactoryResult(
            @Nullable ResourceDescriptionRequestHandlerSettings resourceDescriptionRequestHandlerSettings,
            @Nullable List<String> errors) {
        this.resourceDescriptionRequestHandlerSettings = resourceDescriptionRequestHandlerSettings;
        this.errors = errors;
    }

    @Nullable
    public ResourceDescriptionRequestHandlerSettings getResourceDescriptionRequestHandlerSettings() {
        return resourceDescriptionRequestHandlerSettings;
    }

    @NotNull
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors != null ? errors : Collections.emptyList());
    }

    public static final class Builder {
        private ResourceDescriptionRequestHandlerSettings resourceDescriptionRequestHandlerSettings;
        private List<String> errors;

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder resourceDescriptionRequestHandlerSettings(@Nullable ResourceDescriptionRequestHandlerSettings resourceDescriptionRequestHandlerSettings) {
            this.resourceDescriptionRequestHandlerSettings = resourceDescriptionRequestHandlerSettings;
            return this;
        }

        public Builder errors(@Nullable List<String> errors) {
            this.errors = errors;
            return this;
        }

        public ResourceDescriptionRequestHandlerSettingsFactoryResult build() {
            return new ResourceDescriptionRequestHandlerSettingsFactoryResult(resourceDescriptionRequestHandlerSettings, errors);
        }
    }
}
