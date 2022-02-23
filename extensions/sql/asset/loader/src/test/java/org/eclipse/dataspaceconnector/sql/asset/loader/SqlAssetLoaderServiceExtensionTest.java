/*
 *  Copyright (c) 2022 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial Tests
 *
 */

package org.eclipse.dataspaceconnector.sql.asset.loader;

import org.eclipse.dataspaceconnector.dataloading.AssetEntry;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.junit.jupiter.api.*;

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
    @DisplayName("Accepts and persists AssetEntry")
    void testAcceptEntry() throws SQLException {
        Asset asset = Asset.Builder.newInstance()
                .id("1")
                .name("a1")
                .contentType("t1")
                .build();
        DataAddress dataAddress = DataAddress.Builder.newInstance()
                .type("t1")
                .build();
        AssetEntry assetEntry = new AssetEntry(asset, dataAddress);

        getAssetLoader().accept(assetEntry);

        Long assetCount = executeQuery(getDataSourceRegistry().resolve(DATASOURCE_NAME).getConnection(), (rs) -> rs.getLong(1), "SELECT count(*) FROM edc_assets WHERE asset_id=?", "1").iterator().next();
        Long propertyCount = executeQuery(getDataSourceRegistry().resolve(DATASOURCE_NAME).getConnection(), (rs) -> rs.getLong(1), "SELECT count(*) FROM edc_asset_properties WHERE asset_id=?", "1").iterator().next();
        Long dataAddressCount = executeQuery(getDataSourceRegistry().resolve(DATASOURCE_NAME).getConnection(), (rs) -> rs.getLong(1), "SELECT count(*) FROM edc_asset_dataaddress WHERE asset_id=?", "1").iterator().next();

        Assertions.assertEquals(1, assetCount);
        Assertions.assertEquals(3, propertyCount);
        Assertions.assertEquals(1, dataAddressCount);
    }

    @Test
    @DisplayName("Accepts and persists Asset and DataAddress")
    void testAcceptAssetAndDataAddress() throws SQLException {
        Asset asset = Asset.Builder.newInstance()
                .id("2")
                .name("a2")
                .contentType("t2")
                .build();
        DataAddress dataAddress = DataAddress.Builder.newInstance()
                .type("t2")
                .build();

        getAssetLoader().accept(asset, dataAddress);

        Long assetCount = executeQuery(getDataSourceRegistry().resolve(DATASOURCE_NAME).getConnection(), (rs) -> rs.getLong(1), "SELECT count(*) FROM edc_assets WHERE asset_id=?", "2").iterator().next();
        Long propertyCount = executeQuery(getDataSourceRegistry().resolve(DATASOURCE_NAME).getConnection(), (rs) -> rs.getLong(1), "SELECT count(*) FROM edc_asset_properties WHERE asset_id=?", "2").iterator().next();
        Long dataAddressCount = executeQuery(getDataSourceRegistry().resolve(DATASOURCE_NAME).getConnection(), (rs) -> rs.getLong(1), "SELECT count(*) FROM edc_asset_dataaddress WHERE asset_id=?", "2").iterator().next();

        Assertions.assertEquals(1, assetCount);
        Assertions.assertEquals(3, propertyCount);
        Assertions.assertEquals(1, dataAddressCount);
    }
}