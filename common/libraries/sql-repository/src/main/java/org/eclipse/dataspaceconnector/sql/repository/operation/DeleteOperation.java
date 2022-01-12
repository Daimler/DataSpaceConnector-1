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

import org.eclipse.dataspaceconnector.sql.repository.util.PreparedStatementResourceReader;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.SqlClient;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Objects;

public class DeleteOperation {
    private final SqlClient sqlClient;

    public DeleteOperation(@NotNull SqlClient sqlClient) {
        this.sqlClient = Objects.requireNonNull(sqlClient);
    }

    public void invoke(@NotNull Asset asset) throws SQLException {
        Objects.requireNonNull(asset);

        String sqlDelete = PreparedStatementResourceReader.readAssetDelete();

        sqlClient.execute(sqlDelete, asset.getId());
    }
}
