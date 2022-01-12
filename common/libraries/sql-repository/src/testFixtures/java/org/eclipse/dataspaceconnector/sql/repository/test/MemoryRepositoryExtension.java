package org.eclipse.dataspaceconnector.sql.repository.test;

import org.eclipse.dataspaceconnector.sql.repository.Repository;
import org.eclipse.dataspaceconnector.sql.repository.RepositoryImpl;
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

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.execute;


public class MemoryRepositoryExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private Repository repository;
    private Connection connection;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Class.forName("org.h2.Driver");
        this.connection = DriverManager.getConnection("jdbc:h2:mem:test", "", "");
        this.repository = new RepositoryImpl(connection);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        this.connection.close();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        execute(connection, TestPreparedStatementResourceReader.getTablesCreate());
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        execute(connection, TestPreparedStatementResourceReader.getTablesDelete());
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();
        return parameterType == Connection.class || parameterType == Repository.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();

        if (parameterType == Connection.class) {
            return connection;
        } else if (parameterType == Repository.class) {
            return repository;
        }
        throw new UnsupportedOperationException("Cannot resolve parameter of type " + parameterType.getName());
    }

}
