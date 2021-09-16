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

package org.eclipse.dataspaceconnector.contract.approval;

import org.eclipse.dataspaceconnector.spi.contract.approval.*;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;

class ContractOfferApprovalServiceImpl extends ContractOfferApprovalObservable implements ContractOfferApprovalService {

    private final ContractOfferApprovalStore contractOfferApprovalStore;

    public ContractOfferApprovalServiceImpl(final ContractOfferApprovalStore contractOfferApprovalStore) {
        this.contractOfferApprovalStore = contractOfferApprovalStore;
    }

    @Override
    public void submit(final ContractOffer contractOffer) {
        final ContractOfferApproval contractOfferApproval = ContractOfferApproval.pending(contractOffer);

        contractOfferApprovalStore.store(contractOfferApproval);

        this.getListeners().forEach(e -> e.pending(contractOfferApproval));
    }

    @Override
    public void approve(final ContractOffer contractOffer) {
        final ContractOfferApproval contractOfferApproval = contractOfferApprovalStore.findContractOfferApproval(contractOffer)
                .map(ContractOfferApproval::approve)
                .orElseThrow(ContractOfferApprovalNotFoundException::new);

        contractOfferApprovalStore.store(contractOfferApproval);

        this.getListeners().forEach(e -> e.approved(contractOfferApproval));
    }

    @Override
    public void reject(final ContractOffer contractOffer)  {
        final ContractOfferApproval contractOfferApproval = contractOfferApprovalStore.findContractOfferApproval(contractOffer)
                .map(ContractOfferApproval::approve)
                .orElseThrow(ContractOfferApprovalNotFoundException::new);

        this.getListeners().forEach(e -> e.rejected(contractOfferApproval));
    }

    @Override
    public boolean isApproved(final ContractOffer contractOffer) {
        return contractOfferApprovalStore.findContractOfferApproval(contractOffer)
                .map(ContractOfferApproval::isApproved)
                .orElse(false);
    }
}
