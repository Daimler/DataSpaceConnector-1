package org.eclipse.dataspaceconnector.ids.core.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ConnectorServiceSettingsFactoryResult {
    private final ConnectorServiceSettings connectorServiceSettings;
    private final List<String> errors;

    private ConnectorServiceSettingsFactoryResult(
            @Nullable ConnectorServiceSettings connectorServiceSettings,
            @Nullable List<String> errors) {
        this.connectorServiceSettings = connectorServiceSettings;
        this.errors = errors;
    }

    @Nullable
    public ConnectorServiceSettings getConnectorServiceSettings() {
        return connectorServiceSettings;
    }

    @NotNull
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors != null ? errors : Collections.emptyList());
    }

    public static final class Builder {
        private ConnectorServiceSettings connectorServiceSettings;
        private List<String> errors;

        public static ConnectorServiceSettingsFactoryResult.Builder newInstance() {
            return new ConnectorServiceSettingsFactoryResult.Builder();
        }

        public ConnectorServiceSettingsFactoryResult.Builder connectorServiceSettings(@Nullable ConnectorServiceSettings connectorServiceSettings) {
            this.connectorServiceSettings = connectorServiceSettings;
            return this;
        }

        public ConnectorServiceSettingsFactoryResult.Builder errors(@Nullable List<String> errors) {
            this.errors = errors;
            return this;
        }

        public ConnectorServiceSettingsFactoryResult build() {
            return new ConnectorServiceSettingsFactoryResult(connectorServiceSettings, errors);
        }
    }
}
