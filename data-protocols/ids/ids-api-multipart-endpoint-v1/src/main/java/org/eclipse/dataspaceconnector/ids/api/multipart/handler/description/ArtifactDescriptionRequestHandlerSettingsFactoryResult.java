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

public class ArtifactDescriptionRequestHandlerSettingsFactoryResult {
    private final ArtifactDescriptionRequestHandlerSettings artifactDescriptionRequestHandlerSettings;
    private final List<String> errors;

    private ArtifactDescriptionRequestHandlerSettingsFactoryResult(
            @Nullable ArtifactDescriptionRequestHandlerSettings artifactDescriptionRequestHandlerSettings,
            @Nullable List<String> errors) {
        this.artifactDescriptionRequestHandlerSettings = artifactDescriptionRequestHandlerSettings;
        this.errors = errors;
    }

    @Nullable
    public ArtifactDescriptionRequestHandlerSettings getArtifactDescriptionRequestHandlerSettings() {
        return artifactDescriptionRequestHandlerSettings;
    }

    @NotNull
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors != null ? errors : Collections.emptyList());
    }

    public static final class Builder {
        private ArtifactDescriptionRequestHandlerSettings artifactDescriptionRequestHandlerSettings;
        private List<String> errors;

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder artifactDescriptionRequestHandlerSettings(@Nullable ArtifactDescriptionRequestHandlerSettings artifactDescriptionRequestHandlerSettings) {
            this.artifactDescriptionRequestHandlerSettings = artifactDescriptionRequestHandlerSettings;
            return this;
        }

        public Builder errors(@Nullable List<String> errors) {
            this.errors = errors;
            return this;
        }

        public ArtifactDescriptionRequestHandlerSettingsFactoryResult build() {
            return new ArtifactDescriptionRequestHandlerSettingsFactoryResult(artifactDescriptionRequestHandlerSettings, errors);
        }
    }
}
