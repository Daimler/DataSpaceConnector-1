package org.eclipse.dataspaceconnector.sql.operations.query;

import org.eclipse.dataspaceconnector.sql.operations.Query;
import org.eclipse.dataspaceconnector.sql.operations.query.operations.QueryOperation;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unchecked")
public class QueryImplTest {

    private Query<String> query;

    // mocks
    QueryOperation<String> queryOperation;
    ConnectionPool connectionPool;
    Connection connection;

    @BeforeEach
    public void setup() throws SQLException {
        queryOperation = Mockito.mock(QueryOperation.class);
        connectionPool = Mockito.mock(ConnectionPool.class);
        connection = Mockito.mock(Connection.class);

        Mockito.when(connectionPool.getConnection())
                .thenReturn(connection);


        query = new QueryImpl<>(connectionPool, queryOperation);
    }

    @Test
    public void testQueryExecuted() throws SQLException {
        String expected = "foo bar";

        Mockito.when(queryOperation.invoke(connection))
                .thenReturn(Collections.singletonList(expected));

        List<String> results = query.execute();

        assertThat(results).contains(expected);
    }

    @Test
    public void testConnectionReturnedOnSuccess() throws SQLException {

        Mockito.when(queryOperation.invoke(connection))
                .thenReturn(Collections.emptyList());

        query.execute();

        Mockito.verify(connectionPool).returnConnection(connection);
    }

    @Test
    public void testConnectionReturnedOnException() throws SQLException {
        Mockito.when(queryOperation.invoke(connection))
                .thenThrow(SQLException.class);

        try {
            query.execute();
        } catch (SQLException ignore) {
        }

        Mockito.verify(connectionPool).returnConnection(connection);
    }
}
