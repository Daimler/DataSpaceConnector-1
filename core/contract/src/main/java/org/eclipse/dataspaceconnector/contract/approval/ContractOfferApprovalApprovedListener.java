package org.eclipse.dataspaceconnector.contract.approval;

import org.eclipse.dataspaceconnector.spi.contract.approval.ContractOfferApproval;
import org.eclipse.dataspaceconnector.spi.contract.approval.ContractOfferApprovalListener;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractAgreementMessage;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractAgreementMessageFactory;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcher;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

class ContractOfferApprovalApprovedListener implements ContractOfferApprovalListener {
    private final Supplier<ContractAgreementMessageFactory> contractAgreementMessageFactorySupplier;
    private final Supplier<RemoteMessageDispatcher> remoteMessageDispatcherSupplier;

    public ContractOfferApprovalApprovedListener(
            final Supplier<ContractAgreementMessageFactory> contractAgreementMessageFactorySupplier,
            final Supplier<RemoteMessageDispatcher> remoteMessageDispatcherSupplier) {
        this.contractAgreementMessageFactorySupplier = contractAgreementMessageFactorySupplier;
        this.remoteMessageDispatcherSupplier = remoteMessageDispatcherSupplier;
    }

    @Override
    public void approved(final ContractOfferApproval contractOfferApproval) {
        /*
         * Construct a contract agreement message fitting the request protocol
         */
        final ContractAgreementMessage contractAgreementMessage = getContractAgreementMessageFactory()
            .createContractAgreementMessage(contractOfferApproval.getContractOffer());

        /*
         * actually issue the request
         */
        final CompletableFuture<Void> future = getRemoteMessageDispatcher()
            .send(Void.class, contractAgreementMessage, null);

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private ContractAgreementMessageFactory getContractAgreementMessageFactory() {
        return contractAgreementMessageFactorySupplier.get();
    }

    private RemoteMessageDispatcher getRemoteMessageDispatcher() {
        return remoteMessageDispatcherSupplier.get();
    }
}
