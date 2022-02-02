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
 *       Microsoft Corporation - Refactoring
 *
 */
package org.eclipse.dataspaceconnector.contract.offer;

import org.eclipse.dataspaceconnector.contract.common.ContractId;
import org.eclipse.dataspaceconnector.policy.model.Duty;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.policy.model.Prohibition;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.contract.agent.ParticipantAgentService;
import org.eclipse.dataspaceconnector.spi.contract.offer.ContractDefinitionService;
import org.eclipse.dataspaceconnector.spi.contract.offer.ContractOfferQuery;
import org.eclipse.dataspaceconnector.spi.contract.offer.ContractOfferService;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractOffer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Implementation of the {@link ContractOfferService}.
 */
public class ContractOfferServiceImpl implements ContractOfferService {
    private final ParticipantAgentService agentService;
    private final ContractDefinitionService definitionService;
    private final AssetIndex assetIndex;

    public ContractOfferServiceImpl(ParticipantAgentService agentService, ContractDefinitionService definitionService, AssetIndex assetIndex) {
        this.agentService = Objects.requireNonNull(agentService, "ParticipantAgentService must not be null");
        this.definitionService = Objects.requireNonNull(definitionService, "ContractDefinitionService must not be null");
        this.assetIndex = Objects.requireNonNull(assetIndex, "AssetIndex must not be null");
    }

    @Override
    @NotNull
    public Stream<ContractOffer> queryContractOffers(ContractOfferQuery query) {
        var agent = agentService.createFor(query.getClaimToken());
        var definitions = definitionService.definitionsFor(agent);

        return definitions.flatMap(definition -> {
            var assets = assetIndex.queryAssets(definition.getSelectorExpression());
            return assets.map(asset -> ContractOffer.Builder.newInstance()
                    .id(ContractId.createContractId(definition.getId()))
                    .policy(createTargetedPolicy(definition.getContractPolicy(), asset.getId()))
                    .asset(asset)
                    .build());
        });
    }

    private Policy createTargetedPolicy(Policy p, String targetId) {

        Policy.Builder policyBuilder = Policy.Builder.newInstance();
        policyBuilder.id(p.getUid());
        policyBuilder.target(targetId);
        policyBuilder.assignee(p.getAssignee());
        policyBuilder.assigner(p.getAssigner());
        policyBuilder.extensibleProperties(p.getExtensibleProperties());
        policyBuilder.type(p.getType());

        if (p.getPermissions() != null) {
            for (Permission permission : p.getPermissions()) {
                policyBuilder.permission(createTargetedPermission(permission, targetId));
            }
        }

        if (p.getObligations() != null) {
            for (Duty d : p.getObligations()) {
                policyBuilder.duty(createTargetedDuty(d, targetId));
            }
        }

        if (p.getProhibitions() != null) {
            for (Prohibition prohibition : p.getProhibitions()) {
                policyBuilder.prohibition(createTargetedProhibition(prohibition, targetId));
            }
        }

        return policyBuilder.build();
    }

    private Prohibition createTargetedProhibition(Prohibition p, String targetId) {
        Prohibition.Builder prohibitionBuilder = Prohibition.Builder.newInstance();
        prohibitionBuilder.target(targetId);
        prohibitionBuilder.action(p.getAction());
        prohibitionBuilder.assignee(p.getAssignee());
        prohibitionBuilder.assigner(p.getAssigner());

        if (p.getConstraints() != null) {
            prohibitionBuilder.constraints(p.getConstraints());
        }

        return prohibitionBuilder.build();
    }

    private Permission createTargetedPermission(Permission p, String targetId) {
        Permission.Builder permissionBuilder = Permission.Builder.newInstance();
        permissionBuilder.target(targetId);
        permissionBuilder.uid(p.getUid());
        permissionBuilder.action(p.getAction());
        permissionBuilder.assignee(p.getAssignee());
        permissionBuilder.assigner(p.getAssigner());

        if (p.getDuties() != null) {
            for (Duty d : p.getDuties()) {
                permissionBuilder.duty(createTargetedDuty(d, targetId));
            }
        }

        if (p.getConstraints() != null) {
            permissionBuilder.constraints(p.getConstraints());
        }

        return permissionBuilder.build();
    }

    private Duty createTargetedDuty(Duty d, String targetId) {
        Duty.Builder dutyBuilder = Duty.Builder.newInstance();
        dutyBuilder.uid(d.getUid());
        dutyBuilder.target(targetId);
        dutyBuilder.parentPermission(d.getParentPermission());
        dutyBuilder.assignee(d.getAssignee());
        dutyBuilder.assigner(d.getAssigner());
        dutyBuilder.action(d.getAction());

        if (d.getConsequence() != null) {
            dutyBuilder.consequence(createTargetedDuty(d.getConsequence(), targetId));
        }

        return dutyBuilder.build();
    }
}
