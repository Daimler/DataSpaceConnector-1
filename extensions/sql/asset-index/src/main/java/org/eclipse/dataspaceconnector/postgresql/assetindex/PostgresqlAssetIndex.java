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

import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.operations.Query;
import org.eclipse.dataspaceconnector.sql.operations.QueryBuilder;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.transaction.tx.Transactional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PostgresqlAssetIndex implements AssetIndex {

    private final ConnectionPool connectionPool;

    public PostgresqlAssetIndex(@NotNull ConnectionPool connectionPool) {
        this.connectionPool = Objects.requireNonNull(connectionPool);
    }

    @Override
    public Stream<Asset> queryAssets(AssetSelectorExpression expression) {
        return queryAssets(expression.getCriteria());
    }

    @Override
    public Stream<Asset> queryAssets(List<Criterion> criteria) {
        try {
            QueryBuilder.FilterBuilder<Asset> builder = new QueryBuilder(connectionPool)
                    .assets();

            // TODO see whether criteria must be checked for empty and select_all
            // https://github.com/eclipse-dataspaceconnector/DataSpaceConnector/pull/501

            for (Criterion criterion : criteria) {
                if (!criterion.getOperator().equals("=") && !criterion.getOperator().equals("eq")) {
                    throw new EdcException(String.format("Unsupported operator '%s'", criterion.getOperator()));
                }
                if (!(criterion.getOperandLeft() instanceof String)) {
                    throw new EdcException(String.format("Unsupported left operand. Must be String, was %s", criterion.getOperandLeft().getClass().getName()));
                }

                builder.with((String) criterion.getOperandLeft(), criterion.getOperandRight());
            }

            return builder.build()
                    .execute()
                    .stream();

        } catch (SQLException e) {
            throw new EdcException(e);
        }
    }

    @Override
    public @Nullable Asset findById(String assetId) {
        try {
            Query<Asset> query = new QueryBuilder(connectionPool)
                    .assets().with(Asset.PROPERTY_ID, assetId)
                    .build();

            return query.execute().stream()
                    .findFirst()
                    .orElse(null);

        } catch (SQLException e) {
            throw new EdcException(e);
        }
    }
}
