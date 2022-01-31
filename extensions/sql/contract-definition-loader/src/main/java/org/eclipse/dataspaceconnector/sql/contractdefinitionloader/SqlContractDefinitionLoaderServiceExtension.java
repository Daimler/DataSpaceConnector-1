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

package org.eclipse.dataspaceconnector.sql.contractdefinitionloader;

import org.eclipse.dataspaceconnector.dataloading.ContractDefinitionLoader;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;

import javax.sql.DataSource;

@Provides(ContractDefinitionLoader.class)
@Requires(TransactionContext.class)
public class SqlContractDefinitionLoaderServiceExtension implements ServiceExtension {

    @EdcSetting
    public static final String DATA_SOURCE_NAME_SETTING = "edc.contract.definition.datasource.name";

    private static final String DATA_SOURCE_MISSING_MSG = "Missing data source for name '%s'";

    @Override
    public String name() {
        return "SQL Contract Definition Loader Service Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {

        TransactionContext transactionContext = context.getService(TransactionContext.class);
        String dataSourceName = context.getSetting(DATA_SOURCE_NAME_SETTING, DataSourceRegistry.DEFAULT_DATASOURCE);
        DataSourceRegistry dataSourceRegistry = context.getService(DataSourceRegistry.class);

        DataSource dataSource = dataSourceRegistry.resolve(dataSourceName);
        if (dataSource == null) {
            throw new EdcException(String.format(DATA_SOURCE_MISSING_MSG, dataSourceName));
        }

        SqlContractDefinitionLoader sqlContractDefinitionLoader = new SqlContractDefinitionLoader(dataSource, transactionContext);
        context.registerService(SqlContractDefinitionLoader.class, sqlContractDefinitionLoader);

    }
}
