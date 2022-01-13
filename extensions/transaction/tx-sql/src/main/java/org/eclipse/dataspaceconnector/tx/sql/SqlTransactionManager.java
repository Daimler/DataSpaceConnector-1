package org.eclipse.dataspaceconnector.tx.sql;

import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.transaction.tx.TransactionManager;

import java.sql.SQLException;

public class SqlTransactionManager implements TransactionManager {

    private final ThreadLocal<SqlTransactionContext> threadLocal = ThreadLocal.withInitial(this::createSqlTransactionContext);
    private final ConnectionPool connectionPool;

    public SqlTransactionManager(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public SqlTransactionContext beginTransaction() {
        return threadLocal.get();
    }

    private SqlTransactionContext createSqlTransactionContext() {
        try {
            return new SqlTransactionContext(connectionPool.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
