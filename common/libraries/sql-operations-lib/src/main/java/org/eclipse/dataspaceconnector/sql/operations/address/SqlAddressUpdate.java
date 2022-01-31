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

import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.sql.operations.mapper.ExistsMapper;
import org.eclipse.dataspaceconnector.sql.operations.mapper.PropertyMapper;
import org.eclipse.dataspaceconnector.sql.operations.serialization.EnvelopePacker;
import org.eclipse.dataspaceconnector.sql.operations.types.Property;
import org.eclipse.dataspaceconnector.sql.operations.util.PreparedStatementResourceReader;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

public class SqlAddressUpdate {

    private final Connection connection;

    public SqlAddressUpdate(@NotNull Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    public void invoke(@NotNull String assetId, @NotNull DataAddress address) throws SQLException {
        Objects.requireNonNull(assetId);
        Objects.requireNonNull(address);

        String sqlAddressExists = PreparedStatementResourceReader.readAddressExists();
        String sqlAssetExists = PreparedStatementResourceReader.readAddressExists();
        String sqlProperties = PreparedStatementResourceReader.readAddressPropertiesSelectByAddressId();
        String sqlPropertyCreate = PreparedStatementResourceReader.readAddressPropertyCreate();
        String sqlPropertyDelete = PreparedStatementResourceReader.readAddressPropertyDelete();
        String sqlPropertyUpdate = PreparedStatementResourceReader.readAddressPropertyUpdate();

        List<Boolean> existsAssetResult = executeQuery(connection, new ExistsMapper(), sqlAssetExists, assetId);
        boolean existsAsset = existsAssetResult.size() == 1 && existsAssetResult.get(0);
        if (!existsAsset) {
            throw new SQLException(String.format("Asset with id %s does not exist", assetId));
        }

        List<Boolean> existsAddressResult = executeQuery(connection, new ExistsMapper(), sqlAddressExists, assetId);
        boolean existsAddress = existsAddressResult.size() == 1 && existsAddressResult.get(0);
        if (!existsAddress) {
            throw new SQLException(String.format("Address with asset id %s does not exist", assetId));
        }

        List<Property> storedProperties = executeQuery(connection, new PropertyMapper(), sqlProperties, assetId);

        List<Property> propertyToDelete = findPropertiesToDelete(address, storedProperties);
        List<Property> propertyToInsert = findPropertiesToInsert(address, storedProperties);
        List<Property> propertyToUpdate = findPropertiesToUpdate(address, storedProperties);

        for (var property : propertyToDelete) {
            executeQuery(connection, sqlPropertyDelete, assetId, property.getKey());
        }
        for (var property : propertyToInsert) {
            String value = EnvelopePacker.pack(property.getValue());
            executeQuery(connection, sqlPropertyCreate, assetId, property.getKey(), value);
        }
        for (var property : propertyToUpdate) {
            String value = EnvelopePacker.pack(property.getValue());
            executeQuery(connection, sqlPropertyUpdate, value, assetId, property.getKey());
        }
    }

    private List<Property> findPropertiesToUpdate(DataAddress address, List<Property> storedProperties) {
        List<Property> propertiesToUpdate = new ArrayList<>();
        Map<String, String> assetProperties = address.getProperties();

        for (Property storedProperty : storedProperties) {
            if (!assetProperties.containsKey(storedProperty.getKey())) {
                continue;
            }

            String value = assetProperties.get(storedProperty.getKey());

            if (!value.equals(storedProperty.getValue())) {
                propertiesToUpdate.add(new Property(storedProperty.getKey(), value));
            }
        }

        return propertiesToUpdate;
    }

    private List<Property> findPropertiesToDelete(DataAddress address, List<Property> storedProperties) {
        List<Property> propertiesToDelete = new ArrayList<>();
        Map<String, String> assetProperties = address.getProperties();

        for (Property storedProperty : storedProperties) {
            if (!assetProperties.containsKey(storedProperty.getKey())) {
                propertiesToDelete.add(storedProperty);
            }
        }

        return propertiesToDelete;
    }

    private List<Property> findPropertiesToInsert(DataAddress address, List<Property> storedProperties) {
        List<Property> propertiesToInsert = new ArrayList<>();
        Map<String, String> assetProperties = address.getProperties();

        for (var assetProperty : assetProperties.entrySet()) {
            if (storedProperties.stream()
                    .noneMatch(p -> p.getKey().equals(assetProperty.getKey()))) {
                propertiesToInsert.add(new Property(assetProperty.getKey(), assetProperty.getValue()));
            }
        }

        return propertiesToInsert;
    }
}
