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

package org.eclipse.dataspaceconnector.sql.asset.index;

import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.DataAddressResolver;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;

import javax.sql.DataSource;

public class SqlAssetIndexServiceExtension implements ServiceExtension {

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Override
    public void initialize(ServiceExtensionContext context) {
        DataSource dataSource = dataSourceRegistry.resolve("asset"); // TODO configuration

        SqlAssetIndex sqlAssetIndex = new SqlAssetIndex(dataSource);

        context.registerService(AssetIndex.class, sqlAssetIndex);
        context.registerService(DataAddressResolver.class, sqlAssetIndex);
    }
}
