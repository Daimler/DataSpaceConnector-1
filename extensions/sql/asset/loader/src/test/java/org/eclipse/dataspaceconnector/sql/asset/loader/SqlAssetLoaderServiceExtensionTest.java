package org.eclipse.dataspaceconnector.sql.asset.loader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

class SqlAssetLoaderServiceExtensionTest extends AbstractSqlAssetLoaderServiceExtensionTest {

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
        executeQuery(getDataSourceRegistry().resolve(DATASOURCE_NAME).getConnection(), "SELECT 1 FROM edc_assets");
        executeQuery(getDataSourceRegistry().resolve(DATASOURCE_NAME).getConnection(), "SELECT 1 FROM edc_asset_properties");
        executeQuery(getDataSourceRegistry().resolve(DATASOURCE_NAME).getConnection(), "SELECT 1 FROM edc_asset_dataaddress");
    }

    @Test
    void testAcceptEntry() {
        //getSqlAssetLoader().accept();
// TODO: implement
    }

    @Test
    void testAcceptAssetAndDataAddress() {
        //getSqlAssetLoader().accept();
        // TODO: implement
    }
}