package org.eclipse.dataspaceconnector.ids.spi.version;

import java.util.Optional;

public interface ConnectorVersionProvider {

    Optional<String> getVersion();
}
