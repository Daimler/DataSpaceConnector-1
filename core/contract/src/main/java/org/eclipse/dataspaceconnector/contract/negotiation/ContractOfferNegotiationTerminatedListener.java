package org.eclipse.dataspaceconnector.contract.negotiation;

import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractOfferNegotiation;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractOfferNegotiationListener;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractRejectionMessage;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractRejectionMessageFactory;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcher;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

class ContractOfferNegotiationTerminatedListener implements ContractOfferNegotiationListener {
    private final Supplier<ContractRejectionMessageFactory> contractRejectionMessageFactorySupplier;
    private final Supplier<RemoteMessageDispatcher> remoteMessageDispatcherSupplier;

    public ContractOfferNegotiationTerminatedListener(
            final Supplier<ContractRejectionMessageFactory> contractRejectionMessageFactorySupplier,
            final Supplier<RemoteMessageDispatcher> remoteMessageDispatcherSupplier) {
        this.contractRejectionMessageFactorySupplier = contractRejectionMessageFactorySupplier;
        this.remoteMessageDispatcherSupplier = remoteMessageDispatcherSupplier;
    }

    @Override
    public void terminated(final ContractOfferNegotiation contractOfferNegotiation) {
        /*
         * Construct a contract rejection /termination message fitting the request protocol
         */
        final ContractRejectionMessage contractRejectionMessage = getContractRejectionMessageFactory()
                .createContractRejectionMessage(contractOfferNegotiation.getContractOffer());

        /*
         * actually issue the request
         */
        final CompletableFuture<Void> future = getRemoteMessageDispatcher()
                .send(Void.class, contractRejectionMessage, null);

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private ContractRejectionMessageFactory getContractRejectionMessageFactory() {
        return contractRejectionMessageFactorySupplier.get();
    }

    private RemoteMessageDispatcher getRemoteMessageDispatcher() {
        return remoteMessageDispatcherSupplier.get();
    }
}
