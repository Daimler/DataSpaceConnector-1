package org.eclipse.dataspaceconnector.sql.operations.query;

import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.sql.operations.query.operations.QueryOperation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Query<T> {

    private final QueryOperation<T> queryOperation;
    private final ConnectionPool connectionPool;

    public Query(ConnectionPool connectionPool, QueryOperation<T> queryOperation) {
        this.connectionPool = connectionPool;
        this.queryOperation = queryOperation;
    }

    public List<T> execute() throws SQLException {
        Connection connection = connectionPool.getConnection();
        try {
            return queryOperation.invoke(connection);
        } finally {
            connectionPool.returnConnection(connection);
        }
    }
}
