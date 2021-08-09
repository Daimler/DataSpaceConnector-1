package org.eclipse.dataspaceconnector.contract.negotiation;

import org.eclipse.dataspaceconnector.contract.domain.ContractOffer;
import org.eclipse.dataspaceconnector.contract.eventing.EventDispatcher;
import org.eclipse.dataspaceconnector.contract.spi.negotiation.ApprovalService;
import org.eclipse.dataspaceconnector.contract.spi.negotiation.ContractNegotiationManager;

public class ApprovalServiceImpl implements ApprovalService {

    private final EventDispatcher eventDispatcher;
    private final ContractNegotiationManager contractNegotiationManager;

    public ApprovalServiceImpl(
            final EventDispatcher eventDispatcher,
            final ContractNegotiationManager contractNegotiationManager
    ) {
        this.eventDispatcher = eventDispatcher;
        this.eventDispatcher.registerListener(ContractOfferNegotiationUpdateEvent.class, this::onContractOfferNegotiationUpdateEvent);
        this.contractNegotiationManager = contractNegotiationManager;
    }

    private void onContractOfferNegotiationUpdateEvent(final ContractOfferNegotiationUpdateEvent contractOfferNegotiationUpdateEvent) {

    }
}