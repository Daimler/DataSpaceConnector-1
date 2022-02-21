package org.eclipse.dataspaceconnector.sql.asset.index;

import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.asset.DataAddressResolver;
import org.eclipse.dataspaceconnector.spi.query.QuerySpec;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;
import javax.sql.DataSource;

public class SqlAssetIndex implements AssetIndex, DataAddressResolver {

    private final DataSource dataSource;

    public SqlAssetIndex(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Override
    public Stream<Asset> queryAssets(AssetSelectorExpression expression) {
        return null;
    }

    @Override
    public Stream<Asset> queryAssets(QuerySpec querySpec) {
        return null;
    }

    @Override
    public @Nullable Asset findById(String assetId) {
        return null;
    }

    @Override
    public DataAddress resolveForAsset(String assetId) {
        return null;
    }
}
