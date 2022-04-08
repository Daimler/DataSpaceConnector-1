/*
 *  Copyright (c) 2022 Daimler TSS GmbH and others
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial API and Implementation
 *       Microsoft Corporation - refactoring
 *       Mercedes Benz Tech Innovation - Rename DataSource name setting, and default value
 *
 */

package org.eclipse.dataspaceconnector.sql.contractdefinition.store;


import org.eclipse.dataspaceconnector.dataloading.ContractDefinitionLoader;
import org.eclipse.dataspaceconnector.spi.contract.offer.store.ContractDefinitionStore;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;

import static org.eclipse.dataspaceconnector.sql.contractdefinition.store.ConfigurationKeys.DATASOURCE_SETTING_NAME;
import static org.eclipse.dataspaceconnector.sql.contractdefinition.store.ConfigurationKeys.DATASOURCE_SETTING_NAME_DEFAULT;

@Provides({ ContractDefinitionStore.class, ContractDefinitionLoader.class })
public class SqlContractDefinitionStoreExtension implements ServiceExtension {

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Inject
    private TransactionContext transactionContext;

    @Inject(required = false)
    private ContractDefinitionStatements statements;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var dataSourceName = context.getConfig().getString(DATASOURCE_SETTING_NAME, DATASOURCE_SETTING_NAME_DEFAULT);

        var sqlContractDefinitionStore = new SqlContractDefinitionStore(dataSourceRegistry, dataSourceName, transactionContext, getStatementImpl(), context.getTypeManager().getMapper());

        context.registerService(ContractDefinitionLoader.class, sqlContractDefinitionStore::save);
        context.registerService(ContractDefinitionStore.class, sqlContractDefinitionStore);
    }

    private ContractDefinitionStatements getStatementImpl() {
        return statements == null ? new PostgresStatements() : statements;
    }

}