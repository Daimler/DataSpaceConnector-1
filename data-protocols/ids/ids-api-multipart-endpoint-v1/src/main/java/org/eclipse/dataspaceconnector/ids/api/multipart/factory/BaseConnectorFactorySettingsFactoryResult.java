package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseConnectorFactorySettingsFactoryResult {
    private final BaseConnectorFactorySettings baseConnectorFactorySettings;
    private final List<String> errors;

    public BaseConnectorFactorySettingsFactoryResult(
            @Nullable BaseConnectorFactorySettings baseConnectorFactorySettings,
            @Nullable List<String> errors) {
        this.baseConnectorFactorySettings = baseConnectorFactorySettings;
        this.errors = errors == null ? new ArrayList<>() : errors;
    }

    @Nullable
    public BaseConnectorFactorySettings getBaseConnectorFactorySettings() {
        return baseConnectorFactorySettings;
    }

    @NotNull
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
