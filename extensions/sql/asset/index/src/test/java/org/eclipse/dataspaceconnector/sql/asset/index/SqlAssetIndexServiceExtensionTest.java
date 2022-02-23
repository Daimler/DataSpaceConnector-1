package org.eclipse.dataspaceconnector.sql.asset.index;

import org.eclipse.dataspaceconnector.dataloading.AssetEntry;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

class SqlAssetIndexServiceExtensionTest extends AbstractSqlAssetIndexServiceExtensionTest {

    private static final String DATASOURCE_NAME = "asset";

    private final Map<String, String> systemProperties = new HashMap<>() {
        {
            put(String.format("edc.datasource.%s.url", DATASOURCE_NAME), "jdbc:h2:mem:test");
            put(String.format("edc.datasource.%s.driverClassName", DATASOURCE_NAME), org.h2.Driver.class.getName());
            put("edc.asset.datasource.name", DATASOURCE_NAME);
        }
    };

    @BeforeEach
    void setUp() {
        systemProperties.forEach(System::setProperty);
    }

    @AfterEach
    void tearDown() {
        systemProperties.keySet().forEach(System::clearProperty);
    }

    @Test
    @DisplayName("Context Loads, tables exist")
    void contextLoads() throws SQLException {
        String clz = Queries.ASSET_SELECT_ALL;
        executeQuery(getDataSourceRegistry().resolve(DATASOURCE_NAME).getConnection(), "SELECT 1 FROM edc_assets");
        executeQuery(getDataSourceRegistry().resolve(DATASOURCE_NAME).getConnection(), "SELECT 1 FROM edc_asset_properties");
        executeQuery(getDataSourceRegistry().resolve(DATASOURCE_NAME).getConnection(), "SELECT 1 FROM edc_asset_dataaddress");
    }

    @Test
    void testQueryAssetsAssetSelectorExpression() {
        // TODO: implement
        //getSqlAssetIndex().queryAssets()
    }

    @Test
    void testQueryAssetsQuerySpec() {
        // TODO: implement
        //getSqlAssetIndex().queryAssets()
    }

    @Test
    @DisplayName("Finds asset by given id")
    void testFindById() {
        AssetEntry assetEntry = createAssetEntry();

        getAssetLoader().accept(assetEntry);

        Asset asset = getAssetIndex().findById(assetEntry.getAsset().getId());

        Assertions.assertNotNull(asset);

        Assertions.assertEquals(assetEntry.getAsset().getProperties(), asset.getProperties());
    }

    @Test
    @DisplayName("Finds data address by given asset id")
    void resolveForAsset() {
        AssetEntry assetEntry = createAssetEntry();

        getAssetLoader().accept(assetEntry);

        DataAddress dataAddress = getDataAddressResolver().resolveForAsset(assetEntry.getAsset().getId());

        Assertions.assertNotNull(dataAddress);

        Assertions.assertEquals(assetEntry.getDataAddress().getProperties(), dataAddress.getProperties());
    }

    private AssetEntry createAssetEntry() {
        Asset asset = Asset.Builder.newInstance()
                .id("1")
                .name("a1")
                .contentType("t1")
                .build();
        DataAddress dataAddress = DataAddress.Builder.newInstance()
                .type("t1")
                .build();

        return new AssetEntry(asset, dataAddress);
    }
}