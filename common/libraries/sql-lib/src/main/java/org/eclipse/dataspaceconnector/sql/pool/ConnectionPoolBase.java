package org.eclipse.dataspaceconnector.sql.pool;

import org.eclipse.dataspaceconnector.transaction.spi.TransactionContext;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ConnectionPoolBase implements ConnectionPool {

    private Map<TransactionContext, Connection> transactionConnections;
    private TransactionManager transactionManager;

    public ConnectionPoolBase() {
    }

    public ConnectionPoolBase(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.transactionConnections = new ConcurrentHashMap<>();
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (transactionManager == null) {
            return getConnectionInternal();
        }

        TransactionContext context = transactionManager.beginTransaction();
        if (transactionConnections.containsKey(context)) {
            return new TransactionalConnection(context, transactionConnections.get(context));
        }

        Connection connection = getConnectionInternal();
        transactionConnections.put(context, connection);

        registerListener(context, connection);

        return new TransactionalConnection(context, connection);
    }

    @Override
    public void returnConnection(Connection connection) throws SQLException {
        if (transactionManager == null) {
            returnConnectionInternal(connection);
        }
    }

    private void registerListener(TransactionContext context, Connection connection) {
        context.onCommit(() -> {
            try {
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        context.onAfterCommit(() -> {
            try {
                transactionalReturn(context, connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        context.onRollback(() -> {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        context.onAfterRollback(() -> {
            try {
                transactionalReturn(context, connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void transactionalReturn(TransactionContext context, Connection connection) throws SQLException {
        try {
            connection.rollback();
        } finally {
            transactionConnections.remove(context);
            returnConnectionInternal(connection);
        }
    }

    protected abstract Connection getConnectionInternal() throws SQLException;

    protected abstract void returnConnectionInternal(Connection connection) throws SQLException;
}
