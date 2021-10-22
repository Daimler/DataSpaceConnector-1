package org.eclipse.dataspaceconnector.ids.api.multipart.handler.description;

import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

public class ConnectorDescriptionRequestHandlerSettings {
    private final URI id;

    private ConnectorDescriptionRequestHandlerSettings(@Nullable URI id) {
        this.id = Objects.requireNonNull(id);
    }

    @Nullable
    public URI getId() {
        return id;
    }

    public static class Builder {
        private URI id;

        public static Builder newInstance() {
            return new Builder();
        }

        private Builder() {
        }

        public Builder id(URI id) {
            this.id = id;
            return this;
        }

        public ConnectorDescriptionRequestHandlerSettings build() {
            return new ConnectorDescriptionRequestHandlerSettings(id);
        }
    }
}
