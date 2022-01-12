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

package org.eclipse.dataspaceconnector.sql.repository;

import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.sql.repository.connection.ConnectionProvider;
import org.eclipse.dataspaceconnector.sql.repository.connection.PooledConnectionProvider;
import org.eclipse.dataspaceconnector.sql.repository.operations.CreateOperation;
import org.eclipse.dataspaceconnector.sql.repository.operations.DeleteOperation;
import org.eclipse.dataspaceconnector.sql.repository.operations.QueryOperation;
import org.eclipse.dataspaceconnector.sql.repository.operations.UpdateOperation;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class RepositoryImpl implements Repository {

    private final CreateOperation createOperation;
    private final UpdateOperation updateOperation;
    private final DeleteOperation deleteOperation;
    private final QueryOperation queryOperation;

    /**
     * The repository will a connection from the pool for each operation.
     *
     * @param connectionPool the pool that manages the connections
     */
    public RepositoryImpl(@NotNull ConnectionPool connectionPool) {
        ConnectionProvider connectionProvider = new PooledConnectionProvider(connectionPool));

        this.createOperation = new CreateOperation(connectionProvider);
        this.updateOperation = new UpdateOperation(connectionProvider);
        this.deleteOperation = new DeleteOperation(connectionProvider);
        this.queryOperation = new QueryOperation(connectionProvider);
    }

    @NotNull
    @Override
    public List<Asset> query(@NotNull List<Criterion> criteria) throws SQLException {
        return queryOperation.invoke(Objects.requireNonNull(criteria));
    }

    @Override
    public void create(@NotNull Asset asset) throws SQLException {
        createOperation.invoke(Objects.requireNonNull(asset));
    }

    @Override
    public void update(@NotNull Asset asset) throws SQLException {
        updateOperation.invoke(Objects.requireNonNull(asset));
    }

    @Override
    public void delete(@NotNull Asset asset) throws SQLException {
        deleteOperation.invoke(Objects.requireNonNull(asset));
    }
}
