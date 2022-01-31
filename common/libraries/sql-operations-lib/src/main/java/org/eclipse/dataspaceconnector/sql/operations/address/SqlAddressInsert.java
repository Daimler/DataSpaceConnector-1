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
import org.eclipse.dataspaceconnector.sql.operations.serialization.EnvelopePacker;
import org.eclipse.dataspaceconnector.sql.operations.util.PreparedStatementResourceReader;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

public class SqlAddressInsert {

    private final Connection connection;

    public SqlAddressInsert(@NotNull Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    public void execute(@NotNull String assetId, @NotNull DataAddress dataAddress) throws SQLException {
        Objects.requireNonNull(assetId);
        Objects.requireNonNull(dataAddress);

        String sqlCreateAddress = PreparedStatementResourceReader.readAddressCreate();
        String sqlCreateAddressProperty = PreparedStatementResourceReader.readAddressPropertyCreate();

        executeQuery(connection, sqlCreateAddress, assetId);

        for (var entrySet : dataAddress.getProperties().entrySet()) {
            String key = entrySet.getKey();
            String value = EnvelopePacker.pack(entrySet.getValue());

            executeQuery(connection, sqlCreateAddressProperty, assetId, key, value);
        }
    }
}
