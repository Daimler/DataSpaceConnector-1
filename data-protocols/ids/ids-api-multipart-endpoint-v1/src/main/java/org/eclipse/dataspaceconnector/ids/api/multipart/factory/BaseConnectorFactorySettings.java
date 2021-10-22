package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import org.eclipse.dataspaceconnector.ids.spi.types.SecurityProfile;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;

public class BaseConnectorFactorySettings {
    private final URI id;
    private final String title;
    private final String description;
    private final URI maintainer;
    private final URI curator;
    private final URI connectorEndpoint;
    private final SecurityProfile securityProfile;

    public BaseConnectorFactorySettings(@Nullable URI id,
                                        @Nullable String title,
                                        @Nullable String description,
                                        @Nullable URI maintainer,
                                        @Nullable URI curator,
                                        @Nullable URI connectorEndpoint,
                                        @Nullable SecurityProfile securityProfile) {
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        this.description = Objects.requireNonNull(description);
        this.maintainer = Objects.requireNonNull(maintainer);
        this.curator = Objects.requireNonNull(curator);
        this.connectorEndpoint = Objects.requireNonNull(connectorEndpoint);
        this.securityProfile = Objects.requireNonNull(securityProfile);
    }

    @Nullable
    public URI getId() {
        return id;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public URI getMaintainer() {
        return maintainer;
    }

    @Nullable
    public URI getCurator() {
        return curator;
    }

    @Nullable
    public URI getConnectorEndpoint() {
        return connectorEndpoint;
    }

    @Nullable
    public SecurityProfile getSecurityProfile() {
        return securityProfile;
    }

    // TODO Add Builder to all settings
}
