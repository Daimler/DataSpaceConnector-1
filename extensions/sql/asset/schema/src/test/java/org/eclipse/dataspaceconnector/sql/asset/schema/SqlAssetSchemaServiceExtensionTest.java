/*
 *  Copyright (c) 2021 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial Test
 *
 */

package org.eclipse.dataspaceconnector.sql.asset.schema;

import org.eclipse.dataspaceconnector.junit.launcher.EdcExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

@ExtendWith(EdcExtension.class)
class SqlAssetSchemaServiceExtensionTest extends AbstractSqlAssetSchemaServiceExtensionTest {
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
}