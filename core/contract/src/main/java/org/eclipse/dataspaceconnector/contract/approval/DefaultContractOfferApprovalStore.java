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

import org.eclipse.dataspaceconnector.spi.contract.approval.ContractOfferApproval;
import org.eclipse.dataspaceconnector.spi.contract.approval.ContractOfferApprovalStore;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

enum DefaultContractOfferApprovalStore implements ContractOfferApprovalStore {
    INSTANCE;

    private final Map<URI, ContractOfferApproval> BACKEND = new ConcurrentHashMap<>();

    @Override
    public void store(final ContractOfferApproval contractOfferApproval) {
        Optional.ofNullable(contractOfferApproval)
                .map(ContractOfferApproval::getContractOffer)
                .map(ContractOffer::getId)
                .ifPresent((k) -> BACKEND.put(k, contractOfferApproval));
    }

    @Override
    public Optional<ContractOfferApproval> findContractOfferApproval(final ContractOffer contractOffer) {
        return Optional.ofNullable(contractOffer)
                .map(ContractOffer::getId)
                .map(BACKEND::get);
    }
}
