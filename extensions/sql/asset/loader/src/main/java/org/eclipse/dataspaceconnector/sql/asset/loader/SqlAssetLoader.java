package org.eclipse.dataspaceconnector.sql.asset.loader;

import org.eclipse.dataspaceconnector.dataloading.AssetEntry;
import org.eclipse.dataspaceconnector.dataloading.AssetLoader;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;

import java.util.Objects;
import javax.sql.DataSource;

public class SqlAssetLoader implements AssetLoader {

    private final DataSource dataSource;

    public SqlAssetLoader(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Override
    public void accept(AssetEntry item) {
        accept(item.getAsset(), item.getDataAddress());
    }

    @Override
    public void accept(Asset asset, DataAddress dataAddress) {

    }
}
