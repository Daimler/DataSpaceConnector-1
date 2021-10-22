package org.eclipse.dataspaceconnector.ids.api.multipart.handler.description;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectorDescriptionRequestHandlerSettingsFactoryResult {
    private final ConnectorDescriptionRequestHandlerSettings connectorDescriptionRequestHandlerSettings;
    private final List<String> errors;

    public ConnectorDescriptionRequestHandlerSettingsFactoryResult(
            @Nullable ConnectorDescriptionRequestHandlerSettings connectorDescriptionRequestHandlerSettings,
            @Nullable List<String> errors) {
        this.connectorDescriptionRequestHandlerSettings = connectorDescriptionRequestHandlerSettings;
        this.errors = errors == null ? new ArrayList<>() : errors;
    }

    @Nullable
    public ConnectorDescriptionRequestHandlerSettings getConnectorDescriptionRequestHandlerSettings() {
        return connectorDescriptionRequestHandlerSettings;
    }

    @NotNull
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
