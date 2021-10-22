package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import org.jetbrains.annotations.Nullable;

import java.net.URI;

public class DescriptionResponseMessageFactorySettings {
    private final URI id;

    private DescriptionResponseMessageFactorySettings(@Nullable URI id) {
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

        public DescriptionResponseMessageFactorySettings build() {
            return new DescriptionResponseMessageFactorySettings(id);
        }
    }
}
