package org.eclipse.dataspaceconnector.sql.asset.index;

import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.DataAddressResolver;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;

import javax.sql.DataSource;

public class SqlAssetIndexServiceExtension implements ServiceExtension {

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Override
    public void initialize(ServiceExtensionContext context) {
        DataSource dataSource = dataSourceRegistry.resolve("asset"); // TODO configuration

        SqlAssetIndex sqlAssetIndex = new SqlAssetIndex(dataSource);

        context.registerService(AssetIndex.class, sqlAssetIndex);
        context.registerService(DataAddressResolver.class, sqlAssetIndex);
    }
}
