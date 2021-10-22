package org.eclipse.dataspaceconnector.ids.api.multipart.handler;

import org.jetbrains.annotations.Nullable;

import java.net.URI;

public class DescriptionHandlerSettings {
    private final URI id;

    private DescriptionHandlerSettings(@Nullable URI id) {
        this.id = id;
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

        public DescriptionHandlerSettings build() {
            return new DescriptionHandlerSettings(id);
        }
    }
}
