package org.eclipse.dataspaceconnector.spi.contract.approval;

import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractOfferNegotiationService;

public class ContractOfferApprovalStrategyContext {
    private ContractOfferApprovalService contractOfferApprovalService;
    private ContractOfferNegotiationService contractOfferNegotiationService;

    private ContractOfferApprovalStrategyContext() {
    }

    public ContractOfferApprovalService getContractOfferApprovalService() {
        return contractOfferApprovalService;
    }

    public ContractOfferNegotiationService getContractOfferNegotiationService() {
        return contractOfferNegotiationService;
    }

    public static class Builder {
        private final ContractOfferApprovalStrategyContext contractOfferApprovalStrategyContext;

        private Builder() {
            contractOfferApprovalStrategyContext = new ContractOfferApprovalStrategyContext();
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder contractOfferApprovalService(final ContractOfferApprovalService contractOfferApprovalService) {
            contractOfferApprovalStrategyContext.contractOfferApprovalService = contractOfferApprovalService;
            return this;
        }

        public Builder contractOfferNegotiationService(final ContractOfferNegotiationService contractOfferNegotiationService) {
            contractOfferApprovalStrategyContext.contractOfferNegotiationService = contractOfferNegotiationService;
            return this;
        }

        public ContractOfferApprovalStrategyContext build() {
            return contractOfferApprovalStrategyContext;
        }
    }
}
