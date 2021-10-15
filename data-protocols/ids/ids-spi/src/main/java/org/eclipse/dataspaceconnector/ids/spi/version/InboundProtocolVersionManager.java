package org.eclipse.dataspaceconnector.ids.spi.version;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface InboundProtocolVersionManager {

    void addInboundProtocolVersionProvider(ProtocolVersionProvider protocolVersionProvider);

    @NotNull
    List<IdsProtocolVersion> getInboundProtocolVersions();
}
