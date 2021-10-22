package org.eclipse.dataspaceconnector.ids.api.multipart.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RejectionMessageFactorySettingsFactoryResult {
    private final RejectionMessageFactorySettings rejectionMessageFactorySettings;
    private final List<String> errors;

    public RejectionMessageFactorySettingsFactoryResult(
            @Nullable RejectionMessageFactorySettings rejectionMessageFactorySettings,
            @Nullable List<String> errors) {
        this.rejectionMessageFactorySettings = rejectionMessageFactorySettings;
        this.errors = errors == null ? new ArrayList<>() : errors;
    }

    @Nullable
    public RejectionMessageFactorySettings getRejectionMessageFactorySettings() {
        return rejectionMessageFactorySettings;
    }

    @NotNull
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
