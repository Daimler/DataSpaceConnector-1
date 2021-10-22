package org.eclipse.dataspaceconnector.ids.api.multipart.handler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DescriptionHandlerSettingsFactoryResult {
    private final DescriptionHandlerSettings descriptionHandlerSettings;
    private final List<String> errors;

    public DescriptionHandlerSettingsFactoryResult(
            @Nullable DescriptionHandlerSettings descriptionHandlerSettings,
            @Nullable List<String> errors) {
        this.descriptionHandlerSettings = descriptionHandlerSettings;
        this.errors = errors == null ? new ArrayList<>() : errors;
    }

    @Nullable
    public DescriptionHandlerSettings getDescriptionHandlerSettings() {
        return descriptionHandlerSettings;
    }

    @NotNull
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
