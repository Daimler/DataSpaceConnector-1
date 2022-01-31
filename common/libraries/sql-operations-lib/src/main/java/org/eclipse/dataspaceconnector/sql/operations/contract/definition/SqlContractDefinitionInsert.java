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
import org.eclipse.dataspaceconnector.sql.operations.serialization.EnvelopePacker;
import org.eclipse.dataspaceconnector.sql.operations.util.PreparedStatementResourceReader;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

public class SqlContractDefinitionInsert {
    private final Connection connection;

    public SqlContractDefinitionInsert(@NotNull Connection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    public int execute(@NotNull ContractDefinition contractDefinition) throws SQLException {
        Objects.requireNonNull(contractDefinition);

        String packedExpression = EnvelopePacker.pack(contractDefinition.getSelectorExpression());
        String packedAccessPolicy = EnvelopePacker.pack(contractDefinition.getAccessPolicy());
        String packedContractPolicy = EnvelopePacker.pack(contractDefinition.getContractPolicy());

        String statement = PreparedStatementResourceReader.readContractDefinitionCreate();

        return executeQuery(connection,
                statement,
                contractDefinition.getId(),
                packedExpression,
                packedAccessPolicy,
                packedContractPolicy);
    }

    public void execute(@NotNull Collection<ContractDefinition> contractDefinitions) throws SQLException {
        Objects.requireNonNull(contractDefinitions);

        String statement = PreparedStatementResourceReader.readContractDefinitionCreate();
        for (ContractDefinition contractDefinition : contractDefinitions) {
            String packedExpression = EnvelopePacker.pack(contractDefinition.getSelectorExpression());
            String packedAccessPolicy = EnvelopePacker.pack(contractDefinition.getAccessPolicy());
            String packedContractPolicy = EnvelopePacker.pack(contractDefinition.getContractPolicy());

            executeQuery(connection,
                    statement,
                    contractDefinition.getId(),
                    packedExpression,
                    packedAccessPolicy,
                    packedContractPolicy);
        }
    }
}
