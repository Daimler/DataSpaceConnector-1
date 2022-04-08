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
 *       Mercedes Benz Tech Innovation - Add default data source config value
 *
 */

package org.eclipse.dataspaceconnector.sql.asset.index;

import org.eclipse.dataspaceconnector.dataloading.AssetLoader;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.DataAddressResolver;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;

import static org.eclipse.dataspaceconnector.sql.asset.index.ConfigurationKeys.DATASOURCE_SETTING_NAME;
import static org.eclipse.dataspaceconnector.sql.asset.index.ConfigurationKeys.DATASOURCE_SETTING_NAME_DEFAULT;


@Provides({AssetLoader.class, AssetIndex.class, DataAddressResolver.class})
public class SqlAssetIndexServiceExtension implements ServiceExtension {

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Inject
    private TransactionContext transactionContext;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var dataSourceName = context.getConfig().getString(DATASOURCE_SETTING_NAME, DATASOURCE_SETTING_NAME_DEFAULT);

        var sqlAssetLoader = new SqlAssetIndex(dataSourceRegistry, dataSourceName, transactionContext, context.getTypeManager().getMapper(), new PostgresSqlAssetQueries());

        context.registerService(AssetLoader.class, sqlAssetLoader);
        context.registerService(AssetIndex.class, sqlAssetLoader);
        context.registerService(DataAddressResolver.class, sqlAssetLoader);
    }
}
