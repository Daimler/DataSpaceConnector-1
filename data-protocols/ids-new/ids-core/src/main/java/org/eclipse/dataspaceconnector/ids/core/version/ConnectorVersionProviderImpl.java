package org.eclipse.dataspaceconnector.ids.core.version;

import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;

import java.util.Optional;

public class ConnectorVersionProviderImpl implements ConnectorVersionProvider {

    @Override
    public Optional<String> getVersion() {
        return Optional.ofNullable(ServiceExtension.class.getPackage().getImplementationVersion());
    }
}
