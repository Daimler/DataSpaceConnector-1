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

package org.eclipse.dataspaceconnector.demo.contract.offer.approval;

import org.eclipse.dataspaceconnector.spi.contract.approval.*;

class RejectAllContractOfferApprovalStrategy implements ContractOfferApprovalStrategy {
    @Override
    public void apply(final ContractOfferApprovalStrategyContext contractOfferApprovalStrategyContext) {
        final ContractOfferApprovalService contractOfferApprovalService = contractOfferApprovalStrategyContext.getContractOfferApprovalService();

        contractOfferApprovalService.registerListener(new ContractOfferApprovalListener() {
            @Override
            public void pending(final ContractOfferApproval contractOfferApproval) {
                contractOfferApprovalService.reject(contractOfferApproval.getContractOffer());
            }
        });
    }
}
