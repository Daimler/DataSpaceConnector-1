package org.eclipse.dataspaceconnector.contract.spi.negotiation;

import org.eclipse.dataspaceconnector.contract.domain.ContractOffer;

public interface ContractRemoteMessageDispatcher {

    void notifyAccepted(ContractOffer offer);

    void notifyDeclined(ContractOffer offer);

    void notifyPending(ContractOffer offer);
}
