package org.eclipse.dataspaceconnector.s4.fakes;

import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.postgresql.ds.PGSimpleDataSource;

@Requires({ DataSourceRegistry.class })
@Provides(Asset.class)
public class DatasourceServiceExtension implements ServiceExtension {

    @Override
    public void initialize(ServiceExtensionContext context) {
        DataSourceRegistry registry = context.getService(DataSourceRegistry.class);
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setURL("jdbc:postgresql://localhost:5432/postgres");
        source.setUser("postgres");
        source.setPassword("password");
        registry.register(DataSourceRegistry.DEFAULT_DATASOURCE, source);
    }
}
