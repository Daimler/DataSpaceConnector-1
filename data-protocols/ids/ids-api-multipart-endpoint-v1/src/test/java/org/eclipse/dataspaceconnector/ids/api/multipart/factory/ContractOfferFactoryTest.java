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

package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import de.fraunhofer.iais.eis.Action;
import org.assertj.core.api.Assertions;
import org.easymock.EasyMock;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.OfferedAsset;
import org.eclipse.dataspaceconnector.spi.types.domain.policy.CommonActionTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

public class ContractOfferFactoryTest {
    private static URI provider;
    private static URI consumer;

    private ZonedDateTime contractStart = ZonedDateTime.now();
    private ZonedDateTime contractEnd = ZonedDateTime.now();

    private String assetId = UUID.randomUUID().toString();
    // mocks
    private Monitor monitor;
    private Asset asset;
    private Policy policy;
    private OfferedAsset offeredAsset;
    private ContractOffer contractOffer;
    private Permission permission;

    @BeforeAll
    public static void setupAll() throws URISyntaxException {
        provider = new URI("https://provider.com");
        consumer = new URI("https://consumer.com");
    }

    @BeforeEach
    public void setup() {
        monitor = EasyMock.createMock(Monitor.class);
        asset = EasyMock.createMock(Asset.class);
        policy = EasyMock.createMock(Policy.class);
        offeredAsset = EasyMock.createMock(OfferedAsset.class);
        contractOffer = EasyMock.createMock(ContractOffer.class);
        permission = EasyMock.createMock(Permission.class);
    }

    @AfterEach
    public void teardown() {
        EasyMock.verify(monitor, asset, policy, permission, offeredAsset, contractOffer);
    }

    @Test
    void testContractOfferFactoryReturnsValidOffer() {
        // prepare
        monitor = EasyMock.createMock(Monitor.class);
        asset = EasyMock.createMock(Asset.class);
        policy = EasyMock.createMock(Policy.class);
        offeredAsset = EasyMock.createMock(OfferedAsset.class);
        contractOffer = EasyMock.createMock(ContractOffer.class);
        permission = EasyMock.createMock(Permission.class);

        org.eclipse.dataspaceconnector.policy.model.Action action =
                org.eclipse.dataspaceconnector.policy.model.Action.Builder.newInstance().type(CommonActionTypes.ALL).build();
        EasyMock.expect(permission.getAction()).andReturn(action).times(1);
        EasyMock.expect(permission.getDuty()).andReturn(null).times(1);
        EasyMock.expect(permission.getConstraints()).andReturn(null).times(1);
        EasyMock.expect(asset.getId()).andReturn(assetId).times(1);
        EasyMock.expect(policy.getObligations()).andReturn(null).times(1);
        EasyMock.expect(policy.getProhibitions()).andReturn(null).times(1);
        EasyMock.expect(policy.getPermissions()).andReturn(Collections.singletonList(permission)).times(1);
        EasyMock.expect(offeredAsset.getPolicy()).andReturn(policy).times(1);
        EasyMock.expect(offeredAsset.getAsset()).andReturn(asset).times(1);

        EasyMock.expect(contractOffer.getProvider()).andReturn(provider).times(1);
        EasyMock.expect(contractOffer.getConsumer()).andReturn(consumer).times(1);
        EasyMock.expect(contractOffer.getAssets()).andReturn(Collections.singletonList(offeredAsset)).times(1);

        EasyMock.expect(contractOffer.getContractStart()).andReturn(contractStart).times(1);
        EasyMock.expect(contractOffer.getContractEnd()).andReturn(contractEnd).times(1);

        EasyMock.replay(monitor, asset, policy, permission, offeredAsset, contractOffer);

        ContractOfferFactory contractOfferFactory = new ContractOfferFactory(monitor);

        // invoke
        de.fraunhofer.iais.eis.ContractOffer result = contractOfferFactory.createContractOffer(contractOffer).orElseThrow();

        // verify
        Assertions.assertThat(provider).isEqualTo(result.getProvider());
        Assertions.assertThat(consumer).isEqualTo(result.getConsumer());

        Assertions.assertThat(result.getPermission()).hasSize(1);

        Assertions.assertThat(result.getPermission().get(0).getAssignee()).hasSize(1);
        Assertions.assertThat(result.getPermission().get(0).getAssignee().get(0)).isEqualTo(consumer);

        Assertions.assertThat(result.getPermission().get(0).getAssigner()).hasSize(1);
        Assertions.assertThat(result.getPermission().get(0).getAssigner().get(0)).isEqualTo(provider);

        String assetIdValue = IdsId.fromUri(result.getPermission().get(0).getTarget()).getValue();
        Assertions.assertThat(assetIdValue).isEqualTo(assetId);

        Assertions.assertThat(result.getPermission().get(0).getAction().get(0)).isEqualTo(Action.USE);
    }
}
