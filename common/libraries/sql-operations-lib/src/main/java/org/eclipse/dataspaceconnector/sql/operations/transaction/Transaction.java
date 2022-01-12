package org.eclipse.dataspaceconnector.sql.operations.transaction;

import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.sql.operations.transaction.operations.TransactionOperation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Transaction {
    private final ConnectionPool connectionPool;

    private final List<TransactionOperation> transactionOperations;

    public Transaction(ConnectionPool connectionPool, List<TransactionOperation> transactionOperations) {
        this.connectionPool = connectionPool;
        this.transactionOperations = transactionOperations;
    }

    public void execute() throws SQLException {
        Connection connection = connectionPool.getConnection();
        try {
            connection.createStatement().execute("BEGIN");

            for (TransactionOperation operation : transactionOperations) {
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
