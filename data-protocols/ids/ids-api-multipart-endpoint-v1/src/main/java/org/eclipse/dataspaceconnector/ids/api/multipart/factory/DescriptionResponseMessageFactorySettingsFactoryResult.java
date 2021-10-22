package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DescriptionResponseMessageFactorySettingsFactoryResult {
    private final DescriptionResponseMessageFactorySettings descriptionResponseMessageFactorySettings;
    private final List<String> errors;

    public DescriptionResponseMessageFactorySettingsFactoryResult(
            @Nullable DescriptionResponseMessageFactorySettings descriptionResponseMessageFactorySettings,
            @Nullable List<String> errors) {
        this.descriptionResponseMessageFactorySettings = descriptionResponseMessageFactorySettings;
        this.errors = errors == null ? new ArrayList<>() : errors;
    }

    @Nullable
    public DescriptionResponseMessageFactorySettings getDescriptionResponseMessageFactorySettings() {
        return descriptionResponseMessageFactorySettings;
    }

    @NotNull
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
