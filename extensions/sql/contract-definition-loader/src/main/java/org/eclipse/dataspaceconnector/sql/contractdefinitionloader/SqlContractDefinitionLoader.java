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

package org.eclipse.dataspaceconnector.sql.contractdefinitionloader;

import org.eclipse.dataspaceconnector.dataloading.ContractDefinitionLoader;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;
import org.eclipse.dataspaceconnector.sql.operations.contract.definition.SqlContractDefinitionInsert;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import javax.sql.DataSource;

public class SqlContractDefinitionLoader implements ContractDefinitionLoader {

    private final DataSource dataSource;
    private final TransactionContext transactionContext;

    public SqlContractDefinitionLoader(@NotNull DataSource dataSource, @NotNull TransactionContext transactionContext) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.transactionContext = Objects.requireNonNull(transactionContext);
    }

    @Override
    public void accept(ContractDefinition item) {
        Objects.requireNonNull(item);

        transactionContext.execute(() -> {

            try {
                Connection connection = dataSource.getConnection();
                SqlContractDefinitionInsert insert = new SqlContractDefinitionInsert(connection);

                insert.execute(item);
            } catch (SQLException e) {
                throw new EdcException(e);
            }
        });
    }
}
