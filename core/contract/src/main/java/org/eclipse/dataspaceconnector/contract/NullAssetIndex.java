package org.eclipse.dataspaceconnector.contract;

import java.util.stream.Stream;

import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;

/**
 * NullObject of the {@link AssetIndex}
 */
public class NullAssetIndex implements AssetIndex {

    @Override
    public Stream<Asset> queryAssets(AssetSelectorExpression expression) {
        return Stream.empty();
    }
}
