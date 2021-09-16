package org.eclipse.dataspaceconnector.contract.negotiation;

import org.eclipse.dataspaceconnector.spi.contract.approval.ContractOfferApprovalService;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractOfferNegotiation;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractOfferNegotiationListener;

import java.util.function.Supplier;

class ContractOfferNegotiationInitiationListener implements ContractOfferNegotiationListener {
    private final Supplier<ContractOfferApprovalService> contractOfferApprovalServiceSupplier;

    public ContractOfferNegotiationInitiationListener(final Supplier<ContractOfferApprovalService> contractOfferApprovalServiceSupplier) {
        this.contractOfferApprovalServiceSupplier = contractOfferApprovalServiceSupplier;
    }

    @Override
    public void initiated(final ContractOfferNegotiation contractOfferNegotiation) {
        getContractOfferApprovalService().submit(contractOfferNegotiation.getContractOffer());
    }

    private ContractOfferApprovalService getContractOfferApprovalService() {
        return contractOfferApprovalServiceSupplier.get();
    }
}
