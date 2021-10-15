package org.eclipse.dataspaceconnector.ids.core.version;

import org.eclipse.dataspaceconnector.ids.spi.version.IdsProtocolVersion;
import org.eclipse.dataspaceconnector.ids.spi.version.InboundProtocolVersionManager;
import org.eclipse.dataspaceconnector.ids.spi.version.ProtocolVersionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class InboundProtocolVersionManagerImpl implements InboundProtocolVersionManager {
    private final List<ProtocolVersionProvider> inboundProviders = new LinkedList<>();

    @Override
    public void addInboundProtocolVersionProvider(final ProtocolVersionProvider protocolVersionProvider) {
        Optional.ofNullable(protocolVersionProvider)
                .ifPresent(inboundProviders::add);
    }

    @NotNull
    @Override
    public List<IdsProtocolVersion> getInboundProtocolVersions() {
        return inboundProviders.stream()
                .map(ProtocolVersionProvider::getIdsProtocolVersion)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
