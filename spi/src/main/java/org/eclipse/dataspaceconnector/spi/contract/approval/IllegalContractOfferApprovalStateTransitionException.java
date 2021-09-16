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

public class IllegalContractOfferApprovalStateTransitionException extends ContractOfferApprovalException {
    private final ContractOfferApproval.State source;
    private final ContractOfferApproval.State target;

    public IllegalContractOfferApprovalStateTransitionException(
            final ContractOfferApproval.State source,
            final ContractOfferApproval.State target) {
        this.source = source;
        this.target = target;
    }

    public ContractOfferApproval.State getSource() {
        return source;
    }

    public ContractOfferApproval.State getTarget() {
        return target;
    }
}
