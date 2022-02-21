package org.eclipse.dataspaceconnector.sql.asset.loader;

import org.eclipse.dataspaceconnector.dataloading.AssetLoader;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;

import javax.sql.DataSource;

public class SqlAssetLoaderServiceExtension implements ServiceExtension {

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Override
    public void initialize(ServiceExtensionContext context) {
        DataSource dataSource = dataSourceRegistry.resolve("asset"); // TODO configuration

        SqlAssetLoader sqlAssetLoader = new SqlAssetLoader(dataSource);

        context.registerService(AssetLoader.class, sqlAssetLoader);
    }
}
