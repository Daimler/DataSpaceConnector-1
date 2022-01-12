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

package org.eclipse.datspaceconnector.postgresql.assetindex;

import org.assertj.core.api.Assertions;
import org.eclipse.dataspaceconnector.postgresql.assetindex.PostgresqlAssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.operations.SqlConnectionExtension;
import org.eclipse.dataspaceconnector.sql.operations.TransactionBuilder;
import org.eclipse.dataspaceconnector.sql.operations.transaction.Transaction;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(SqlConnectionExtension.class)
public class PostgresqlAssetIndexComponentTest {
    private AssetIndex assetIndex;
    private ConnectionPool connectionPool;

    @BeforeEach
    public void setup(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.assetIndex = new PostgresqlAssetIndex(connectionPool);
    }

    @Test
    public void testQueryExpression() throws SQLException {
        List<Criterion> criteria = new ArrayList<>();
        criteria.add(new Criterion("foo", "=", "bar"));

        Asset asset = Asset.Builder.newInstance().property("foo", "bar").build();
        createAsset(asset);

        AssetSelectorExpression expression =
                AssetSelectorExpression.Builder.newInstance().criteria(criteria).build();
        Stream<Asset> result = assetIndex.queryAssets(expression);

        Assertions.assertThat(result.map(Asset::getId).collect(Collectors.toUnmodifiableList()))
                .contains(asset.getId());
    }

    @Test
    public void testQueryCriteria() throws SQLException {
        List<Criterion> criteria = new ArrayList<>();
        criteria.add(new Criterion("foo", "=", "bar"));

        Asset asset = Asset.Builder.newInstance().property("foo", "bar").build();
        createAsset(asset);

        Stream<Asset> result = assetIndex.queryAssets(criteria);

        Assertions.assertThat(result.map(Asset::getId).collect(Collectors.toUnmodifiableList()))
                .contains(asset.getId());
    }

    @Test
    public void testQueryId() throws SQLException {
        Asset asset = Asset.Builder.newInstance().build();
        createAsset(asset);

        Asset result = assetIndex.findById(asset.getId());

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(asset.getId());
    }

    private void createAsset(Asset asset) throws SQLException {
        Transaction transaction = new TransactionBuilder(connectionPool)
                .create(asset)
                .build();
        transaction.execute();
    }
}
