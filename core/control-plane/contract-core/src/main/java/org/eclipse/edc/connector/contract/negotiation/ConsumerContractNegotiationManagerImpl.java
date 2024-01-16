/*
 *  Copyright (c) 2021 - 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *       Fraunhofer Institute for Software and Systems Engineering - extended method implementation
 *       Daimler TSS GmbH - fixed contract dates to epoch seconds
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - refactor
 *       ZF Friedrichshafen AG - fixed contract validity issue
 *
 */

package org.eclipse.edc.connector.contract.negotiation;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.eclipse.edc.connector.contract.spi.negotiation.ConsumerContractNegotiationManager;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreementMessage;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreementVerificationMessage;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractRequest;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractRequestMessage;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.types.domain.agreement.ContractAgreement;
import org.eclipse.edc.statemachine.StateMachineManager;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation.Type.CONSUMER;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.ACCEPTING;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.AGREED;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.INITIAL;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.REQUESTING;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.TERMINATING;
import static org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates.VERIFYING;

/**
 * Implementation of the {@link ConsumerContractNegotiationManager}.
 */
public class ConsumerContractNegotiationManagerImpl extends AbstractContractNegotiationManager implements ConsumerContractNegotiationManager {

    private ConsumerContractNegotiationManagerImpl() {
    }

    @Override
    protected StateMachineManager.Builder configureStateMachineManager(StateMachineManager.Builder builder) {
        return builder
                .processor(processNegotiationsInState(INITIAL, this::processInitial))
                .processor(processNegotiationsInState(REQUESTING, this::processRequesting))
                .processor(processNegotiationsInState(ACCEPTING, this::processAccepting))
                .processor(processNegotiationsInState(AGREED, this::processAgreed))
                .processor(processNegotiationsInState(VERIFYING, this::processVerifying))
                .processor(processNegotiationsInState(TERMINATING, this::processTerminating));
    }

    /**
     * Initiates a new {@link ContractNegotiation}. The ContractNegotiation is created and persisted, which moves it to
     * state REQUESTING.
     *
     * @param request Container object containing all relevant request parameters.
     * @return a {@link StatusResult}: OK
     */
    @WithSpan
    @Override
    public StatusResult<ContractNegotiation> initiate(ContractRequest request) {
        var id = UUID.randomUUID().toString();
        var negotiation = ContractNegotiation.Builder.newInstance()
                .id(id)
                .protocol(request.getProtocol())
                .counterPartyId(request.getProviderId())
                .counterPartyAddress(request.getCounterPartyAddress())
                .callbackAddresses(request.getCallbackAddresses())
                .traceContext(telemetry.getCurrentTraceContext())
                .type(CONSUMER)
                .build();

        negotiation.addContractOffer(request.getContractOffer());
        transitionToInitial(negotiation);

        return StatusResult.success(negotiation);
    }

    @Override
    ContractNegotiation.Type type() {
        return CONSUMER;
    }

    /**
     * Processes {@link ContractNegotiation} in state INITIAL. Transition ContractNegotiation to REQUESTING.
     *
     * @return true if processed, false otherwise
     */
    @WithSpan
    private boolean processInitial(ContractNegotiation negotiation) {
        transitionToRequesting(negotiation);
        return true;
    }

    /**
     * Processes {@link ContractNegotiation} in state REQUESTING. Tries to send the current offer to the respective
     * provider. If this succeeds, the ContractNegotiation is transitioned to state REQUESTED. Else, it is transitioned
     * to REQUESTING for a retry.
     *
     * @return true if processed, false otherwise
     */
    @WithSpan
    private boolean processRequesting(ContractNegotiation negotiation) {
        var offer = negotiation.getLastContractOffer();
        var request = ContractRequestMessage.Builder.newInstance()
                .contractOffer(offer)
                .counterPartyAddress(negotiation.getCounterPartyAddress())
                .callbackAddress(protocolWebhook.url())
                .protocol(negotiation.getProtocol())
                .consumerPid(negotiation.getId())
                .providerPid(negotiation.getCorrelationId())
                .processId(Optional.ofNullable(negotiation.getCorrelationId()).orElse(negotiation.getId()))
                .type(ContractRequestMessage.Type.INITIAL)
                .build();

        return entityRetryProcessFactory.doAsyncStatusResultProcess(negotiation, () -> dispatcherRegistry.dispatch(Object.class, request))
                .entityRetrieve(store::findById)
                .onSuccess((n, result) -> transitionToRequested(n))
                .onFailure((n, throwable) -> transitionToRequesting(n))
                .onFatalError((n, failure) -> transitionToTerminated(n, failure.getFailureDetail()))
                .onRetryExhausted((n, throwable) -> transitionToTerminating(n, format("Failed to send %s to provider: %s", request.getClass().getSimpleName(), throwable.getMessage())))
                .execute("[Consumer] Send ContractRequestMessage message");
    }

    /**
     * Processes {@link ContractNegotiation} in state ACCEPTING. Tries to send a dummy contract agreement to
     * the respective provider in order to approve the last offer sent by the provider. If this succeeds, the
     * ContractNegotiation is transitioned to state ACCEPTED. Else, it is transitioned to ACCEPTING
     * for a retry.
     *
     * @return true if processed, false otherwise
     */
    @WithSpan
    private boolean processAccepting(ContractNegotiation negotiation) {
        var lastOffer = negotiation.getLastContractOffer();

        var policy = lastOffer.getPolicy();
        var agreement = ContractAgreement.Builder.newInstance()
                .contractSigningDate(clock.instant().getEpochSecond())
                .providerId(negotiation.getCounterPartyId())
                .consumerId(participantId)
                .policy(policy)
                .assetId(lastOffer.getAssetId())
                .build();

        var request = ContractAgreementMessage.Builder.newInstance()
                .protocol(negotiation.getProtocol())
                .counterPartyAddress(negotiation.getCounterPartyAddress())
                .contractAgreement(agreement)
                .consumerPid(negotiation.getId())
                .providerPid(negotiation.getCorrelationId())
                .processId(negotiation.getCorrelationId())
                .build();

        return entityRetryProcessFactory.doAsyncStatusResultProcess(negotiation, () -> dispatcherRegistry.dispatch(Object.class, request))
                .entityRetrieve(store::findById)
                .onSuccess((n, result) -> transitionToAccepted(n))
                .onFailure((n, throwable) -> transitionToAccepting(n))
                .onFatalError((n, failure) -> transitionToTerminated(n, failure.getFailureDetail()))
                .onRetryExhausted((n, throwable) -> transitionToTerminating(n, format("Failed to send %s to provider: %s", request.getClass().getSimpleName(), throwable.getMessage())))
                .execute("[consumer] send agreement");
    }

    /**
     * Processes {@link ContractNegotiation} in state AGREED. It transitions to VERIFYING to make the verification process start.
     *
     * @return true if processed, false otherwise
     */
    @WithSpan
    private boolean processAgreed(ContractNegotiation negotiation) {
        transitionToVerifying(negotiation);
        return true;
    }

    /**
     * Processes {@link ContractNegotiation} in state VERIFYING. Verifies the Agreement and send the
     * {@link ContractAgreementVerificationMessage} to the provider and transition the negotiation to the VERIFIED
     * state.
     *
     * @return true if processed, false otherwise
     */
    @WithSpan
    private boolean processVerifying(ContractNegotiation negotiation) {
        var message = ContractAgreementVerificationMessage.Builder.newInstance()
                .protocol(negotiation.getProtocol())
                .counterPartyAddress(negotiation.getCounterPartyAddress())
                .consumerPid(negotiation.getId())
                .providerPid(negotiation.getCorrelationId())
                .policy(negotiation.getContractAgreement().getPolicy())
                .processId(negotiation.getCorrelationId())
                .build();

        return entityRetryProcessFactory.doAsyncStatusResultProcess(negotiation, () -> dispatcherRegistry.dispatch(Object.class, message))
                .entityRetrieve(store::findById)
                .onSuccess((n, result) -> transitionToVerified(n))
                .onFailure((n, throwable) -> transitionToVerifying(n))
                .onFatalError((n, failure) -> transitionToTerminated(n, failure.getFailureDetail()))
                .onRetryExhausted((n, throwable) -> transitionToTerminating(n, format("Failed to send %s to provider: %s", message.getClass().getSimpleName(), throwable.getMessage())))
                .execute(format("[consumer] send %s", message.getClass().getSimpleName()));
    }

    /**
     * Builder for ConsumerContractNegotiationManagerImpl.
     */
    public static class Builder extends AbstractContractNegotiationManager.Builder<ConsumerContractNegotiationManagerImpl> {

        private Builder() {
            super(new ConsumerContractNegotiationManagerImpl());
        }

        public static ConsumerContractNegotiationManagerImpl.Builder newInstance() {
            return new Builder();
        }

    }
}
