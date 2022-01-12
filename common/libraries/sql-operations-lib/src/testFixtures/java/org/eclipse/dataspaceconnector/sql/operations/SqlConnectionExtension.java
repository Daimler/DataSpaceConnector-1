package org.eclipse.dataspaceconnector.sql.operations;

import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.sql.pool.commons.CommonsConnectionPool;
import org.eclipse.dataspaceconnector.sql.pool.commons.CommonsConnectionPoolConfig;
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
    private Connection connection;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Class.forName("org.h2.Driver");

        CommonsConnectionPoolConfig config = CommonsConnectionPoolConfig.Builder.newInstance()
                .maxTotalConnections(MAX_TOTAL_CONNECTIONS)
                .build();

        this.connectionPool = new CommonsConnectionPool(() -> DriverManager.getConnection("jdbc:h2:mem:test", "", ""), config);
        this.connection = connectionPool.getConnection();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        connectionPool.returnConnection(connection);
        connectionPool.close();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        executeQuery(connection, TestPreparedStatementResourceReader.getTablesCreate());
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        executeQuery(connection, TestPreparedStatementResourceReader.getTablesDelete());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();
        return parameterType == Connection.class || parameterType == ConnectionPool.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();

        if (parameterType == Connection.class) {
            return connection;
        } else if (parameterType == ConnectionPool.class) {
            return connectionPool;
        }

        throw new UnsupportedOperationException("Cannot resolve parameter of type " + parameterType.getName());
    }

}
