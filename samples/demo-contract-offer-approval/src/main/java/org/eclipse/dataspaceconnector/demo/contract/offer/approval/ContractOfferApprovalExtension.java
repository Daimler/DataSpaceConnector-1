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

import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.contract.approval.ContractOfferApprovalStrategy;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public class ContractOfferApprovalExtension implements ServiceExtension {
    private static final String[] PROVIDES = {
            ContractOfferApprovalStrategy.class.getName()
    };

    @EdcSetting
    private static final String SETTING_APPROVAL_STRATEGY = "edc.contract.offer.approval.strategy";

    @Override
    public final Set<String> provides() {
        return Set.of(PROVIDES);
    }

    @Override
    public void initialize(final ServiceExtensionContext serviceExtensionContext) {
        resolveContractOfferApprovalStrategy(serviceExtensionContext)
            .ifPresent(strategy -> serviceExtensionContext.registerService(ContractOfferApprovalStrategy.class, strategy));
    }

    private Optional<ContractOfferApprovalStrategy> resolveContractOfferApprovalStrategy(final ServiceExtensionContext serviceExtensionContext) {
        return Optional.ofNullable(serviceExtensionContext.getSetting(SETTING_APPROVAL_STRATEGY, null))
                .map(this::convertToEnum)
                .filter(Objects::nonNull)
                .map(this::createContractOfferApprovalStrategy);
    }

    private ContractOfferApprovalStrategy createContractOfferApprovalStrategy(ContractOfferApprovalStrategyEnum anEnum) {
        switch (anEnum) {
            case APPROVE_ALL:
                return new ApproveAllContractOfferApprovalStrategy();
            case REJECT_ALL:
                return new RejectAllContractOfferApprovalStrategy();
            default:
                return null;
        }
    }

    private ContractOfferApprovalStrategyEnum convertToEnum(final String config) {
        return Stream.of(ContractOfferApprovalStrategyEnum.values())
                .filter(e -> e.name().equalsIgnoreCase(config))
                .findFirst()
                .orElse(null);
    }
}
