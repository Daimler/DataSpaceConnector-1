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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.asset.DataAddressResolver;
import org.eclipse.dataspaceconnector.spi.persistence.EdcPersistenceException;
import org.eclipse.dataspaceconnector.spi.query.QuerySpec;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.ResultSetMapper;
import org.eclipse.dataspaceconnector.sql.SqlQueryExecutor;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.sql.DataSource;

public class SqlAssetIndex implements AssetIndex, DataAddressResolver {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final DataSource dataSource;

    public SqlAssetIndex(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    private static String writeObject(Object object) {
        try {
            String className = object.getClass().getName();
            String content = OBJECT_MAPPER.writeValueAsString(object);

            Envelope envelope = new Envelope(className, content);

            return OBJECT_MAPPER.writeValueAsString(envelope);
        } catch (Exception e) {
            throw new EdcException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T readObject(String value) {
        try {
            Envelope envelope = OBJECT_MAPPER.readValue(value, Envelope.class);
            return (T) OBJECT_MAPPER.readValue(envelope.getContent(), Class.forName(envelope.getClassName()));
        } catch (Exception e) {
            throw new EdcException(e.getMessage(), e);
        }
    }

    @Override
    public Stream<Asset> queryAssets(AssetSelectorExpression expression) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Stream<Asset> queryAssets(QuerySpec querySpec) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public @Nullable Asset findById(String assetId) {
        Objects.requireNonNull(assetId);

        try (Connection connection = dataSource.getConnection()) {
            boolean exists = SqlQueryExecutor.executeQuery(connection, (rs) -> rs.getString(1), Queries.ASSET_SELECT_BY_ID, assetId).iterator().hasNext();
            if (!exists) {
                return null;
            }

            Map<String, Object> assetProperties = new HashMap<>();
            SqlQueryExecutor.executeQuery(
                            connection,
                            AssetPropertiesMapper.INSTANCE,
                            Queries.ASSET_PROPERTIES_SELECT_BY_ASSET_ID,
                            assetId)
                    .forEach(assetProperties::putAll);

            return Asset.Builder.newInstance().properties(assetProperties).id(assetId).build();
        } catch (Exception exception) {
            throw new EdcPersistenceException(exception);
        }
    }

    @Override
    public DataAddress resolveForAsset(String assetId) {
        Objects.requireNonNull(assetId);

        try (Connection connection = dataSource.getConnection()) {
            boolean exists = SqlQueryExecutor.executeQuery(connection, (rs) -> rs.getString(1), Queries.ASSET_SELECT_BY_ID, assetId).iterator().hasNext();
            if (!exists) {
                return null;
            }

            List<DataAddress> list = SqlQueryExecutor.executeQuery(
                    connection,
                    AssetDataAddress.INSTANCE,
                    Queries.ASSET_DATA_ADDRESS_SELECT_BY_ASSET_ID,
                    assetId);

            if (list.isEmpty()) {
                return null;
            }

            return list.iterator().next();
        } catch (Exception exception) {
            throw new EdcPersistenceException(exception);
        }
    }

    private enum AssetPropertiesMapper implements ResultSetMapper<Map<String, Object>> {
        INSTANCE;

        @Override
        public Map<String, Object> mapResultSet(ResultSet resultSet) throws Exception {
            return Collections.singletonMap(resultSet.getString("k"), readObject(resultSet.getString("v"))); // TODO externalize column naming
        }
    }

    private enum AssetDataAddress implements ResultSetMapper<DataAddress> {
        INSTANCE;

        @Override
        public DataAddress mapResultSet(ResultSet resultSet) throws Exception {
            Map<String, String> properties = readObject(resultSet.getString("properties"));
            return DataAddress.Builder.newInstance().properties(properties).build();
        }
    }

    private static final class Envelope {
        private String className;
        private String content;

        public Envelope() {
        }

        public Envelope(String className, String content) {
            this.className = className;
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }
}
