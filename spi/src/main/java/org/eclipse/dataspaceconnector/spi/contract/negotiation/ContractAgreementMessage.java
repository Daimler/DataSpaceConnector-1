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
import org.eclipse.dataspaceconnector.spi.types.domain.message.RemoteMessage;

import java.net.URL;

public class ContractAgreementMessage implements RemoteMessage {
    private ContractOffer contractOffer;
    private String protocol;
    private URL connectorAddress;

    public ContractOffer getContractOffer() {
        return contractOffer;
    }

    public void setContractOffer(ContractOffer contractOffer) {
        this.contractOffer = contractOffer;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setConnectorAddress(URL connectorAddress) {
        this.connectorAddress = connectorAddress;
    }

    // TODO not nullable!
    public URL getConnectorAddress() {
        return connectorAddress;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }
}
