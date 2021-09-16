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
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractNegotiationInProcessMessageFactory;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractOfferNegotiationService;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractOfferNegotiationStore;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractRejectionMessageFactory;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcher;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.util.Optional;
import java.util.stream.Stream;

public class ContractOfferNegotiationServiceFactory {

    public static ContractOfferNegotiationService createContractOfferNegotiationService(
            final ServiceExtensionContext serviceExtensionContext,
            final ContractOfferApprovalService contractOfferApprovalService) {

        final ContractOfferNegotiationStore contractOfferNegotiationStore = Optional
                .ofNullable(serviceExtensionContext.getService(ContractOfferNegotiationStore.class, true))
                .orElse(DefaultContractOfferNegotiationStore.INSTANCE);

        final ContractOfferNegotiationService contractOfferNegotiationService = new ContractOfferNegotiationServiceImpl(
                contractOfferNegotiationStore, contractOfferApprovalService);

        Stream.of(
            createContractOfferNegotiationInProcessListener(serviceExtensionContext),
            createContractOfferNegotiationInitiationListener(contractOfferApprovalService),
            createContractOfferNegotiationTerminatedListener(serviceExtensionContext)
        ).forEach(contractOfferNegotiationService::registerListener);

        return contractOfferNegotiationService;
    }

    private static ContractOfferNegotiationInProcessListener createContractOfferNegotiationInProcessListener(
        final ServiceExtensionContext serviceExtensionContext
    ) {
        return new ContractOfferNegotiationInProcessListener(
                () -> serviceExtensionContext.getService(ContractNegotiationInProcessMessageFactory.class),
                () -> serviceExtensionContext.getService(RemoteMessageDispatcher.class)
        );
    }
    private static ContractOfferNegotiationInitiationListener createContractOfferNegotiationInitiationListener(
            final ContractOfferApprovalService contractApprovalService
    ) {
        return new ContractOfferNegotiationInitiationListener(
                () -> contractApprovalService
        );
    }

    private static ContractOfferNegotiationTerminatedListener createContractOfferNegotiationTerminatedListener(
            final ServiceExtensionContext serviceExtensionContext) {
        return new ContractOfferNegotiationTerminatedListener(
                () -> serviceExtensionContext.getService(ContractRejectionMessageFactory.class),
                () -> serviceExtensionContext.getService(RemoteMessageDispatcher.class)
        );
    }
}
