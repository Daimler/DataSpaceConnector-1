package org.eclipse.dataspaceconnector.spi.contract;

import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;

public interface ContractOfferStore {

    void store(ContractOffer contractOffer);
}
