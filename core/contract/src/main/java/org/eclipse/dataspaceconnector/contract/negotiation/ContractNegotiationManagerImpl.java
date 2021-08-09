package org.eclipse.dataspaceconnector.contract.negotiation;

import org.eclipse.dataspaceconnector.contract.domain.ContractOffer;
import org.eclipse.dataspaceconnector.contract.eventing.EventDispatcher;
import org.eclipse.dataspaceconnector.contract.spi.*;
import org.eclipse.dataspaceconnector.contract.spi.negotiation.ApprovalService;
import org.eclipse.dataspaceconnector.contract.spi.negotiation.ContractNegotiationManager;
import org.eclipse.dataspaceconnector.contract.spi.negotiation.ContractNegotiationProcess;
import org.eclipse.dataspaceconnector.contract.spi.negotiation.ContractNegotiationProcessStore;
import org.eclipse.dataspaceconnector.contract.spi.negotiation.ContractRemoteMessageDispatcher;

public class ContractNegotiationManagerImpl implements ContractNegotiationManager {

    private final ContractRemoteMessageDispatcher contractRemoteMessageDispatcher;
    private final ContractNegotiationProcessStore negotiationStore;
    private final ContractStore contractStore;
    private final ApprovalService approvalService;
    private final ContractOfferService contractOfferServices;
    private final EventDispatcher eventDispatcher;

    public ContractNegotiationManagerImpl(
            final ContractRemoteMessageDispatcher contractRemoteMessageDispatcher,
            ContractNegotiationProcessStore negotiationStore,
            final ContractStore contractStore,
            final ApprovalService approvalService,
            final ContractOfferService contractOfferService,
            final EventDispatcher eventDispatcher) {
        this.negotiationStore = negotiationStore;
        this.contractStore = contractStore;
        this.approvalService = approvalService;
        this.contractRemoteMessageDispatcher = contractRemoteMessageDispatcher;
        this.contractOfferServices = contractOfferService;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void validate(final ContractOffer contractOffer) {

        var process = ContractNegotiationProcess.initialize(contractOffer);

        update(process);

        var isProper = contractOfferServices.isProperOffer(contractOffer);

        if (!isProper) {
            reject(contractOffer);

            return;
        }

        update(process.validated());
    }

    @Override
    public void approve(final ContractOffer contractOffer) {
    }

    @Override
    public void reject(final ContractOffer contractOffer) {
        contractRemoteMessageDispatcher.notifyDeclined(contractOffer);

        final ContractNegotiationProcess contractNegotiationProcess = negotiationStore.findForOffer(contractOffer);

        update(contractNegotiationProcess.rejected());
    }

    @Override
    public void conclude(final ContractOffer contractOffer) {

    }

    private void update(final ContractNegotiationProcess contractNegotiationProcess) {
        negotiationStore.save(contractNegotiationProcess);

        final ContractOfferNegotiationUpdateEvent event = ContractOfferNegotiationUpdateEvent
                .create(contractNegotiationProcess);

        eventDispatcher.emit(event);
    }
}
