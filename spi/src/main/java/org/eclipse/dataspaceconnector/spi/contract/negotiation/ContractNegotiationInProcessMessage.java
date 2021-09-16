package org.eclipse.dataspaceconnector.spi.contract.negotiation;

import org.eclipse.dataspaceconnector.spi.types.domain.message.RemoteMessage;

public class ContractNegotiationInProcessMessage implements RemoteMessage {
    private String protocol;

    @Override
    public String getProtocol() {
        return protocol;
    }
}
