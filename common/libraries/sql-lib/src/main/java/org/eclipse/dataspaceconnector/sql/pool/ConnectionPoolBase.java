package org.eclipse.dataspaceconnector.sql.pool;

import org.eclipse.dataspaceconnector.transaction.spi.TransactionManager;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ConnectionPoolBase implements ConnectionPool {

    private final ThreadLocal<Connection> transactionConnection = ThreadLocal.withInitial(() -> null);
    private TransactionManager transactionManager;

    public ConnectionPoolBase() {
    }

    public ConnectionPoolBase(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;

        registerListener(transactionManager);
    }

    @Override
    public Connection getConnection() {
        if (transactionConnection.get() == null) {
            try {
                return getConnectionInternal();
            } catch (SQLException e) {
                // TODO
                e.printStackTrace();
            }
        }

        return new TransactionalConnection(transactionManager, transactionConnection.get());
    }

    @Override
    public void returnConnection(Connection connection) {
        if (transactionConnection.get() == null) {
            try {
                returnConnectionInternal(connection);
            } catch (SQLException e) {
                // TODO
                e.printStackTrace();
            }
        }
    }

    private void registerListener(TransactionManager transactionManager) {
        transactionManager.onStatusChanged(status -> {
            Connection connection = transactionConnection.get();

            try {
                switch (status) {
                    case NEW:
                        break;
                    case ACTIVE:
                        transactionConnection.set(getConnectionInternal());
                        break;
                    case ROLLBACK:
                        connection.rollback();
                        break;
                    case COMMIT:
                        connection.commit();
                        break;
                    case ROLLBACK_COMPLETE:
                    case COMMIT_COMPLETE:
                        returnConnectionInternal(connection);
                        transactionConnection.set(null);
                        break;
                }
            } catch (SQLException e) {
                try {
                    if(connection!=null) {
                        connection.rollback();
                    }
                } catch (SQLException ignored) {
                }

                throw new RuntimeException(e);
            }
        });
    }

    protected abstract Connection getConnectionInternal() throws SQLException;

    protected abstract void returnConnectionInternal(Connection connection) throws SQLException;
}
