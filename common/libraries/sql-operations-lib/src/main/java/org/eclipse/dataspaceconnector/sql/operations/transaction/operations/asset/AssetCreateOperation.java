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
package org.eclipse.dataspaceconnector.sql.operations.transaction.operations.asset;

import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.operations.serialization.EnvelopePacker;
import org.eclipse.dataspaceconnector.sql.operations.transaction.operations.TransactionOperation;
import org.eclipse.dataspaceconnector.sql.operations.util.PreparedStatementResourceReader;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

public class AssetCreateOperation implements TransactionOperation {

    private final Asset asset;

    public AssetCreateOperation(@NotNull Asset asset) {
        this.asset = Objects.requireNonNull(asset);
    }

    @Override
    public void execute(Connection connection) throws SQLException {

        String sqlCreateAsset = PreparedStatementResourceReader.readAssetCreate();
        String sqlCreateProperty = PreparedStatementResourceReader.readPropertyCreate();

        executeQuery(connection, sqlCreateAsset, asset.getId());

        for (var entrySet : asset.getProperties().entrySet()) {
            String id = asset.getId();
            String key = entrySet.getKey();
            String value = EnvelopePacker.pack(entrySet.getValue());

            executeQuery(connection, sqlCreateProperty, id, key, value);
        }
    }
}
