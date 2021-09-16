package org.eclipse.dataspaceconnector.contract.approval;

import org.eclipse.dataspaceconnector.spi.contract.approval.ContractOfferApproval;
import org.eclipse.dataspaceconnector.spi.contract.approval.ContractOfferApprovalListener;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractOfferNegotiationService;

import java.util.function.Supplier;

class ContractOfferApprovalRejectedListener implements ContractOfferApprovalListener {
    private final Supplier<ContractOfferNegotiationService> contractOfferNegotiationServiceSupplier;

    public ContractOfferApprovalRejectedListener(final Supplier<ContractOfferNegotiationService> contractOfferNegotiationServiceSupplier) {
        this.contractOfferNegotiationServiceSupplier = contractOfferNegotiationServiceSupplier;
    }

    @Override
    public void rejected(final ContractOfferApproval contractOfferApproval) {
        getContractOfferNegotiationService().terminate(contractOfferApproval.getContractOffer());
    }

    private ContractOfferNegotiationService getContractOfferNegotiationService() {
        return contractOfferNegotiationServiceSupplier.get();
    }
}
