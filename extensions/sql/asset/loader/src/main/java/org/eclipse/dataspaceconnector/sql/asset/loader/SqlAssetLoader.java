/*
 *  Copyright (c) 2021-2022 Daimler TSS GmbH
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

package org.eclipse.dataspaceconnector.sql.asset.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.dataloading.AssetEntry;
import org.eclipse.dataspaceconnector.dataloading.AssetLoader;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.asset.schema.SqlAssetTables;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;
import java.util.Objects;

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

public class SqlAssetLoader implements AssetLoader {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final DataSource dataSource;
    private final TransactionContext transactionContext;

    public SqlAssetLoader(DataSource dataSource, TransactionContext transactionContext) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.transactionContext = Objects.requireNonNull(transactionContext);
    }

    @Override
    public final void accept(AssetEntry asset) {
        Objects.requireNonNull(asset);

        accept(asset.getAsset(), asset.getDataAddress());
    }

    @Override
    public void accept(Asset asset, DataAddress dataAddress) {
        Objects.requireNonNull(asset);
        Objects.requireNonNull(dataAddress);

        transactionContext.execute(() -> {
            try (Connection connection = dataSource.getConnection()) {
                executeQuery(connection, String.format("INSERT INTO %s (asset_id) VALUES (?)", SqlAssetTables.assetTable), asset.getId());
                executeQuery(connection, String.format("INSERT INTO %s (asset_id, properties) VALUES (?, ?)", SqlAssetTables.assetDataAddressTable), asset.getId(), writeObject(dataAddress.getProperties()));


                for (Map.Entry<String, Object> property :
                        asset.getProperties().entrySet()) {
                    executeQuery(connection, String.format("INSERT INTO %s (asset_id, k, v) VALUES (?, ?, ?)", SqlAssetTables.assetPropertiesTable), asset.getId(), property.getKey(), writeObject(property.getValue()));
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });

    }

    private String writeObject(Object object) {
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
    private <T> T readObject(String value) {
        try {
            Envelope envelope = OBJECT_MAPPER.readValue(value, Envelope.class);
            return (T) OBJECT_MAPPER.readValue(envelope.getContent(), Class.forName(envelope.getClassName()));
        } catch (Exception e) {
            throw new EdcException(e.getMessage(), e);
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
