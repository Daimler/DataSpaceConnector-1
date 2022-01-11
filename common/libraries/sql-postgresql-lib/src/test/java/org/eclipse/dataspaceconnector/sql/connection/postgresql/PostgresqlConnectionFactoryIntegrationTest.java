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

package org.eclipse.dataspaceconnector.sql.connection.postgresql;

import org.eclipse.dataspaceconnector.common.annotations.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@IntegrationTest
public class PostgresqlConnectionFactoryIntegrationTest {

    private static PostgresqlConnectionFactory postgresqlConnectionFactory;

    @BeforeAll
    static void setUp() {
        PostgresqlConnectionFactoryConfig postgresqlConnectionFactoryConfig = PostgresqlConnectionFactoryConfig.Builder.newInstance()
                .uri(URI.create(String.format("jdbc://postgres:5432/%s", System.getenv("POSTGRES_DB"))))
                .userName(System.getenv("POSTGRES_USER"))
                .password(System.getenv("POSTGRES_PASSWORD"))
                .build();

        postgresqlConnectionFactory = new PostgresqlConnectionFactory(postgresqlConnectionFactoryConfig);
    }

    @Test
    void testCreate() throws SQLException {
        try (Connection connection = postgresqlConnectionFactory.create()) {
            Assertions.assertNotNull(connection);
        }
    }

    @Test
    void testExecutePreparedStatement() throws SQLException {
        try (Connection connection = postgresqlConnectionFactory.create()) {
            Assertions.assertNotNull(connection);

            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1;")) {
                Assertions.assertTrue(preparedStatement.execute());
            }
        }
    }
}
