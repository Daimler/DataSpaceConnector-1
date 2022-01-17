package org.eclipse.dataspaceconnector.sql.operations;

import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.sql.pool.commons.CommonsConnectionPool;
import org.eclipse.dataspaceconnector.sql.pool.commons.CommonsConnectionPoolConfig;
import org.eclipse.dataspaceconnector.transaction.core.TransactionManagerImpl;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionManager;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

public class SqlConnectionExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final int MAX_TOTAL_CONNECTIONS = 5;

    private ConnectionPool connectionPool;
    private TransactionManager transactionManager;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
//        Class.forName("org.h2.Driver");

        CommonsConnectionPoolConfig config = CommonsConnectionPoolConfig.Builder.newInstance()
                .maxTotalConnections(MAX_TOTAL_CONNECTIONS)
                .build();

        this.transactionManager = new TransactionManagerImpl();
//        this.connectionPool = new CommonsConnectionPool(transactionManager, () -> DriverManager.getConnection("jdbc:h2:mem:test", "", ""), config);
        this.connectionPool = new CommonsConnectionPool(transactionManager, () -> {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
            connection.setAutoCommit(false);
            return connection;
        }, config);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
//        connectionPool.returnConnection(connection);
        connectionPool.close();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Connection connection = connectionPool.getConnection();
        executeQuery(connection, TestPreparedStatementResourceReader.getTablesCreate());
        connection.commit();
        connectionPool.returnConnection(connection);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Connection connection = connectionPool.getConnection();
        executeQuery(connection, TestPreparedStatementResourceReader.getTablesDelete());
        connection.commit();
        connectionPool.returnConnection(connection);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();
        return parameterType == ConnectionPool.class || parameterType == TransactionManager.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();
        if (parameterType == ConnectionPool.class) {
            return connectionPool;
        } else if (parameterType == TransactionManager.class) {
            return transactionManager;
        }

        throw new UnsupportedOperationException("Cannot resolve parameter of type " + parameterType.getName());
    }

}
