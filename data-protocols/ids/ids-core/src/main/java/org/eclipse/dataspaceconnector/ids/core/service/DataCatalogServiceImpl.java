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

package org.eclipse.dataspaceconnector.ids.core.service;

import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.dataspaceconnector.ids.spi.service.DataCatalogService;
import org.eclipse.dataspaceconnector.ids.spi.types.DataCatalog;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferQuery;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferQueryResponse;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferService;
import org.eclipse.dataspaceconnector.spi.iam.VerificationResult;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

public class DataCatalogServiceImpl implements DataCatalogService {
    private final Monitor monitor;
    private final DataCatalogServiceSettings dataCatalogServiceSettings;
    private final ContractOfferService contractOfferService;

    public DataCatalogServiceImpl(
            @NotNull Monitor monitor,
            @NotNull DataCatalogServiceSettings dataCatalogServiceSettings,
            @NotNull ContractOfferService contractOfferService) {
        this.monitor = monitor;
        this.dataCatalogServiceSettings = Objects.requireNonNull(dataCatalogServiceSettings);
        this.contractOfferService = Objects.requireNonNull(contractOfferService);
    }

    /**
     * Provides the dataCatalog object, which may be used by the IDS self-description of the connector.
     *
     * @return data catalog
     */
    @Override
    @NotNull
    public DataCatalog getDataCatalog(VerificationResult verificationResult) {

        var query = ContractOfferQuery.Builder.newInstance().verificationResult(verificationResult).build();
        ContractOfferQueryResponse response = contractOfferService.queryContractOffers(query);

        return DataCatalog.Builder
                .newInstance()
                .id(dataCatalogServiceSettings.getCatalogId())
                .contractOffers(response.getContractOfferStream().collect(Collectors.toList()))
                .build();
    }
}
