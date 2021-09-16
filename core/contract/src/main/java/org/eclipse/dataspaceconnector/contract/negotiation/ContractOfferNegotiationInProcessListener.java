package org.eclipse.dataspaceconnector.contract.negotiation;

import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractNegotiationInProcessMessage;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractNegotiationInProcessMessageFactory;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractOfferNegotiation;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractOfferNegotiationListener;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.ContractRejectionMessageFactory;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcher;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

class ContractOfferNegotiationInProcessListener implements ContractOfferNegotiationListener {
    private final Supplier<ContractNegotiationInProcessMessageFactory> messageFactorySupplier;
    private final Supplier<RemoteMessageDispatcher> remoteMessageDispatcherSupplier;

    public ContractOfferNegotiationInProcessListener(
            final Supplier<ContractNegotiationInProcessMessageFactory> contractRejectionMessageFactorySupplier,
            final Supplier<RemoteMessageDispatcher> remoteMessageDispatcherSupplier) {
        this.messageFactorySupplier = contractRejectionMessageFactorySupplier;
        this.remoteMessageDispatcherSupplier = remoteMessageDispatcherSupplier;
    }

    @Override
    public void initiated(final ContractOfferNegotiation contractOfferNegotiation) {
        /*
         * Construct a contract negotiation in process message to notify the consumer connector
         */
        final ContractNegotiationInProcessMessage contractNegotiationInProcessMessage = getContractNegotiationInProcessMessageFactory()
                .createContractNegotiationInProcessMessage(contractOfferNegotiation.getContractOffer());

        /*
         * issue the request
         */
        final CompletableFuture<Void> future = getRemoteMessageDispatcher()
                .send(Void.class, contractNegotiationInProcessMessage, null);

        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private ContractNegotiationInProcessMessageFactory getContractNegotiationInProcessMessageFactory() {
        return messageFactorySupplier.get();
    }

    private RemoteMessageDispatcher getRemoteMessageDispatcher() {
        return remoteMessageDispatcherSupplier.get();
    }
}