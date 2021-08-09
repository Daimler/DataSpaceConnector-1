package org.eclipse.dataspaceconnector.contract.negotiation;

import org.eclipse.dataspaceconnector.contract.eventing.Event;
import org.eclipse.dataspaceconnector.contract.spi.negotiation.ContractNegotiationProcess;

import java.security.cert.CertPathValidatorException;
import java.util.Objects;
import java.util.UUID;

public class ContractOfferNegotiationUpdateEvent implements Event {
    private final UUID contractOfferNegotiationId;

    private ContractOfferNegotiationUpdateEvent(UUID contractOfferNegotiationId) {
        this.contractOfferNegotiationId = contractOfferNegotiationId;
    }

    public static ContractOfferNegotiationUpdateEvent create(final ContractNegotiationProcess contractNegotiationProcess) {
        Objects.requireNonNull(contractNegotiationProcess);

        final ContractOfferNegotiationUpdateEvent event = new ContractOfferNegotiationUpdateEvent(
            contractNegotiationProcess.getId()
        );

        return event;
    }

    private UUID getContractOfferNegotiationId() {
        return contractOfferNegotiationId;
    }
}
