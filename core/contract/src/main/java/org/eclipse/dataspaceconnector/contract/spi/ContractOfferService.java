package org.eclipse.dataspaceconnector.contract.spi;

import org.eclipse.dataspaceconnector.contract.domain.ContractOffer;

public interface ContractOfferService {

    /**
     * Verifies whether the passed contract offer meets all requirements.
     *
     * @param contractOffer
     * @return
     */
    boolean isProperOffer(ContractOffer contractOffer);
}
