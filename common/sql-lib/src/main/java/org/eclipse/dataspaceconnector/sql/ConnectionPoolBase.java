//package org.eclipse.dataspaceconnector.sql;
//
//import org.eclipse.dataspaceconnector.transaction.spi.TransactionManager;
//
//import java.sql.Connection;
//
//public abstract class ConnectionPoolBase implements ConnectionPool {
//
//    private final ThreadLocal<Connection> transactionConnection = ThreadLocal.withInitial(() -> null);
//    private TransactionManager transactionManager;
//
//    public ConnectionPoolBase() {
//    }
//
//    public ConnectionPoolBase(TransactionManager transactionManager) {
//        this.transactionManager = transactionManager;
//
//        registerListener(transactionManager);
//    }
//
//    @Override
//    public Connection getConnection() {
//        if (transactionConnection.get() == null) {
//            return getConnectionInternal();
//        }
//
//        return new TransactionalConnection(transactionManager, transactionConnection.get());
//    }
//
//    @Override
//    public void returnConnection(Connection connection) {
//        if (transactionConnection.get() == null) {
//            returnConnectionInternal(connection);
//        }
//    }
//
//    private void registerListener(TransactionManager transactionManager) {
//
//        transactionManager.onStatusChanged(status ->
//        {
//            Connection connection = transactionConnection.get();
//
//            switch (status) {
//                case NEW:
//                    transactionConnection.set(getConnectionInternal());
//                    break;
//                case ACTIVE:
//                    break;
//                case ROLLBACK:
//                    connection.rollback();
//                    break;
//                case COMMIT:
//                    connection.commit();
//                    break;
//                case ROLLBACK_COMPLETE:
//                case COMMIT_COMPLETE:
//                    returnConnectionInternal(connection);
//                    transactionConnection.set(null);
//                    break;
//            }
//        });
//    }
//
//    protected abstract Connection getConnectionInternal();
//
//    protected abstract void returnConnectionInternal(Connection connection);
//}
