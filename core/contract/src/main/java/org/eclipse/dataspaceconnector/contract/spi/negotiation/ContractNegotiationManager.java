package org.eclipse.dataspaceconnector.contract.spi.negotiation;

import org.eclipse.dataspaceconnector.contract.domain.ContractOffer;

public interface ContractNegotiationManager {

    void validate(ContractOffer contractOffer);

    void approve(ContractOffer contractOffer);

    void reject(ContractOffer contractOffer);

    void conclude(ContractOffer contractOffer);

}