package org.eclipse.dataspaceconnector.tx.sql;

import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class TxTest {

    private SqlTransactionManager transactionManager = null;
    private ConnectionPool connectionPool = null;

    @Test
    public void controller() throws SQLException {

        SqlTransactionContext context = transactionManager.beginTransaction();

        subRoutine();

        SqlTransactionContext contextB = transactionManager.beginTransaction();

        subRoutine();

        contextB.commit(); // do nothing


        context.commit();
    }

    @Test
    public void subRoutine() throws SQLException {
        Connection connection = connectionPool.getConnection();
        connection.createStatement().execute("SELECT 1");
        connectionPool.returnConnection(connection);
    }


}
