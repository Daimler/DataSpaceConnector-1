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

package org.eclipse.dataspaceconnector.sql;

import org.h2.Driver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

public class SqlQueryExecutorIntegrationTest {

    private Connection connection;

    static {
        Driver.load();
    }

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:test", new Properties());
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback();
        connection.close();
    }

    @Test
    void testExecute() throws SQLException {
        SqlQueryExecutor.execute(connection, "SELECT 1;");
    }

    @Test
    void testExecuteWithRowMapping() throws SQLException {
        List<Long> result = SqlQueryExecutor.execute(connection, CountResultSetMapper.INSTANCE, "SELECT 1;");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.iterator().next());
    }

    @Test
    void testTransaction() throws SQLException {
        SqlQueryExecutor.execute(connection, "SELECT COUNT(c), COUNT(*) FROM (VALUES (1), (NULL)) t(c);");

        connection.commit();
    }

    @Test
    void testTransactionAndResultSetMapper() throws SQLException {
        String table = "kv_testTransactionAndResultSetMapper";
        String schema = getTableSchema(table);
        Kv kv = new Kv(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        SqlQueryExecutor.execute(connection, schema);
        SqlQueryExecutor.execute(connection, String.format("INSERT INTO %s (k, v) values (?, ?)", table), kv.key, kv.value);

        connection.commit();

        List<Long> countResult = SqlQueryExecutor.execute(connection, CountResultSetMapper.INSTANCE, String.format("SELECT COUNT(*) FROM %s", table));

        Assertions.assertNotNull(countResult);
        Assertions.assertEquals(1, countResult.size());
        Assertions.assertEquals(1, countResult.iterator().next());

        List<Kv> kvs = SqlQueryExecutor.execute(connection, (rs) -> new Kv(rs.getString(1), rs.getString(2)), String.format("SELECT * FROM %s", table));

        Assertions.assertNotNull(kvs);
        Assertions.assertEquals(1, kvs.size());
        Assertions.assertEquals(kv, kvs.iterator().next());
    }

    @Test
    void testInvalidSql() {
        Assertions.assertThrows(SQLException.class, () -> SqlQueryExecutor.execute(connection, "Lorem ipsum dolor sit amet"));
    }

    private String getTableSchema(String tableName) {
        return String.format("" +
                "CREATE TABLE %s (\n" +
                "    k VARCHAR(80) PRIMARY KEY NOT NULL,\n" +
                "    v VARCHAR(80) NOT NULL\n" +
                ");", Objects.requireNonNull(tableName));
    }

    private enum CountResultSetMapper implements ResultSetMapper<Long> {
        INSTANCE;

        @Override
        public Long mapResultSet(ResultSet resultSet) throws SQLException {
            return resultSet.getLong(1);
        }
    }

    private static class Kv {
        final String key;
        final String value;

        public Kv(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Kv kv = (Kv) o;
            return key.equals(kv.key) && Objects.equals(value, kv.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }
}
