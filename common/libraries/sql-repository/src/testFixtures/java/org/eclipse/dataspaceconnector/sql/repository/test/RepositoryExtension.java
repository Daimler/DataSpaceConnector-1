package org.eclipse.dataspaceconnector.sql.repository.test;

import org.eclipse.dataspaceconnector.sql.SqlClient;
import org.eclipse.dataspaceconnector.sql.connection.ConnectionFactory;
import org.eclipse.dataspaceconnector.sql.connection.pool.commons.CommonsConnectionPool;
import org.eclipse.dataspaceconnector.sql.connection.pool.commons.CommonsConnectionPoolConfig;
import org.eclipse.dataspaceconnector.sql.repository.Repository;
import org.eclipse.dataspaceconnector.sql.repository.RepositoryImpl;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class RepositoryExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private Repository repository;
    private SqlClient sqlClient;

    @Override
    public void beforeAll(ExtensionContext context) {
        ConnectionFactory connectionFactory = new ConnectionFactoryImpl();
        CommonsConnectionPoolConfig commonsConnectionPoolConfig = CommonsConnectionPoolConfig.Builder.newInstance().build();
        CommonsConnectionPool connectionPool = new CommonsConnectionPool(connectionFactory, commonsConnectionPoolConfig);

        this.sqlClient = new SqlClient(connectionPool);
        this.repository = new RepositoryImpl(sqlClient);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        sqlClient.execute(TestPreparedStatementResourceReader.getTablesCreate());
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        sqlClient.execute(TestPreparedStatementResourceReader.getTablesDelete());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();
        return parameterType == SqlClient.class || parameterType == Repository.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();

        if (parameterType == SqlClient.class) {
            return sqlClient;
        } else if (parameterType == Repository.class) {
            return repository;
        }
        throw new UnsupportedOperationException("Cannot resolve parameter of type " + parameterType.getName());
    }
}
