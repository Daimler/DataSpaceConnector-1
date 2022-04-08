/*
 *  Copyright (c) 2020 - 2022 Microsoft Corporation and others
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *       Mercedes Benz Tech Innovation - Rename DataSource name setting, and default value
 *
 */

package org.eclipse.dataspaceconnector.sql.contractnegotiation;

import org.eclipse.dataspaceconnector.spi.contract.negotiation.store.ContractNegotiationStore;
import org.eclipse.dataspaceconnector.spi.policy.store.PolicyArchive;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;
import org.eclipse.dataspaceconnector.sql.contractnegotiation.store.ContractNegotiationStatements;
import org.eclipse.dataspaceconnector.sql.contractnegotiation.store.PostgresStatements;
import org.eclipse.dataspaceconnector.sql.contractnegotiation.store.SqlContractNegotiationStore;

import static org.eclipse.dataspaceconnector.sql.contractnegotiation.ConfigurationKeys.DATASOURCE_SETTING_NAME;
import static org.eclipse.dataspaceconnector.sql.contractnegotiation.ConfigurationKeys.DATASOURCE_SETTING_NAME_DEFAULT;

@Provides({ ContractNegotiationStore.class, PolicyArchive.class })
public class SqlContractNegotiationStoreExtension implements ServiceExtension {

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Inject
    private TransactionContext trxContext;

    @Inject(required = false)
    private ContractNegotiationStatements statements;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var sqlStore = new SqlContractNegotiationStore(dataSourceRegistry, getDataSourceName(context), trxContext, context.getTypeManager(), getStatementImpl(), context.getConnectorId());
        context.registerService(ContractNegotiationStore.class, sqlStore);
        context.registerService(PolicyArchive.class, sqlStore);
    }

    /**
     * returns an externally-provided sql statement dialect, or postgres as a default
     */
    private ContractNegotiationStatements getStatementImpl() {
        return statements != null ? statements : new PostgresStatements();
    }

    private String getDataSourceName(ServiceExtensionContext context) {
        return context.getConfig().getString(DATASOURCE_SETTING_NAME, DATASOURCE_SETTING_NAME_DEFAULT);
    }
}
