package org.eclipse.dataspaceconnector.sql.repository.builder;

import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.sql.repository.types.Operation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Transaction {
    private final ConnectionPool connectionPool;

    private final List<Operation> transactionOperations;

    public Transaction(ConnectionPool connectionPool, List<Operation> transactionOperations) {
        this.connectionPool = connectionPool;
        this.transactionOperations = transactionOperations;
    }

    public void execute() throws SQLException {
        Connection connection = connectionPool.getConnection();
        try {
            connection.createStatement().executeQuery("BEGIN");

            for (Operation operation : transactionOperations) {
                operation.execute(connection);
            }

            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connectionPool.returnConnection(connection);
        }
    }
}
