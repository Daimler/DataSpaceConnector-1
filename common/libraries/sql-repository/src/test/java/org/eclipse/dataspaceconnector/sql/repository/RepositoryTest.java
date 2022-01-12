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
 *       Daimler TSS GmbH - Initial API and Implementation
 *
 */

package org.eclipse.dataspaceconnector.sql.repository;

import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.SqlClient;
import org.eclipse.dataspaceconnector.sql.repository.mapper.ExistsMapper;
import org.eclipse.dataspaceconnector.sql.repository.mapper.PropertyMapper;
import org.eclipse.dataspaceconnector.sql.repository.test.RepositoryExtension;
import org.eclipse.dataspaceconnector.sql.repository.types.Property;
import org.eclipse.dataspaceconnector.sql.repository.util.PreparedStatementResourceReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(RepositoryExtension.class)
class RepositoryTest {

    private Repository repository;
    private SqlClient sqlClient;

    @BeforeEach
    public void setup(Repository repository, SqlClient sqlClient) {
        this.repository = repository;
        this.sqlClient = sqlClient;
    }

    @Test
    public void testAssetAndPropertyCreation() throws SQLException {
        Asset asset = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contentType("pdf")
                .version("1.0.0")
                .build();

        repository.create(asset);

        List<Property> properties = sqlClient.execute(new PropertyMapper(),
                PreparedStatementResourceReader.readPropertiesSelectByAssetId(), asset.getId());
        List<Boolean> exists = sqlClient.execute(new ExistsMapper(),
                PreparedStatementResourceReader.readAssetExists(), asset.getId());

        Assertions.assertTrue(exists.size() == 1 && exists.get(0));
        Assertions.assertEquals(3, properties.size());
        assertThat(properties)
                .contains(new Property(Asset.PROPERTY_ID, asset.getId()))
                .contains(new Property(Asset.PROPERTY_CONTENT_TYPE, asset.getContentType()))
                .contains(new Property(Asset.PROPERTY_VERSION, asset.getVersion()));
    }

    @Test
    public void testSelectAllQuery() throws SQLException {
        Asset asset1 = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .build();

        Asset asset2 = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .build();

        repository.create(asset1);
        repository.create(asset2);

        Criterion selectAll = new Criterion("*", "=", "*");
        List<Asset> assets = repository.query(Collections.singletonList(selectAll));

        org.assertj.core.api.Assertions.assertThat(assets.stream().map(Asset::getId).collect(Collectors.toUnmodifiableList()))
                .contains(asset1.getId())
                .contains(asset2.getId());
    }

    @Test
    public void testSelectById() throws SQLException {
        Asset asset1 = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .build();

        Asset asset2 = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .build();

        repository.create(asset1);
        repository.create(asset2);

        Criterion select = new Criterion(Asset.PROPERTY_ID, "=", asset1.getId());
        List<Asset> assets = repository.query(Collections.singletonList(select));

        org.assertj.core.api.Assertions.assertThat(assets.stream().map(Asset::getId).collect(Collectors.toUnmodifiableList()))
                .contains(asset1.getId());
    }

    @Test
    public void testSelectMultiple() throws SQLException {
        Asset asset1 = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contentType("pdf")
                .version("1.0.0")
                .build();

        Asset asset2 = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contentType("pdf")
                .version("1.0.0")
                .build();

        repository.create(asset1);
        repository.create(asset2);

        Criterion select1 = new Criterion(Asset.PROPERTY_CONTENT_TYPE, "=", "pdf");
        Criterion select2 = new Criterion(Asset.PROPERTY_VERSION, "=", "1.0.0");
        List<Asset> assets = repository.query(Arrays.asList(select1, select2));

        org.assertj.core.api.Assertions.assertThat(assets.stream().map(Asset::getId).collect(Collectors.toUnmodifiableList()))
                .contains(asset1.getId())
                .contains(asset2.getId());
    }

    @Test
    public void testAssetUpdate() throws SQLException {
        Asset base = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contentType("pdf")
                .version("1.0.0")
                .property("foo", "bar")
                .build();

        Asset asset = Asset.Builder.newInstance()
                .id(base.getId())
                .contentType("pdf")
                .version("1.1.0")
                .name("updatedAsset")
                .build();

        repository.create(base);
        repository.update(asset);

        List<Property> properties = sqlClient.execute(new PropertyMapper(),
                PreparedStatementResourceReader.readPropertiesSelectByAssetId(), asset.getId());
        List<Boolean> exists = sqlClient.execute(new ExistsMapper(),
                PreparedStatementResourceReader.readAssetExists(), asset.getId());

        Assertions.assertTrue(exists.size() == 1 && exists.get(0));
        Assertions.assertEquals(4, properties.size());
        assertThat(properties)
                .contains(new Property(Asset.PROPERTY_ID, asset.getId()))
                .contains(new Property(Asset.PROPERTY_CONTENT_TYPE, asset.getContentType()))
                .contains(new Property(Asset.PROPERTY_NAME, asset.getName()))
                .contains(new Property(Asset.PROPERTY_VERSION, asset.getVersion()));
    }

    @Test
    public void testAssetDelete() throws SQLException {
        Asset asset = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contentType("pdf")
                .version("1.0.0")
                .build();

        repository.create(asset);
        repository.delete(asset);

        List<Property> properties = sqlClient.execute(new PropertyMapper(),
                PreparedStatementResourceReader.readPropertiesSelectByAssetId(), asset.getId());
        List<Boolean> exists = sqlClient.execute(new ExistsMapper(),
                PreparedStatementResourceReader.readAssetExists(), asset.getId());

        Assertions.assertFalse(exists.size() == 1 && exists.get(0));
        Assertions.assertEquals(0, properties.size());
    }
}
