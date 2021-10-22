package org.eclipse.dataspaceconnector.ids.api.multipart.handler.description;

import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

public class ConnectorDescriptionRequestHandlerSettings {
    private final URI id;

    public ConnectorDescriptionRequestHandlerSettings(@Nullable URI id) {
        this.id = Objects.requireNonNull(id);
    }

    @Nullable
    public URI getId() {
        return id;
    }
}
