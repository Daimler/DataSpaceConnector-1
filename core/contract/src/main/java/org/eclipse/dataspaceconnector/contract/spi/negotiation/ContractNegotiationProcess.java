package org.eclipse.dataspaceconnector.contract.spi.negotiation;

import org.eclipse.dataspaceconnector.contract.domain.ContractOffer;

import java.util.*;

import static org.eclipse.dataspaceconnector.contract.spi.negotiation.ContractNegotiationProcess.State.*;

public class ContractNegotiationProcess {
    private final ContractOffer contractOffer;
    private final State state;
    private final UUID id;

    private ContractNegotiationProcess(
        final ContractOffer contractOffer,
        final State state
    ) {
        this.contractOffer = contractOffer;
        this.state = state;
        this.id = UUID.randomUUID();
    }

    public static ContractNegotiationProcess initialize(final ContractOffer contractOffer) {
        return new ContractNegotiationProcess(contractOffer, Initialized);
    }

    public ContractNegotiationProcess validated() {
        return transitionTo(Validated);
    }

    public ContractNegotiationProcess pending() {
        return transitionTo(Pending);
    }

    public ContractNegotiationProcess agreed(){
        return transitionTo(Agreed);
    }

    public ContractNegotiationProcess concluded() {
        return transitionTo(Concluded);
    }

    public ContractNegotiationProcess rejected() {
        return transitionTo(Rejected);
    }

    public ContractNegotiationProcess transitionTo(final State targetState) {
        Objects.requireNonNull(targetState);

        final State currentState = this.state;

        final boolean transitionAllowed = LEGAL_TRANSITION_STATES.getOrDefault(currentState, Collections.emptyList())
                .contains(targetState) || currentState == targetState;

        if (transitionAllowed) {
            return new ContractNegotiationProcess(
                    contractOffer, targetState
            );
        }

        throw new IllegalContractNegotiationProcessStateTransitionException(
                String.format("Transition from %s to %s disallowed", currentState, targetState),
                this, targetState);
    }

    public ContractOffer getContractOffer() {
        return contractOffer;
    }
    
    public UUID getId(){
        return id;
    }

    public State getState() {
        return state;
    }

    public static class IllegalContractNegotiationProcessStateTransitionException extends RuntimeException {
        private final ContractNegotiationProcess contractNegotiationProcess;
        private final State targetState;

        public IllegalContractNegotiationProcessStateTransitionException(
                final String message,
                final ContractNegotiationProcess contractNegotiationProcess,
                final State targetState) {
            super(message);

            this.contractNegotiationProcess = contractNegotiationProcess;
            this.targetState = targetState;
        }

        public ContractNegotiationProcess getContractNegotiationProcess() {
            return contractNegotiationProcess;
        }

        public State getTargetState() {
            return targetState;
        }
    }

    public enum State {
        Initialized,
        Validated,
        Pending, // Consumer waits for answer, Provider waits for approval
        Agreed,
        Concluded,
        Rejected,
    }

    private final Map<State, List<State>> LEGAL_TRANSITION_STATES =
            new HashMap<>() {
                {
                    put(Initialized, Arrays.asList(Validated, Rejected));
                    put(Validated, Arrays.asList(Pending, Rejected));
                    put(Pending, Arrays.asList(Agreed, Rejected));
                    put(Agreed, Arrays.asList(Concluded, Rejected));
                    put(Concluded, Arrays.asList(Rejected));
                }
            };
}
