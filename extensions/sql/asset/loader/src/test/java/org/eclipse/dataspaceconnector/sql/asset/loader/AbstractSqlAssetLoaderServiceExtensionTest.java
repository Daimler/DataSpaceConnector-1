package org.eclipse.dataspaceconnector.sql.asset.loader;

import org.eclipse.dataspaceconnector.junit.launcher.EdcExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(EdcExtension.class)
class AbstractSqlAssetLoaderServiceExtensionTest {

    private final AtomicReference<ServiceExtensionContext> contextRef = new AtomicReference<>();

    @BeforeEach
    void before(EdcExtension extension) {
        extension.registerSystemExtension(ServiceExtension.class, new ServiceExtension() {
            public void initialize(ServiceExtensionContext context) {
                contextRef.set(context);
            }
        });
    }

    @AfterEach
    void afterTestExecution() {
        contextRef.set(null);
    }

    protected DataSourceRegistry getDataSourceRegistry() {
        return contextRef.get().getService(DataSourceRegistry.class);
    }

    protected TransactionContext getTransactionContext() {
        return contextRef.get().getService(TransactionContext.class);
    }

    protected SqlAssetLoader getSqlAssetLoader() {
        return contextRef.get().getService(SqlAssetLoader.class);
    }
}