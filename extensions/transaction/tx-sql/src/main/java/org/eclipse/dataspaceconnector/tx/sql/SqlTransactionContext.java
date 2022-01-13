package org.eclipse.dataspaceconnector.tx.sql;

import org.eclipse.dataspaceconnector.transaction.tx.TransactionContext;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlTransactionContext implements TransactionContext {

    private final Connection connection;
    private boolean ongoing;

    public SqlTransactionContext(Connection connection) {
        this.connection = connection;
        this.ongoing = true;
    }

    @Override
    public void commit() {
        ongoing = false;
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void rollback() {
        ongoing = false;
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    // package private
    Connection getConnection() {
        return connection;
    }

    // package private
    boolean isOngoing() {
        return ongoing;
    }
}
