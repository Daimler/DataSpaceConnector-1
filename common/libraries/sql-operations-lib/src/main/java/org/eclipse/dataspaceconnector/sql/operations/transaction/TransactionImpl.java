package org.eclipse.dataspaceconnector.sql.operations.transaction;

import org.eclipse.dataspaceconnector.sql.operations.Transaction;
import org.eclipse.dataspaceconnector.sql.operations.transaction.operations.TransactionOperation;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TransactionImpl implements Transaction {
    private final ConnectionPool connectionPool;

    private final List<TransactionOperation> transactionOperations;

    public TransactionImpl(ConnectionPool connectionPool, List<TransactionOperation> transactionOperations) {
        this.connectionPool = connectionPool;
        this.transactionOperations = transactionOperations;
    }

    @Override
    public void execute() throws SQLException {
        Connection connection = connectionPool.getConnection();
        try {
//            connection.createStatement().execute("BEGIN");

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
