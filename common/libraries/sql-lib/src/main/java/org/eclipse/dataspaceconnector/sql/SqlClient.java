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

import org.eclipse.dataspaceconnector.sql.connection.pool.ConnectionPool;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SqlClient {

    private final ConnectionPool connectionPool;

    /**
     * Constructor for instantiating the SqlClient
     *
     * @param connectionPool mandatory for obtaining connections
     */
    public SqlClient(@NotNull ConnectionPool connectionPool) {
        Objects.requireNonNull(connectionPool, "connectionFactory");

        this.connectionPool = connectionPool;
    }

    /**
     * Intended for mutating queries.
     *
     * @param sql       the parametrized sql query
     * @param arguments the parameteres to interpolate with the parametrized sql query
     * @return rowsChanged
     * @throws SQLException if execution of the query was failing
     */
    public int execute(String sql, Object... arguments) throws SQLException {
        Objects.requireNonNull(sql, "sql");
        Objects.requireNonNull(arguments, "arguments");

        Connection connection = connectionPool.getConnection();

        int result;
        try {
            result = execute(connection, sql, arguments);
        } finally {
            connectionPool.returnConnection(connection);
        }

        return result;
    }

    /**
     * Intended for reading queries.
     *
     * @param resultSetMapper able to map a row to an object e.g. pojo.
     * @param sql             the parametrized sql query
     * @param arguments       the parameteres to interpolate with the parametrized sql query
     * @param <T>             generic type returned after mapping from the executed query
     * @return results
     * @throws SQLException if execution of the query or mapping was failing
     */
    public <T> List<T> execute(ResultSetMapper<T> resultSetMapper, String sql, Object... arguments) throws SQLException {
        Objects.requireNonNull(sql, "rowMapper");
        Objects.requireNonNull(sql, "sql");
        Objects.requireNonNull(arguments, "arguments");

        Connection connection = connectionPool.getConnection();

        List<T> result;
        try {
            result = execute(connection, resultSetMapper, sql, arguments);
        } finally {
            connectionPool.returnConnection(connection);
        }

        return result;
    }

    private int execute(Connection connection, String sql, Object... arguments) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            setArguments(statement, arguments);
            return statement.execute() ? 0 : statement.getUpdateCount();
        }
    }

    private <T> List<T> execute(Connection connection, ResultSetMapper<T> resultSetMapper, String sql, Object... arguments) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            setArguments(statement, arguments);
            return statement.execute() ? mapResultSet(statement.getResultSet(), resultSetMapper) : Collections.emptyList();
        }
    }

    private void setArguments(PreparedStatement statement, Object[] arguments) throws SQLException {
        for (int index = 0; index < arguments.length; index++) {
            int position = index + 1;
            setArgument(statement, position, arguments[index]);
        }
    }

    private void setArgument(PreparedStatement statement, int position, Object argument) throws SQLException {
        ArgumentHandler argumentHandler = findArgumentHandler(argument);

        if (argumentHandler != null) {
            argumentHandler.handle(statement, position, argument);
            return;
        }

        statement.setObject(position, argument);
    }

    private ArgumentHandler findArgumentHandler(Object argument) {
        for (ArgumentHandler handler : ArgumentHandlers.values()) {
            if (handler.accepts(argument)) {
                return handler;
            }
        }

        return null;
    }

    private <T> List<T> mapResultSet(ResultSet resultSet, ResultSetMapper<T> resultSetMapper) throws SQLException {
        List<T> results = new LinkedList<>();

        if (resultSet != null) {
            while (resultSet.next()) {
                results.add(resultSetMapper.mapResultSet(resultSet));
            }
        }

        return results;
    }

    private interface ArgumentHandler {

        /**
         * Tests whether a argument can used by the current handler
         *
         * @param value to be associated with the prepared statement
         * @return true if the current argument handler can act on the given argument
         */
        boolean accepts(Object value);

        /**
         * Associates an argument with a given sql statement at its specific position
         *
         * @param statement to be carrying the argument
         * @param position  to be used for carrying the argument
         * @param argument  to be used together with the statement
         * @throws SQLException if something went wrong
         */
        void handle(PreparedStatement statement, int position, Object argument) throws SQLException;
    }

    private enum ArgumentHandlers implements ArgumentHandler {
        /**
         * Sets an @{code int} argument into its corresponding position of a statement
         */
        INT {
            @Override
            public boolean accepts(Object value) {
                return value instanceof Integer;
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setInt(position, (int) argument);
            }
        },
        /**
         * Sets an @{code long} argument into its corresponding position of a statement
         */
        LONG {
            @Override
            public boolean accepts(Object value) {
                return value instanceof Long;
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setLong(position, (long) argument);
            }
        },
        /**
         * Sets an @{code double} argument into its corresponding position of a statement
         */
        DOUBLE {
            @Override
            public boolean accepts(Object value) {
                return value instanceof Double;
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setDouble(position, (double) argument);
            }
        },
        /**
         * Sets an @{code float} argument into its corresponding position of a statement
         */
        FLOAT {
            @Override
            public boolean accepts(Object value) {
                return value instanceof Float;
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setFloat(position, (float) argument);
            }
        },
        /**
         * Sets an @{code short} argument into its corresponding position of a statement
         */
        SHORT {
            @Override
            public boolean accepts(Object value) {
                return value instanceof Short;
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setShort(position, (short) argument);
            }
        },
        /**
         * Sets an @{code java.math.BigDecimal} argument into its corresponding position of a statement
         */
        BIG_DECIMAL {
            @Override
            public boolean accepts(Object value) {
                return value instanceof BigDecimal;
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setBigDecimal(position, (BigDecimal) argument);
            }
        },
        /**
         * Sets an @{code java.lang.String} argument into its corresponding position of a statement
         */
        STRING {
            @Override
            public boolean accepts(Object value) {
                return value instanceof String;
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setString(position, (String) argument);
            }
        },
        /**
         * Sets an @{code boolean} argument into its corresponding position of a statement
         */
        BOOLEAN {
            @Override
            public boolean accepts(Object value) {
                return value instanceof Boolean;
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setBoolean(position, (Boolean) argument);
            }
        },
        /**
         * Sets an @{code java.util.Date} argument into its corresponding position of a statement
         */
        DATE {
            @Override
            public boolean accepts(Object value) {
                return value instanceof Date;
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setTimestamp(position, new Timestamp(((Date) argument).getTime()));
            }
        },
        /**
         * Sets an @{code byte} argument into its corresponding position of a statement
         */
        BYTE {
            @Override
            public boolean accepts(Object value) {
                return value instanceof Byte;
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setByte(position, (Byte) argument);
            }
        },
        /**
         * Sets an @{code byte[]} array argument into its corresponding position of a statement
         */
        BYTES {
            @Override
            public boolean accepts(Object value) {
                return value instanceof byte[];
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setBytes(position, (byte[]) argument);
            }
        },
        /**
         * Sets an @{code java.io.InputStream} argument into its corresponding position of a statement
         */
        INPUT_STREAM {
            @Override
            public boolean accepts(Object value) {
                return value instanceof InputStream;
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setBlob(position, (InputStream) argument);
            }
        },
        /**
         * Sets an @{code null} argument into its corresponding position of a statement
         */
        NULL {
            @Override
            public boolean accepts(Object value) {
                return value == null;
            }

            @Override
            public void handle(PreparedStatement statement, int position, Object argument) throws SQLException {
                statement.setNull(position, java.sql.Types.NULL);
            }
        }
    }
}
