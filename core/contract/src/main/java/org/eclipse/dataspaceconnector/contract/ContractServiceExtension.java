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

package org.eclipse.dataspaceconnector.contract;

import org.eclipse.dataspaceconnector.contract.approval.ContractOfferApprovalServiceFactory;
import org.eclipse.dataspaceconnector.contract.negotiation.ContractOfferNegotiationServiceFactory;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferService;
import org.eclipse.dataspaceconnector.spi.contract.approval.*;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractOfferNegotiationService;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.util.Optional;
import java.util.Set;

public class ContractServiceExtension implements ServiceExtension {
    private static final String NAME = "Core Contract Service Extension";

    private static final String[] PROVIDES = {
            ContractOfferService.class.getName(),
            ContractOfferNegotiationService.class.getName(),
            ContractOfferApprovalService.class.getName()
    };

    private Monitor monitor;

    @Override
    public final Set<String> provides() {
        return Set.of(PROVIDES);
    }

    @Override
    public void initialize(final ServiceExtensionContext serviceExtensionContext) {
        monitor = serviceExtensionContext.getMonitor();

        registerServices(serviceExtensionContext);

        monitor.info(String.format("Initialized %s", NAME));
    }

    @Override
    public void start() {
        monitor.info(String.format("Started %s", NAME));
    }

    @Override
    public void shutdown() {
        monitor.info(String.format("Shutdown %s", NAME));
    }

    private void registerServices(final ServiceExtensionContext serviceExtensionContext) {
        // Register contract offer service to the service extension context.
        final ContractOfferService contractOfferService = createContractOfferService(serviceExtensionContext);
        serviceExtensionContext.registerService(ContractOfferService.class, contractOfferService);

        // Register contract approval service to the service extension context.
        final ContractOfferApprovalService contractOfferApprovalService = createContractApprovalService(serviceExtensionContext);
        serviceExtensionContext.registerService(ContractOfferApprovalService.class, contractOfferApprovalService);

        // Register contract negotiation service to the service extension context.
        final ContractOfferNegotiationService contractOfferNegotiationService = createContractNegotiationService(
                serviceExtensionContext, contractOfferApprovalService);
        serviceExtensionContext.registerService(ContractOfferNegotiationService.class, contractOfferNegotiationService);

        // Apply a contract offer approval strategy
        final ContractOfferApprovalStrategyContext contractOfferApprovalStrategyContext = ContractOfferApprovalStrategyContext.Builder.newInstance()
                .contractOfferApprovalService(contractOfferApprovalService)
                .contractOfferNegotiationService(contractOfferNegotiationService)
                .build();
        final ContractOfferApprovalStrategy contractOfferApprovalStrategy = createContractOfferApprovalStrategy(serviceExtensionContext);
        contractOfferApprovalStrategy.apply(contractOfferApprovalStrategyContext);
    }

    private ContractOfferApprovalStrategy createContractOfferApprovalStrategy(
            final ServiceExtensionContext serviceExtensionContext
    ) {
        return Optional
                .ofNullable(serviceExtensionContext.getService(ContractOfferApprovalStrategy.class, true))
                .orElse(DefaultContractOfferApprovalStrategy.INSTANCE);
    }

    private ContractOfferApprovalService createContractApprovalService(final ServiceExtensionContext serviceExtensionContext) {
        return ContractOfferApprovalServiceFactory.createContractOfferApprovalService(serviceExtensionContext);
    }

    private ContractOfferNegotiationService createContractNegotiationService(
            final ServiceExtensionContext serviceExtensionContext,
            final ContractOfferApprovalService contractOfferApprovalService) {
        return ContractOfferNegotiationServiceFactory.createContractOfferNegotiationService(
                serviceExtensionContext, contractOfferApprovalService);
    }

    private ContractOfferService createContractOfferService(final ServiceExtensionContext serviceExtensionContext) {
        return ContractOfferServiceFactory.createContractOfferService(serviceExtensionContext);
    }

    /*
     * The default implementation automatically rejects just created contract offer negotiations
     */
    enum DefaultContractOfferApprovalStrategy implements ContractOfferApprovalStrategy {
        INSTANCE;

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
}
