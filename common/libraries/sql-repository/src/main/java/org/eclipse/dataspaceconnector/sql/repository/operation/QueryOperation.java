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

package org.eclipse.dataspaceconnector.sql.repository.operation;

import org.eclipse.dataspaceconnector.sql.repository.mapper.IdMapper;
import org.eclipse.dataspaceconnector.sql.repository.mapper.PropertyMapper;
import org.eclipse.dataspaceconnector.sql.repository.serializer.EnvelopePacker;
import org.eclipse.dataspaceconnector.sql.repository.types.Property;
import org.eclipse.dataspaceconnector.sql.repository.util.PreparedStatementResourceReader;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.SqlClient;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QueryOperation {

    private final SqlClient sqlClient;

    public QueryOperation(@NotNull SqlClient sqlClient) {
        this.sqlClient = Objects.requireNonNull(sqlClient);
    }

    @NotNull
    public List<Asset> invoke(@NotNull List<Criterion> criteria) throws SQLException {
        Objects.requireNonNull(criteria);
        throwOnNotSupported(criteria);

        if (criteria.isEmpty()) {
            return Collections.emptyList();
        }

        String sqlQueryTemplate = PreparedStatementResourceReader.readAssetQuery();
        String sqlPropertiesByKv = PreparedStatementResourceReader.readPropertiesSelectByKv();
        String sqlPropertiesByAssetId = PreparedStatementResourceReader.readPropertiesSelectByAssetId();
        String sqlAssetsAll = PreparedStatementResourceReader.readAssetSelectAll();

        List<String> targetAssetIds;
        if (isSelectAll(criteria)) {
            targetAssetIds = sqlClient.execute(new  IdMapper(), sqlAssetsAll);
        } else {
            StringBuilder sqlQuery = new StringBuilder(sqlQueryTemplate);
            // start with 1, because the query template already contains 1 WHERE clause
            for (int i = 1; i < criteria.size(); i++) {
                sqlQuery.append(" AND asset_id IN ( ").append(sqlPropertiesByKv).append(" )");
            }

            List<Object> arguments = new ArrayList<>();
            criteria.forEach(c -> {
                        arguments.add(c.getOperandLeft());
                        arguments.add(EnvelopePacker.pack(c.getOperandRight()));
                    }
            );

            targetAssetIds = sqlClient.execute(new IdMapper(), sqlQuery.toString(), arguments.toArray());
        }

        List<Asset> assets = new ArrayList<>();

        for (String assetId : targetAssetIds) {
            List<Property> properties = sqlClient.execute(new PropertyMapper(), sqlPropertiesByAssetId, assetId);
            //noinspection unchecked
            assets.add(Asset.Builder.newInstance().properties(asMap(properties)).build());
        }

        return assets;
    }

    private void throwOnNotSupported(List<Criterion> criteria) {

        if (isSelectAll(criteria)) {
            return;
        }

        for (Criterion criterion : criteria) {
            if (!(criterion.getOperandLeft() instanceof String)) {
                throw new EdcException("SQL-Repository: Criterion left operand must be of type string");
            }
            if (!criterion.getOperator().equals("=") && !criterion.getOperator().equals("eq")) {
                throw new EdcException("SQL-Repository: Criterion operator must be Equals-Operator ('eq' or '=')");
            }
        }
    }

    private boolean isSelectAll(List<Criterion> criteria) {
        return criteria.size() == 1 &&
                criteria.get(0).equals(new Criterion("*", "=", "*"));
    }

    private Map<String, Object> asMap(List<Property> properties) {
        Map<String, Object> map = new HashMap<>();
        properties.forEach(p -> map.put(p.getKey(), p.getValue()));
        return map;
    }
}
