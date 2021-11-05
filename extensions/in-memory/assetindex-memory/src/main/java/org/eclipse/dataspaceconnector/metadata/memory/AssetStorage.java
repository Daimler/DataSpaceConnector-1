package org.eclipse.dataspaceconnector.metadata.memory;

import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public interface AssetStorage {
    @Nullable
    Asset getAsset(@NotNull String assetId);

    @NotNull
    Iterator<Asset> getAssets();

    /**
     * Returns Assets after a given Asset ID (excluded). The first item if the stream is the Asset nearest to the given Asset ID.
     *
     * @param assetId, excluded
     * @return range of assets
     */
    @NotNull
    Iterator<Asset> getAssetsAscending(@NotNull String assetId);
}
