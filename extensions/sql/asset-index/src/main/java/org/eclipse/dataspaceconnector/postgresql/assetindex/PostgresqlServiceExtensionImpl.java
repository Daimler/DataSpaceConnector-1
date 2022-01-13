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

package org.eclipse.dataspaceconnector.postgresql.assetindex;

import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.jetbrains.annotations.NotNull;

@Provides(AssetIndex.class)
public class PostgresqlServiceExtensionImpl implements ServiceExtension {

    @Override
    public String name() {
        return "PostgreSql Asset Service Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        AssetIndex assetIndex = createAssetIndex(context);
        context.registerService(AssetIndex.class, assetIndex);
    }

    @NotNull
    private AssetIndex createAssetIndex(ServiceExtensionContext context) {
        ConnectionPool connectionPool = context.getService(ConnectionPool.class);
        return new PostgresqlAssetIndex(connectionPool);
    }
}
