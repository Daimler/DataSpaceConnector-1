package org.eclipse.dataspaceconnector.ids.core.version;

import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class ConnectorVersionProviderImplTest {

    private ConnectorVersionProvider connectorVersionProvider;

    @BeforeEach
    void setUp() {
        connectorVersionProvider = new ConnectorVersionProviderImpl();
    }

    @Test
    void testNotNull() {
        final Optional<String> versionOptional = connectorVersionProvider.getVersion();

        Assertions.assertNotNull(versionOptional);
    }

}
