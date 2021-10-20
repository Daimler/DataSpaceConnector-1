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
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionBuilder;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.policy.model.AtomicConstraint;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.OfferedAsset;
import org.eclipse.dataspaceconnector.spi.types.domain.policy.CommonActionTypes;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class ContractOfferFactory {

    private final Monitor monitor;

    public ContractOfferFactory(Monitor monitor) {
        Objects.requireNonNull(monitor);
        this.monitor = monitor;
    }

    /**
     * Creates an IDS the contract offer. Works only if the EDC ContractOffer is completely map-able to IDS.
     *
     * @param contractOffer EDC equivalent of the IDS contract offer
     * @return IDS contract offer
     */
    @NotNull
    public Optional<ContractOffer> createContractOffer(@NotNull org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer contractOffer) {
        try {
            return Optional.of(createOffer(contractOffer));

            // The policy of a contract offer may be described in a wide variety if ways.
            // If a mapping to IDS is not possible an UnsupportedOperationException is thrown.
        } catch (UnsupportedOperationException e) {
            monitor.info("Cannot create contract offer in IDS.", e);
            return Optional.empty();
        }
    }

    private ContractOffer createOffer(
            org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer contractOffer
    ) {
        URI provider = contractOffer.getProvider();
        URI consumer = contractOffer.getConsumer();
        ZonedDateTime contractStart = contractOffer.getContractStart();
        ZonedDateTime contractEnd = contractOffer.getContractEnd();

        ContractOfferBuilder builder = new ContractOfferBuilder();

        Optional.ofNullable(provider).ifPresent(builder::_provider_);
        Optional.ofNullable(consumer).ifPresent(builder::_consumer_);

        Optional.ofNullable(contractStart).map(this::mapToXmlGregorianCalendar).ifPresent(builder::_contractStart_);
        Optional.ofNullable(contractEnd).map(this::mapToXmlGregorianCalendar).ifPresent(builder::_contractEnd_);

        ArrayList<Permission> permissions = new ArrayList<>();
        for (OfferedAsset offeredAsset : contractOffer.getAssets()) {
            Policy policy = offeredAsset.getPolicy();

            if (policy.getObligations() != null && !policy.getObligations().isEmpty()) {
                throw new UnsupportedOperationException("Policy must not have an obligation");
            }
            if (policy.getProhibitions() != null && !policy.getProhibitions().isEmpty()) {
                throw new UnsupportedOperationException("Policy must not have a prohibition");
            }

            Asset asset = offeredAsset.getAsset();
            permissions.addAll(createPermissions(asset, policy, consumer, provider));
        }
        builder._permission_(permissions);

        return builder.build();
    }

    private XMLGregorianCalendar mapToXmlGregorianCalendar(ZonedDateTime zonedDateTime) {
        GregorianCalendar gregorianCalendar = GregorianCalendar.from(zonedDateTime);
        try {
            return DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            // TODO create custom exception
            throw new RuntimeException(e);
        }
    }

    private List<Permission> createPermissions(Asset asset,
                                               Policy policy,
                                               URI consumer,
                                               URI provider) {

        List<Permission> permissions = new ArrayList<>();
        // the target URI must be the similar as the one, the ResourceFactory generates for assets
        URI target = IdsId.artifact(asset.getId()).toUri();

        for (org.eclipse.dataspaceconnector.policy.model.Permission permission : policy.getPermissions()) {
            // Don't take UID from the permission policy model, as the ContractOfferFramework, that provides policies,
            // doesn't know about IDS UUIDs.
            PermissionBuilder permissionBuilder = new PermissionBuilder();
            Action action = createAction(permission.getAction());

            permissionBuilder._action_(new ArrayList<>(Collections.singletonList(action)));

            // Don't take target from the policy model, as the ContractOfferFramework, that provides policies,
            // doesn't know about IDS Asset URIs.
            permissionBuilder._target_(target);

            // Don't take assignee from the policy model, as the ContractOfferFramework, that provides policies,
            // doesn't know about IDS consumer URIs.
            Optional.ofNullable(consumer)
                    .ifPresent(c -> permissionBuilder._assignee_(new ArrayList<>(Collections.singletonList(c))));

            // Don't take assigner from the policy model, as the ContractOfferFramework, that provides policies,
            // doesn't know about IDS provider URIs.
            Optional.ofNullable(provider)
                    .ifPresent(p -> permissionBuilder._assigner_(new ArrayList<>(Collections.singletonList(p))));

            // The current permission object knows only one type of duty. It is undefined whether this represents an
            // IDS pre- or post-duty. This implementation will abstain from supporting duties until this issue has been resolved.
            // TODO create an issue before the pull request is closed and link it here
            if (permission.getDuty() != null) {
                throw new UnsupportedOperationException("Policy permission must not have a duty.");
            }

            Optional.ofNullable(permission.getConstraints())
                    .map(this::createConstraint)
                    .map(ArrayList::new)
                    .map(permissionBuilder::_constraint_);

            permissions.add(permissionBuilder.build());
        }

        return permissions;
    }

    private Action createAction(org.eclipse.dataspaceconnector.policy.model.Action action) {
        // All defined/documented actions that are map-able to the corresponding enum in IDS.
        List<String> supportedActionTypes = Collections.singletonList(CommonActionTypes.ALL);

        if (CommonActionTypes.ALL.equals(action.getType())) {
            return Action.USE;
        }

        throw new UnsupportedOperationException(String.format("Permission action type '%s' not supported. Supported action types: %s",
                action.getType(), String.join(", ", supportedActionTypes)));
    }

    private List<Constraint> createConstraint(List<org.eclipse.dataspaceconnector.policy.model.Constraint> edcConstraints) {

        for (org.eclipse.dataspaceconnector.policy.model.Constraint edcConstraint : edcConstraints) {

            // Non atomic constrains don't have the IDS left-operand, right-operand, operator properties and cannot be mapped to IDS constraints.
            // TODO create an issue before the pull request is closed and link it here
            if (!(edcConstraint instanceof AtomicConstraint)) {
                throw new UnsupportedOperationException("IDS only supports atomic constraints of the policy model.");
            }

            // Expressions from the policy model (at the time of writing only LiteralExpressions)
            // must be mapped to IDS LeftOperand Enum or IDS RightOperand RdfResource.
            // Also, the EDC policy model knows of 7 different operators, the IDS model knows of 46 different operators.
            // At the time of writing it is not defined, how a mapping between the policy model and IDS is possible.
            // TODO create an issue before the pull request is closed and link it here
            throw new UnsupportedOperationException("EDC constraints not supported in IDS.");
        }

        return new ArrayList<>();
    }

}
