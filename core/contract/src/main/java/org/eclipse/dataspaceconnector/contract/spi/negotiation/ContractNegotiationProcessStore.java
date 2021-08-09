package org.eclipse.dataspaceconnector.contract.spi.negotiation;

import org.eclipse.dataspaceconnector.contract.domain.ContractOffer;

public interface ContractNegotiationProcessStore {

    ContractNegotiationProcess save(ContractNegotiationProcess contractNegotiationProcess);

    ContractNegotiationProcess findForOffer(ContractOffer contractOffer);
}
