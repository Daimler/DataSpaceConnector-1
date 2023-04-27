/*
 *  Copyright (c) 2022 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *       Fraunhofer Institute for Software and Systems Engineering - expending Event classes
 *
 */

package org.eclipse.edc.connector.contract.spi.event.contractnegotiation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * This event is raised when the ContractNegotiation has been declined.
 *
 * @deprecated please use {@link ContractNegotiationTerminated}
 */
@Deprecated(since = "milestone9")
@JsonDeserialize(builder = ContractNegotiationDeclined.Builder.class)
public class ContractNegotiationDeclined extends ContractNegotiationEvent {

    private ContractNegotiationDeclined() {
    }

    @Override
    public String name() {
        return "contract.negotiation.declined";
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder extends ContractNegotiationEvent.Builder<ContractNegotiationDeclined, Builder> {

        private Builder() {
            super(new ContractNegotiationDeclined());
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        @Override
        public Builder self() {
            return this;
        }
    }

}