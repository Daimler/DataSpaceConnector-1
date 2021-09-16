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

public class IllegalContractOfferNegotiationStateTransitionException extends ContractOfferNegotiationException {
    private final ContractOfferNegotiation.State source;
    private final ContractOfferNegotiation.State target;

    public IllegalContractOfferNegotiationStateTransitionException(
            final ContractOfferNegotiation.State source,
            final ContractOfferNegotiation.State target) {
        this.source = source;
        this.target = target;
    }

    public ContractOfferNegotiation.State getSource() {
        return source;
    }

    public ContractOfferNegotiation.State getTarget() {
        return target;
    }
}
