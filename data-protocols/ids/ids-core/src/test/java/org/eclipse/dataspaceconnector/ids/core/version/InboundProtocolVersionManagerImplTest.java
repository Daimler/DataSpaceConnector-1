package org.eclipse.dataspaceconnector.ids.core.version;

import org.assertj.core.api.Assertions;
import org.easymock.EasyMock;
import org.eclipse.dataspaceconnector.ids.spi.version.IdsProtocolVersion;
import org.eclipse.dataspaceconnector.ids.spi.version.InboundProtocolVersionManager;
import org.eclipse.dataspaceconnector.ids.spi.version.ProtocolVersionProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class InboundProtocolVersionManagerImplTest {

    private InboundProtocolVersionManager inboundProtocolVersionManager;

    @BeforeEach
    void setUp() {
        inboundProtocolVersionManager = new InboundProtocolVersionManagerImpl();
    }

    @Test
    void testAddInboundProtocolVersionProviderAcceptsNull() {
        inboundProtocolVersionManager.addInboundProtocolVersionProvider(null);
    }

    @Test
    void testGetInboundProtocolVersions() {
        // prepare
        final IdsProtocolVersion idsProtocolVersion = new IdsProtocolVersion("dummy");
        final ProtocolVersionProvider protocolVersionProvider = EasyMock.mock(ProtocolVersionProvider.class);
        EasyMock.expect(protocolVersionProvider.getIdsProtocolVersion()).andReturn(idsProtocolVersion).times(1);

        EasyMock.replay(protocolVersionProvider);

        inboundProtocolVersionManager.addInboundProtocolVersionProvider(protocolVersionProvider);

        // invoke
        final List<IdsProtocolVersion> result = inboundProtocolVersionManager.getInboundProtocolVersions();

        // verify
        Assertions.assertThat(result).hasSize(1).containsExactlyElementsOf(Collections.singletonList(idsProtocolVersion));
    }
}
