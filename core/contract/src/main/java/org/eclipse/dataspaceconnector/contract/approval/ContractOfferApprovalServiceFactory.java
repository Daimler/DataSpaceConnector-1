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

import org.eclipse.dataspaceconnector.spi.contract.approval.ContractOfferApprovalListener;
import org.eclipse.dataspaceconnector.spi.contract.approval.ContractOfferApprovalService;
import org.eclipse.dataspaceconnector.spi.contract.approval.ContractOfferApprovalStore;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractOfferNegotiationService;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractAgreementMessageFactory;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcher;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.util.Optional;
import java.util.stream.Stream;

public class ContractOfferApprovalServiceFactory {

    public static ContractOfferApprovalService createContractOfferApprovalService(final ServiceExtensionContext serviceExtensionContext) {
        /*
         * Wire or create a default ApprovalStore
         */
        final ContractOfferApprovalStore contractOfferApprovalStore = Optional.ofNullable(serviceExtensionContext.getService(ContractOfferApprovalStore.class, true))
                .orElse(DefaultContractOfferApprovalStore.INSTANCE);

        /*
         * Instantiate the approval process service
         */
        final ContractOfferApprovalServiceImpl approvalProcessService = new ContractOfferApprovalServiceImpl(contractOfferApprovalStore);

        // Add default listeners
        Stream.of(
            createContractOfferApprovalRejectedListener(serviceExtensionContext),
            createContractOfferApprovalApprovedListener(serviceExtensionContext)
        ).forEach(approvalProcessService::registerListener);

        return approvalProcessService;
    }

    private static ContractOfferApprovalListener createContractOfferApprovalRejectedListener(final ServiceExtensionContext serviceExtensionContext) {
        return new ContractOfferApprovalRejectedListener(
            () -> serviceExtensionContext.getService(ContractOfferNegotiationService.class)
        );
    }

    private static ContractOfferApprovalListener createContractOfferApprovalApprovedListener(final ServiceExtensionContext serviceExtensionContext) {
        return new ContractOfferApprovalApprovedListener(
            () -> serviceExtensionContext.getService(ContractAgreementMessageFactory.class),
            () -> serviceExtensionContext.getService(RemoteMessageDispatcher.class)
        );
    }
}
