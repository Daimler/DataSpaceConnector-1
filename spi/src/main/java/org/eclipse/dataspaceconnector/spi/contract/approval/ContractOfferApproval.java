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

package org.eclipse.dataspaceconnector.spi.contract.approval;

import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;

import java.util.*;

public class ContractOfferApproval {
    private final State state;
    private final ContractOffer contractOffer;

    public ContractOfferApproval(final State state, final ContractOffer contractOffer) {
        this.state = state;
        this.contractOffer = contractOffer;
    }

    public State getState() {
        return state;
    }

    public boolean isPending() {
        return state == State.PENDING;
    }

    public boolean isApproved() {
        return state == State.APPROVED;
    }

    public boolean isRejected() {
        return state == State.REJECTED;
    }

    public ContractOffer getContractOffer() {
        return contractOffer;
    }

    public static ContractOfferApproval pending(final ContractOffer contractOffer) {
        return new ContractOfferApproval(State.PENDING, contractOffer);
    }

    public ContractOfferApproval approve() {
        return transitionTo(State.APPROVED);
    }

    public ContractOfferApproval reject() {
        return transitionTo(State.REJECTED);
    }

    private ContractOfferApproval transitionTo(final State target) {
        final ContractOfferApproval.State source = this.state;

        final boolean isLegal = source == target || Optional.ofNullable(LEGAL_TRANSITIONS.get(this.state)).orElseGet(Collections::emptyList)
                .contains(target);

        if (!isLegal) {
            throw new IllegalContractOfferApprovalStateTransitionException(source, target);
        }

        return new ContractOfferApproval(target, this.contractOffer);
    }

    private static final Map<State, List<State>> LEGAL_TRANSITIONS = new HashMap<>() {
        {
            put(State.PENDING, Arrays.asList(State.APPROVED, State.REJECTED));
            put(State.APPROVED, Collections.singletonList(State.REJECTED));
            put(State.REJECTED, Collections.emptyList());
        }
    };

    enum State {
        PENDING,
        APPROVED,
        REJECTED;
    }
}
