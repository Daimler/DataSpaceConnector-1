package org.eclipse.dataspaceconnector.ids.transform;

import org.easymock.EasyMock;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AssetToArtifactTransformerTest {
    private static final String ASSET_ID = "test_id";
    private static final URI ASSET_ID_URI = URI.create("urn:asset:1");
    private static final String ASSET_FILENAME = "test_filename";
    private static final BigInteger ASSET_BYTESIZE = BigInteger.valueOf(5);

    // subject
    private AssetToArtifactTransformer assetToArtifactTransformer;

    // mocks
    private Asset asset;
    private TransformerContext context;

    @BeforeEach
    void setUp() {
        assetToArtifactTransformer = new AssetToArtifactTransformer();
        asset = EasyMock.createMock(Asset.class);
        context = EasyMock.createMock(TransformerContext.class);
    }

    @Test
    void testThrowsNullPointerExceptionForAll() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            assetToArtifactTransformer.transform(null, null);
        });
    }

    @Test
    void testThrowsNullPointerExceptionForContext() {
        EasyMock.replay(asset);

        Assertions.assertThrows(NullPointerException.class, () -> {
            assetToArtifactTransformer.transform(asset, null);
        });
    }

    @Test
    void testReturnsNull() {
        var result = assetToArtifactTransformer.transform(null, context);

        Assertions.assertNull(result);
    }

    @Test
    void testSuccessfulSimple() {
        // prepare
        EasyMock.expect(asset.getId()).andReturn(ASSET_ID);
        EasyMock.expect(asset.getProperties()).andReturn(Collections.emptyMap());

        EasyMock.expect(context.transform(EasyMock.anyObject(String.class),EasyMock.eq(URI.class))).andReturn(ASSET_ID_URI);

        // record
        EasyMock.replay(asset, context);

        // invoke
        var result = assetToArtifactTransformer.transform(asset, context);

        // verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(ASSET_ID_URI, result.getId());
    }

    @Test
    void testSuccessfulMap() {
        // prepare
        EasyMock.expect(asset.getId()).andReturn(ASSET_ID);
        Map<String, Object> properties = new HashMap<>(){{
            put(AssetToArtifactTransformer.KEY_ASSET_FILE_NAME, ASSET_FILENAME);
            put(AssetToArtifactTransformer.KEY_ASSET_BYTE_SIZE, ASSET_BYTESIZE);
        }};
        EasyMock.expect(asset.getProperties()).andReturn(properties);

        EasyMock.expect(context.transform(EasyMock.anyObject(String.class),EasyMock.eq(URI.class))).andReturn(ASSET_ID_URI);

        // record
        EasyMock.replay(asset, context);

        // invoke
        var result = assetToArtifactTransformer.transform(asset, context);

        // verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(ASSET_ID_URI, result.getId());
        Assertions.assertEquals(ASSET_FILENAME, result.getFileName());
        Assertions.assertEquals(ASSET_BYTESIZE, result.getByteSize());
    }


    @AfterEach
    void tearDown() {
        EasyMock.verify(asset, context);
    }

}