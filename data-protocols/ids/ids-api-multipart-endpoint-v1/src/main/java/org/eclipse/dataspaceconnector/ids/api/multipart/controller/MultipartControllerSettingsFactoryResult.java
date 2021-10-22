package org.eclipse.dataspaceconnector.ids.api.multipart.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultipartControllerSettingsFactoryResult {
    private final MultipartControllerSettings multipartControllerSettings;
    private final List<String> errors;

    public MultipartControllerSettingsFactoryResult(
            @Nullable MultipartControllerSettings multipartControllerSettings,
            @Nullable List<String> errors) {
        this.multipartControllerSettings = multipartControllerSettings;
        this.errors = errors == null ? new ArrayList<>() : errors;
    }

    @Nullable
    public MultipartControllerSettings getRejectionMessageFactorySettings() {
        return multipartControllerSettings;
    }

    @NotNull
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
