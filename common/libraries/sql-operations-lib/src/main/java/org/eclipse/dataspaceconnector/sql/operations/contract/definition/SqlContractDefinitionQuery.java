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

package org.eclipse.dataspaceconnector.sql.operations.contract.definition;

import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;
import org.eclipse.dataspaceconnector.sql.operations.mapper.ContractDefinitionMapper;
import org.eclipse.dataspaceconnector.sql.operations.util.PreparedStatementResourceReader;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

public class SqlContractDefinitionQuery {
    private final Connection connection;

    public SqlContractDefinitionQuery(@NotNull Connection postgresClient) {
        this.connection = Objects.requireNonNull(postgresClient);
    }

    @NotNull
    public List<ContractDefinition> execute() throws SQLException {
        String statement = PreparedStatementResourceReader.readContractDefinitionSelectAll();
        return executeQuery(connection, new ContractDefinitionMapper(), statement);
    }
}
