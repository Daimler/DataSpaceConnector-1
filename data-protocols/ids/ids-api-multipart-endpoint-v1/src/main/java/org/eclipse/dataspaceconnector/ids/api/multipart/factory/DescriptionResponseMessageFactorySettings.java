package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

public class DescriptionResponseMessageFactorySettings {
    private final URI id;

    public DescriptionResponseMessageFactorySettings(@Nullable URI id) {
        this.id = Objects.requireNonNull(id);
    }

    @Nullable
    public URI getId() {
        return id;
    }
}
