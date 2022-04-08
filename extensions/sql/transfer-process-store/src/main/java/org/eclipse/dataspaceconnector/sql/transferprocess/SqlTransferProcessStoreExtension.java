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

package org.eclipse.dataspaceconnector.sql.transferprocess;

import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;
import org.eclipse.dataspaceconnector.spi.transfer.store.TransferProcessStore;
import org.eclipse.dataspaceconnector.sql.transferprocess.store.PostgresStatements;
import org.eclipse.dataspaceconnector.sql.transferprocess.store.SqlTransferProcessStore;
import org.eclipse.dataspaceconnector.sql.transferprocess.store.TransferProcessStoreStatements;

import static org.eclipse.dataspaceconnector.sql.transferprocess.ConfigurationKeys.DATASOURCE_SETTING_NAME;
import static org.eclipse.dataspaceconnector.sql.transferprocess.ConfigurationKeys.DATASOURCE_SETTING_NAME_DEFAULT;

@Provides(TransferProcessStore.class)
public class SqlTransferProcessStoreExtension implements ServiceExtension {

    @Inject
    private DataSourceRegistry dataSourceRegistry;
    @Inject
    private TransactionContext trxContext;

    @Inject(required = false)
    private TransferProcessStoreStatements statements;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var store = new SqlTransferProcessStore(dataSourceRegistry, getDataSourceName(context), trxContext, context.getTypeManager().getMapper(), getStatementImpl(), context.getConnectorId());
        context.registerService(TransferProcessStore.class, store);
    }

    /**
     * returns an externally-provided sql statement dialect, or postgres as a default
     */
    private TransferProcessStoreStatements getStatementImpl() {
        return statements != null ? statements : new PostgresStatements();
    }

    private String getDataSourceName(ServiceExtensionContext context) {
        return context.getConfig().getString(DATASOURCE_SETTING_NAME, DATASOURCE_SETTING_NAME_DEFAULT);
    }
}
