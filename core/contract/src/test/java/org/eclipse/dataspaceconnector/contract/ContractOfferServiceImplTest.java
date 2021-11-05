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

package org.eclipse.dataspaceconnector.contract;

import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferFramework;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.easymock.EasyMock.mock;

class ContractOfferServiceImplTest {


    // mocks
    private ContractOfferFramework contractOfferFramework;
    private AssetIndex assetIndex;

    @BeforeEach
    void setUp() {

        contractOfferFramework = mock(ContractOfferFramework.class);
        assetIndex = mock(AssetIndex.class);
    }

    @Test
    void testConstructorNullParametersThrowingIllegalArgumentException() {

        // just eval all constructor parameters are mandatory and lead to NPE
        assertThatThrownBy(() -> new ContractOfferServiceImpl(contractOfferFramework, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new ContractOfferServiceImpl(null, assetIndex))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new ContractOfferServiceImpl(null, null))
                .isInstanceOf(NullPointerException.class);
    }
}