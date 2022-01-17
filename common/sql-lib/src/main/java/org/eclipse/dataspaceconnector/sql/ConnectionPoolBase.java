package org.eclipse.dataspaceconnector.sql;

import org.eclipse.dataspaceconnector.transaction.spi.TransactionContext;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ConnectionPoolBase implements ConnectionPool {

    private boolean isTransactional;
    private Map<TransactionContext, Connection> transactionConnections;
    private TransactionManager transactionManager;

    public ConnectionPoolBase() {
    }

    public ConnectionPoolBase(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.transactionConnections = new ConcurrentHashMap<>();
    }

    @Override
    public Connection getConnection() {
        if (transactionManager == null) {
            return getConnectionInternal();
        }

        TransactionContext context = transactionManager.beginTransaction();
        if (transactionConnections.containsKey(context)) {
            return transactionConnections.get(context);
        }

        Connection connection = getConnectionInternal();
        transactionConnections.put(context, connection);

        registerListener(context, connection);

        return new TransactionalConnection(context, connection);

    }

    @Override
    public void returnConnection(Connection connection) {
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
        context.onAfterCommit(() -> transactionalReturn(context, connection));
        context.onRollback(() -> {
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        context.onAfterRollback(() -> transactionalReturn(context, connection));
    }

    private void transactionalReturn(TransactionContext context, Connection connection) {
        returnConnectionInternal(connection);
        transactionConnections.remove(context);
    }

    protected abstract Connection getConnectionInternal();

    protected abstract void returnConnectionInternal(Connection connection);
}
