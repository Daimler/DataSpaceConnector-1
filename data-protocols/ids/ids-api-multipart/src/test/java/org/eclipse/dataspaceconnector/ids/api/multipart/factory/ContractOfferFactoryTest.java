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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class ContractOfferFactoryTest {
    private static URI provider;
    private static URI consumer;

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

        final org.eclipse.dataspaceconnector.policy.model.Action action =
                org.eclipse.dataspaceconnector.policy.model.Action.Builder.newInstance().type(CommonActionTypes.ALL).build();
        EasyMock.expect(permission.getAction()).andReturn(action);
        EasyMock.expect(permission.getDuty()).andReturn(null);
        EasyMock.expect(permission.getConstraints()).andReturn(null);
        EasyMock.expect(asset.getId()).andReturn("cd53848e-892e-490b-83ad-edc9ea933446").anyTimes();
        EasyMock.expect(policy.getObligations()).andReturn(null);
        EasyMock.expect(policy.getProhibitions()).andReturn(null);
        EasyMock.expect(policy.getPermissions()).andReturn(Collections.singletonList(permission));
        EasyMock.expect(offeredAsset.getAsset()).andReturn(asset).anyTimes();
        EasyMock.expect(offeredAsset.getPolicy()).andReturn(policy).anyTimes();

        EasyMock.expect(contractOffer.getProvider()).andReturn(provider).anyTimes();
        EasyMock.expect(contractOffer.getConsumer()).andReturn(consumer).anyTimes();
        EasyMock.expect(contractOffer.getAssets()).andReturn(Collections.singletonList(offeredAsset)).anyTimes();

        EasyMock.replay(monitor, asset, policy, permission, offeredAsset, contractOffer);

    }

    @AfterEach
    public void teardown() {
        EasyMock.verify(monitor, asset, policy, permission, offeredAsset, contractOffer);
    }

    @Test
    void testContractOfferFactoryReturnsValidOffer() {
        // prepare
        final ContractOfferFactory contractOfferFactory = new ContractOfferFactory(monitor);

        // invoke
        de.fraunhofer.iais.eis.ContractOffer result = contractOfferFactory.createContractOffer(contractOffer).orElseThrow();

        // validate
        Assertions.assertEquals(provider, result.getProvider());
        Assertions.assertEquals(consumer, result.getConsumer());
        Assertions.assertEquals(1, result.getPermission().size());
        Assertions.assertEquals(1, result.getPermission().get(0).getAssignee().size());
        Assertions.assertEquals(consumer, result.getPermission().get(0).getAssignee().get(0));
        Assertions.assertEquals(1, result.getPermission().get(0).getAssigner().size());
        Assertions.assertEquals(provider, result.getPermission().get(0).getAssigner().get(0));
        Assertions.assertEquals(1, result.getPermission().size());
        Assertions.assertEquals(IdsId.artifact(asset.getId()).toUri(), result.getPermission().get(0).getTarget());
        Assertions.assertEquals(Action.USE, result.getPermission().get(0).getAction().get(0));
    }
}
