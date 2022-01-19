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

package org.eclipse.dataspaceconnector.clients.postgresql.asset.operation;

import org.eclipse.dataspaceconnector.clients.postgresql.PostgresqlClient;
import org.eclipse.dataspaceconnector.clients.postgresql.asset.Repository;
import org.eclipse.dataspaceconnector.clients.postgresql.asset.mapper.ExistsMapper;
import org.eclipse.dataspaceconnector.clients.postgresql.asset.mapper.PropertyMapper;
import org.eclipse.dataspaceconnector.clients.postgresql.asset.types.Property;
import org.eclipse.dataspaceconnector.clients.postgresql.asset.util.PreparedStatementResourceReader;
import org.eclipse.dataspaceconnector.common.annotations.IntegrationTest;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class AssetAndAddressCreateOperationTest extends AbstractOperationTest {

    private Repository repository;
    private PostgresqlClient postgresqlClient;

    @BeforeEach
    public void setup() {
        repository = getRepository();
        postgresqlClient = getClient();
    }

    @Test
    public void testAssetAndAddressCreation() throws SQLException {

        Asset asset = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contentType("pdf")
                .version("1.0.0")
                .build();

        DataAddress address = DataAddress.Builder.newInstance()
                .property("foo", "bar")
                .property("type", "foo")
                .build();

        String addressId = asset.getId();

        repository.create(asset, address);

        List<Property> assetProperties = postgresqlClient.execute(new PropertyMapper(),
                PreparedStatementResourceReader.readAssetPropertiesSelectByAssetId(), asset.getId());
        List<Property> addressProperties = postgresqlClient.execute(new PropertyMapper(),
                PreparedStatementResourceReader.readAddressPropertiesSelectByAddressId(), addressId);
        List<Boolean> assetExists = postgresqlClient.execute(new ExistsMapper(),
                PreparedStatementResourceReader.readAssetExists(), asset.getId());
        List<Boolean> addressExists = postgresqlClient.execute(new ExistsMapper(),
                PreparedStatementResourceReader.readAddressExists(), addressId);

        Assertions.assertTrue(assetExists.size() == 1 && assetExists.get(0));
        Assertions.assertTrue(addressExists.size() == 1 && assetExists.get(0));
        Assertions.assertEquals(3, assetProperties.size());
        Assertions.assertEquals(2, addressProperties.size());
        assertThat(assetProperties)
                .contains(new Property(Asset.PROPERTY_ID, asset.getId()))
                .contains(new Property(Asset.PROPERTY_CONTENT_TYPE, asset.getContentType()))
                .contains(new Property(Asset.PROPERTY_VERSION, asset.getVersion()));
        assertThat(addressProperties)
                .contains(new Property("foo", "bar"))
                .contains(new Property("type", "foo"));
    }

}