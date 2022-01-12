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

package org.eclipse.dataspaceconnector.sql.repository.operations;

import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.repository.connection.ConnectionProvider;
import org.eclipse.dataspaceconnector.sql.repository.mapper.ExistsMapper;
import org.eclipse.dataspaceconnector.sql.repository.mapper.PropertyMapper;
import org.eclipse.dataspaceconnector.sql.repository.serializer.EnvelopePacker;
import org.eclipse.dataspaceconnector.sql.repository.types.Property;
import org.eclipse.dataspaceconnector.sql.repository.util.PreparedStatementResourceReader;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.execute;

public class UpdateOperation extends AbstractOperation {

    public UpdateOperation(@NotNull ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    public void invoke(@NotNull Asset asset) throws SQLException {
        Objects.requireNonNull(asset);

        String sqlAssetExists = PreparedStatementResourceReader.readAssetExists();
        String sqlProperties = PreparedStatementResourceReader.readPropertiesSelectByAssetId();
        String sqlPropertyCreate = PreparedStatementResourceReader.readPropertyCreate();
        String sqlPropertyDelete = PreparedStatementResourceReader.readPropertyDelete();
        String sqlPropertyUpdate = PreparedStatementResourceReader.readPropertyUpdate();

        this.runTransaction(connection -> {

                    List<Boolean> existsResult = execute(connection, new ExistsMapper(), sqlAssetExists, asset.getId());
                    boolean exists = existsResult.size() == 1 && existsResult.get(0);
                    if (!exists) {
                        throw new SQLException(String.format("SQL-Repository: Asset with id %s does not exist", asset.getId()));
                    }

                    List<Property> storedProperties = execute(connection, new PropertyMapper(), sqlProperties, asset.getId());

                    List<Property> propertyToDelete = findPropertiesToDelete(asset, storedProperties);
                    List<Property> propertyToInsert = findPropertiesToInsert(asset, storedProperties);
                    List<Property> propertyToUpdate = findPropertiesToUpdate(asset, storedProperties);

                    for (var property : propertyToDelete) {
                        execute(connection, sqlPropertyDelete, asset.getId(), property.getKey());
                    }
                    for (var property : propertyToInsert) {
                        String value = EnvelopePacker.pack(property.getValue());
                        execute(connection, sqlPropertyCreate, asset.getId(), property.getKey(), value);
                    }
                    for (var property : propertyToUpdate) {
                        String value = EnvelopePacker.pack(property.getValue());
                        execute(connection, sqlPropertyUpdate, value, asset.getId(), property.getKey());
                    }

                    return null;
                }
        );

    }

    private List<Property> findPropertiesToUpdate(Asset asset, List<Property> storedProperties) {
        List<Property> propertiesToUpdate = new ArrayList<>();
        Map<String, Object> assetProperties = asset.getProperties();

        for (Property storedProperty : storedProperties) {
            if (!assetProperties.containsKey(storedProperty.getKey())) {
                continue;
            }

            Object value = assetProperties.get(storedProperty.getKey());

            if (!value.equals(storedProperty.getValue())) {
                propertiesToUpdate.add(new Property(storedProperty.getKey(), value));
            }
        }

        return propertiesToUpdate;
    }

    private List<Property> findPropertiesToDelete(Asset asset, List<Property> storedProperties) {
        List<Property> propertiesToDelete = new ArrayList<>();
        Map<String, Object> assetProperties = asset.getProperties();

        for (Property storedProperty : storedProperties) {
            if (!assetProperties.containsKey(storedProperty.getKey())) {
                propertiesToDelete.add(storedProperty);
            }
        }

        return propertiesToDelete;
    }

    private List<Property> findPropertiesToInsert(Asset asset, List<Property> storedProperties) {
        List<Property> propertiesToInsert = new ArrayList<>();
        Map<String, Object> assetProperties = asset.getProperties();

        for (var assetProperty : assetProperties.entrySet()) {
            if (storedProperties.stream()
                    .noneMatch(p -> p.getKey().equals(assetProperty.getKey()))) {
                propertiesToInsert.add(new Property(assetProperty.getKey(), assetProperty.getValue()));
            }
        }

        return propertiesToInsert;
    }
}
