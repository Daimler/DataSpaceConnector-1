package org.eclipse.dataspaceconnector.sql.asset.schema;

import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;

import javax.sql.DataSource;

public class SqlAssetSchemaServiceExtension  implements ServiceExtension {

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Override
    public void initialize(ServiceExtensionContext context) {
        DataSource dataSource = dataSourceRegistry.resolve("asset"); // TODO configuration

        // TODO flyway
    }
}
