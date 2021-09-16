package org.eclipse.dataspaceconnector.spi.contract.negotiation;

import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;

public interface ContractNegotiationInProcessMessageFactory {

    ContractNegotiationInProcessMessage createContractNegotiationInProcessMessage(ContractOffer contractOffer);
}
