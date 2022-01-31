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

package org.eclipse.dataspaceconnector.sql.contractdefinitionstore;

import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.contract.offer.store.ContractDefinitionStore;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;
import org.eclipse.dataspaceconnector.sql.operations.contract.definition.SqlContractDefinitionDelete;
import org.eclipse.dataspaceconnector.sql.operations.contract.definition.SqlContractDefinitionInsert;
import org.eclipse.dataspaceconnector.sql.operations.contract.definition.SqlContractDefinitionQuery;
import org.eclipse.dataspaceconnector.sql.operations.contract.definition.SqlContractDefinitionUpdate;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.sql.DataSource;

public class SqlContractDefinitionStore implements ContractDefinitionStore {

    private final DataSource dataSource;
    private final TransactionContext transactionContext;

    public SqlContractDefinitionStore(@NotNull DataSource dataSource, @NotNull TransactionContext transactionContext) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.transactionContext = Objects.requireNonNull(transactionContext);
    }

    @Override
    public @NotNull Collection<ContractDefinition> findAll() {
        try {
            Connection connection = dataSource.getConnection();
            SqlContractDefinitionQuery query = new SqlContractDefinitionQuery(connection);

            return query.execute();
        } catch (SQLException e) {
            throw new EdcException(e);
        }
    }

    @Override
    public void save(Collection<ContractDefinition> definitions) {
        transactionContext.execute(() -> {
                    try {
                        Connection connection = dataSource.getConnection();
                        SqlContractDefinitionInsert insert = new SqlContractDefinitionInsert(connection);
                        insert.execute(definitions);
                    } catch (Exception e) {
                        throw new EdcException(e);
                    }
                }
        );
    }

    @Override
    public void save(ContractDefinition definition) {
        transactionContext.execute(() -> {
            try {
                Connection connection = dataSource.getConnection();
                SqlContractDefinitionInsert insert = new SqlContractDefinitionInsert(connection);
                insert.execute(Collections.singletonList(definition));
            } catch (SQLException e) {
                throw new EdcException(e);
            }
        });
    }

    @Override
    public void update(ContractDefinition definition) {
        transactionContext.execute(() -> {
            try {
                Connection connection = dataSource.getConnection();
                SqlContractDefinitionUpdate update = new SqlContractDefinitionUpdate(connection);
                update.execute(definition);
            } catch (SQLException e) {
                throw new EdcException(e);
            }
        });
    }

    @Override
    public void delete(ContractDefinition definition) {
        transactionContext.execute(() -> {
            try {
                Connection connection = dataSource.getConnection();
                SqlContractDefinitionDelete delete = new SqlContractDefinitionDelete(connection);
                delete.execute(definition);
            } catch (SQLException e) {
                throw new EdcException(e);
            }
        });
    }

    @Override
    public void reload() {
        // do nothing
    }
}
