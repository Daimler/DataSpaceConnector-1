package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import org.eclipse.dataspaceconnector.ids.transform.AssetToArtifactTransformer;
import org.eclipse.dataspaceconnector.ids.transform.AssetToRepresentationTransformer;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;

import java.math.BigInteger;

public class TestDataFactory {
    public static final String IDS_ASSET_ID = "urn:asset:1";
    public static final String IDS_ASSET_NAME = "assetName";
    public static final String IDS_ASSET_VERSION = "v1";
    public static final String IDS_ASSET_FILE_NAME = "file-name";
    public static final BigInteger IDS_ASSET_BYTE_SIZE = BigInteger.valueOf(1024);
    public static final String IDS_ASSET_FILE_EXTENSION = "bin";

    public static Asset createAsset() {
        Asset.Builder builder = Asset.Builder.newInstance()
                .id(IDS_ASSET_ID)
                .name(IDS_ASSET_NAME)
                .version(IDS_ASSET_VERSION)
                .property(AssetToRepresentationTransformer.KEY_ASSET_FILE_EXTENSION, IDS_ASSET_FILE_EXTENSION)
                .property(AssetToArtifactTransformer.KEY_ASSET_BYTE_SIZE, IDS_ASSET_BYTE_SIZE)
                .property(AssetToArtifactTransformer.KEY_ASSET_FILE_NAME, IDS_ASSET_FILE_NAME);

        return builder.build();
    }
}
