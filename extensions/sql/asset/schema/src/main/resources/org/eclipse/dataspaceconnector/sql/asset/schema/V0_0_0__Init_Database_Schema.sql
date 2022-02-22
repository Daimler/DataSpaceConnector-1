--
--  Copyright (c) 2021 Daimler TSS GmbH
--
--  This program and the accompanying materials are made available under the
--  terms of the Apache License, Version 2.0 which is available at
--  https://www.apache.org/licenses/LICENSE-2.0
--
--  SPDX-License-Identifier: Apache-2.0
--
--  Contributors:
--       Daimler TSS GmbH - Initial SQL Query
--

-- table: edc_assets
CREATE TABLE IF NOT EXISTS edc_assets
(
    -- since utf8 uses up to 4 bytes per character and
    -- index length should not exceed 1024 bytes on postgres
    -- resp 900 bytes on ms sql server
    -- resp 3072 bytes on mysql/mariadb
    -- resp 1048576 bytes on h2 etc
    -- common denominator is 900 bytes allowed by ms sql server
    asset_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (asset_id)
);

-- table: edc_asset_properties
CREATE TABLE IF NOT EXISTS edc_asset_properties
(
    asset_id VARCHAR(255) NOT NULL,
    k  VARCHAR(255) NOT NULL,
    v  VARCHAR(65535), -- CLOB would be more reasonable due to ambiguous size of the properties entry
    PRIMARY KEY (asset_id, k),
    FOREIGN KEY (asset_id) REFERENCES edc_assets (asset_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS index_edc_asset_properties_k_v ON edc_asset_properties(k, v); -- TODO: wont work due to v := VARCHAR(65535)

-- table: edc_asset_dataaddress
CREATE TABLE IF NOT EXISTS edc_asset_dataaddress (
    asset_id VARCHAR(255) NOT NULL,
    properties VARCHAR(65535) NOT NULL, -- CLOB would be more reasonable due to ambiguous size of the properties entry
    PRIMARY KEY (asset_id),
    FOREIGN KEY (asset_id) REFERENCES edc_assets (asset_id) ON DELETE CASCADE
);