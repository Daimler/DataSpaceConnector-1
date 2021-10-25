package org.eclipse.dataspaceconnector.ids.core.version;

import org.assertj.core.api.Assertions;
import org.easymock.EasyMock;
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
        IdsProtocolVersion idsProtocolVersion = new IdsProtocolVersion("dummy");
        ProtocolVersionProvider protocolVersionProvider = EasyMock.mock(ProtocolVersionProvider.class);
        EasyMock.expect(protocolVersionProvider.getIdsProtocolVersion()).andReturn(idsProtocolVersion).times(1);

        EasyMock.replay(protocolVersionProvider);

        inboundProtocolVersionManager.addInboundProtocolVersionProvider(protocolVersionProvider);

        // invoke
        List<IdsProtocolVersion> result = inboundProtocolVersionManager.getInboundProtocolVersions();

        // verify
        Assertions.assertThat(result).hasSize(1).containsExactlyElementsOf(Collections.singletonList(idsProtocolVersion));
    }
}
