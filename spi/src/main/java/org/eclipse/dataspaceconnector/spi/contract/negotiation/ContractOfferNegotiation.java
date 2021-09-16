/*
 *  Copyright (c) 2021 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial API and Implementation
 *
 */

package org.eclipse.dataspaceconnector.spi.contract.negotiation;

import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;

import java.util.*;

public class ContractOfferNegotiation {
    private final State state;
    private final ContractOffer contractOffer;

    public ContractOfferNegotiation(final State state,
                                    final ContractOffer contractOffer) {
        this.state = state;
        this.contractOffer = contractOffer;
    }

    public State getState() {
        return state;
    }

    public ContractOffer getContractOffer() {
        return contractOffer;
    }

    public static ContractOfferNegotiation initiate(final ContractOffer contractOffer) {
        return new ContractOfferNegotiation(State.PENDING, contractOffer);
    }

    public ContractOfferNegotiation confirm() {
        return this.transitionTo(State.CONFIRMED);
    }

    public ContractOfferNegotiation terminate() {
        return this.transitionTo(State.TERMINATED);
    }

    private ContractOfferNegotiation transitionTo(final State target) {
        final State source = this.state;

        final boolean isLegal = source == target || Optional.ofNullable(LEGAL_TRANSITIONS.get(this.state)).orElseGet(Collections::emptyList)
                .contains(target);

        if (!isLegal) {
            throw new IllegalContractOfferNegotiationStateTransitionException(source, target);
        }

        return new ContractOfferNegotiation(target, this.contractOffer);
    }

    private static final Map<State, List<State>> LEGAL_TRANSITIONS = new HashMap<>() {
        {
            put(State.PENDING, Arrays.asList(State.CONFIRMED, State.TERMINATED));
            put(State.CONFIRMED, Collections.singletonList(State.TERMINATED));
            put(State.TERMINATED, Collections.emptyList());
        }
    };

    enum State {
        PENDING,
        CONFIRMED,
        TERMINATED
    }
}
