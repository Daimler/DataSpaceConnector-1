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

package org.eclipse.dataspaceconnector.sql.assetloader;

import org.eclipse.dataspaceconnector.dataloading.AssetEntry;
import org.eclipse.dataspaceconnector.dataloading.AssetLoader;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.operations.address.SqlAddressInsert;
import org.eclipse.dataspaceconnector.sql.operations.asset.SqlAssetInsert;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class SqlAssetLoader implements AssetLoader {

    private final DataSource dataSource;
    private final TransactionContext transactionContext;

    public SqlAssetLoader(@NotNull DataSource dataSource, @NotNull TransactionContext transactionContext) {
        this.dataSource = dataSource;
        this.transactionContext = transactionContext;
    }

    @Override
    public void accept(AssetEntry item) {
        this.accept(item.getAsset(), item.getDataAddress());
    }

    @Override
    public void accept(Asset asset, DataAddress dataAddress) {
        transactionContext.execute(() -> {

            try {
                Connection connection = dataSource.getConnection();

                SqlAssetInsert assetInsert = new SqlAssetInsert(connection);
                SqlAddressInsert addressInsert = new SqlAddressInsert(connection);

                assetInsert.execute(asset);
                addressInsert.execute(asset.getId(), dataAddress);
            } catch (SQLException e) {
                throw new EdcException(e);
            }
        });
    }
}
