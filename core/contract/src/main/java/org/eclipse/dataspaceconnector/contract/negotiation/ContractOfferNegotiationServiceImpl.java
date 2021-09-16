/*
 *  Copyright (c) 2021 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial API and Implementation
 *
 */

package org.eclipse.dataspaceconnector.contract.negotiation;

import org.eclipse.dataspaceconnector.spi.contract.approval.ContractOfferApprovalService;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.*;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;

class ContractOfferNegotiationServiceImpl extends ContractOfferNegotiationObservable implements ContractOfferNegotiationService {

    private final ContractOfferNegotiationStore contractOfferNegotiationStore;
    private final ContractOfferApprovalService contractApprovalService;

    public ContractOfferNegotiationServiceImpl(final ContractOfferNegotiationStore contractOfferNegotiationStore,
                                               final ContractOfferApprovalService contractApprovalService) {
        this.contractOfferNegotiationStore = contractOfferNegotiationStore;
        this.contractApprovalService = contractApprovalService;
    }

    @Override
    public void initiate(final ContractOffer contractOffer) {
        final ContractOfferNegotiation contractOfferNegotiation = ContractOfferNegotiation.initiate(contractOffer);

        contractOfferNegotiationStore.store(contractOfferNegotiation);

        this.getListeners().forEach(c -> c.initiated(contractOfferNegotiation));
    }

    @Override
    public void terminate(final ContractOffer contractOffer) {
        final ContractOfferNegotiation contractOfferNegotiation = contractOfferNegotiationStore.findContractNegotiation(contractOffer)
                .map(ContractOfferNegotiation::terminate)
                .orElseThrow(ContractOfferNegotiationNotFoundException::new);

        contractOfferNegotiationStore.store(contractOfferNegotiation);

        this.getListeners().forEach(c -> c.terminated(contractOfferNegotiation));
    }

    @Override
    public void confirm(final ContractOffer contractOffer) {
        final ContractOfferNegotiation contractOfferNegotiation = contractOfferNegotiationStore.findContractNegotiation(contractOffer)
                .orElseThrow(ContractOfferNegotiationNotFoundException::new);

        if (contractApprovalService.isApproved(contractOffer)) {
            throw new ContractOfferNegotiationException(); // lack of approval
        }

        contractOfferNegotiationStore.store(contractOfferNegotiation.confirm());

        this.getListeners().forEach(c -> c.confirmed(contractOfferNegotiation));
    }
}
