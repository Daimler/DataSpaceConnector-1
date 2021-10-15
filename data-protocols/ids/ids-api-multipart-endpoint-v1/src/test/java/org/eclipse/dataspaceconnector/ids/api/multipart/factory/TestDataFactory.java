package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import org.eclipse.dataspaceconnector.ids.spi.types.domain.IdsAsset;

public class TestDataFactory {
    public static final String IDS_ASSET_ID = "fixedassetid";
    public static final String IDS_ASSET_NAME = "assetName";
    public static final String IDS_ASSET_VERSION = "v1";
    public static final String IDS_ASSET_FILE_NAME = "file-name";
    public static final int IDS_ASSET_BYTE_SIZE = 1024;
    public static final String IDS_ASSET_FILE_EXTENSION = "bin";

    public static IdsAsset createIdsAsset() {
        IdsAsset.Builder builder = IdsAsset.Builder.newInstance()
                .fileName(IDS_ASSET_FILE_NAME)
                .byteSize(IDS_ASSET_BYTE_SIZE)
                .fileExtension(IDS_ASSET_FILE_EXTENSION);

        builder.id(IDS_ASSET_ID);
        builder.name(IDS_ASSET_NAME);
        builder.version(IDS_ASSET_VERSION);

        return builder.build();
    }
}
