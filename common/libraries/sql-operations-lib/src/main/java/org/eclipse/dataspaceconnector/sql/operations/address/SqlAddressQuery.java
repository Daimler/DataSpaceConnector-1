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

package org.eclipse.dataspaceconnector.sql.operations.address;

import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.sql.operations.mapper.IdMapper;
import org.eclipse.dataspaceconnector.sql.operations.mapper.PropertyMapper;
import org.eclipse.dataspaceconnector.sql.operations.serialization.EnvelopePacker;
import org.eclipse.dataspaceconnector.sql.operations.types.Property;
import org.eclipse.dataspaceconnector.sql.operations.util.PreparedStatementResourceReader;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

public class SqlAddressQuery {

    private final Connection connection;

    public SqlAddressQuery(@NotNull Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }



    @NotNull
    public List<DataAddress> execute() throws SQLException {
        return this.execute(Collections.emptyList());
    }

    @NotNull
    public List<DataAddress> execute(@NotNull List<Criterion> criteria) throws SQLException {
        Objects.requireNonNull(criteria);
        throwOnNotSupported(criteria);

        String sqlQueryTemplate = PreparedStatementResourceReader.readAddressQuery();
        String sqlPropertiesByKv = PreparedStatementResourceReader.readAddressPropertiesSelectByKv();
        String sqlPropertiesByAddressId = PreparedStatementResourceReader.readAddressPropertiesSelectByAddressId();
        String sqlAddressAll = PreparedStatementResourceReader.readAddressSelectAll();

        List<String> targetAddressIds;
        if (criteria.isEmpty()) {
            targetAddressIds = executeQuery(connection, new IdMapper(), sqlAddressAll);
        } else if (isSelectByAssetIdCriterion(criteria)) {
            var targetId = criteria.get(0).getOperandRight();
            if (targetId instanceof String) {
                targetAddressIds = Collections.singletonList((String) targetId);
            } else {
                throw new SQLException("PostgreSQL-Repository: Criterion right operand must be of type string for left operand asset_id");
            }
        } else {
            StringBuilder sqlQuery = new StringBuilder(sqlQueryTemplate);
            // start with 1, because the query template already contains 1 WHERE clause
            for (int i = 1; i < criteria.size(); i++) {
                sqlQuery.append(" AND address_id IN ( ").append(sqlPropertiesByKv).append(" )");
            }

            List<Object> arguments = new ArrayList<>();
            criteria.forEach(c -> {
                        arguments.add(c.getOperandLeft());
                        arguments.add(EnvelopePacker.pack(c.getOperandRight()));
                    }
            );

            targetAddressIds = executeQuery(connection, new IdMapper(), sqlQuery.toString(), arguments.toArray());
        }

        List<DataAddress> addresses = new ArrayList<>();

        for (String addressId : targetAddressIds) {
            List<Property> properties = executeQuery(connection, new PropertyMapper(), sqlPropertiesByAddressId, addressId);
            addresses.add(DataAddress.Builder.newInstance().properties(asMap(properties)).build());
        }

        return addresses;
    }

    private void throwOnNotSupported(List<Criterion> criteria) {

        if (isSelectAll(criteria)) {
            return;
        }

        if (isSelectByAssetIdCriterion(criteria)) {
            return;
        }

        for (Criterion criterion : criteria) {
            if (!(criterion.getOperandLeft() instanceof String)) {
                throw new EdcException("PostgreSQL-Repository: Criterion left operand must be of type string");
            }
            if (!criterion.getOperator().equals("=") && !criterion.getOperator().equals("eq")) {
                throw new EdcException("PostgreSQL-Repository: Criterion operator must be Equals-Operator ('eq' or '=')");
            }
        }
    }

    private boolean isSelectAll(List<Criterion> criteria) {
        return criteria.size() == 1 &&
                criteria.get(0).equals(new Criterion("*", "=", "*"));
    }

    private boolean isSelectByAssetIdCriterion(List<Criterion> criteria) {
        return criteria.size() == 1 &&
                criteria.get(0).getOperandLeft().equals("asset_id") &&
                criteria.get(0).getOperator().equals("=");
    }

    private Map<String, Object> asMap(List<Property> properties) {
        Map<String, Object> map = new HashMap<>();
        properties.forEach(p -> map.put(p.getKey(), p.getValue()));
        return map;
    }
}
