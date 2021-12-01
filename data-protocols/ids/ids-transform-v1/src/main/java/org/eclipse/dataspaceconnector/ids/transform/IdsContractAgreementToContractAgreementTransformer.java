/*
 *  Copyright (c) 2021 Fraunhofer Institute for Software and Systems Engineering
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer Institute for Software and Systems Engineering - Initial Implementation
 *
 */

package org.eclipse.dataspaceconnector.ids.transform;

import org.eclipse.dataspaceconnector.ids.spi.transform.ContractTransformerInput;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.policy.model.Duty;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.policy.model.Prohibition;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.agreement.ContractAgreement;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractOffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Transforms an IDS ContractAgreement into an {@link ContractAgreement}.
 */
public class IdsContractAgreementToContractAgreementTransformer implements IdsTypeTransformer<ContractTransformerInput, ContractAgreement> {

    @Override
    public Class<ContractTransformerInput> getInputType() {
        return ContractTransformerInput.class;
    }

    @Override
    public Class<ContractAgreement> getOutputType() {
        return ContractAgreement.class;
    }

    @Override
    public @Nullable ContractAgreement transform(ContractTransformerInput object, @NotNull TransformerContext context) {
        Objects.requireNonNull(context);
        if (object == null) {
            return null;
        }

        var contractAgreement = (de.fraunhofer.iais.eis.ContractAgreement) object.getContract();
        var asset = object.getAsset();

        var edcPermissions = new ArrayList<Permission>();
        var edcProhibitions = new ArrayList<Prohibition>();
        var edcObligations = new ArrayList<Duty>();

        if (contractAgreement.getPermission() != null) {
            for (var edcPermission : contractAgreement.getPermission()) {
                var idsPermission = context.transform(edcPermission, Permission.class);
                edcPermissions.add(idsPermission);
            }
        }

        if (contractAgreement.getProhibition() != null) {
            for (var edcProhibition : contractAgreement.getProhibition()) {
                var idsProhibition = context.transform(edcProhibition, Prohibition.class);
                edcProhibitions.add(idsProhibition);
            }
        }

        if (contractAgreement.getObligation() != null) {
            for (var edcObligation : contractAgreement.getObligation()) {
                var idsObligation = context.transform(edcObligation, Duty.class);
                edcObligations.add(idsObligation);
            }
        }

        var policyBuilder = Policy.Builder.newInstance();

        policyBuilder.duties(edcObligations);
        policyBuilder.prohibitions(edcProhibitions);
        policyBuilder.permissions(edcPermissions);

        var builder = ContractAgreement.Builder.newInstance()
                .policy(policyBuilder.build())
                .consumerAgentId(contractAgreement.getConsumer())
                .providerAgentId(contractAgreement.getProvider())
                .asset(asset);

        if (contractAgreement.getId() != null) {
            builder.id(contractAgreement.getId().toString());
        }

        if (contractAgreement.getContractEnd() != null) {
            builder.contractEndDate(contractAgreement.getContractEnd().toGregorianCalendar().toZonedDateTime());
        }

        if (contractAgreement.getContractStart() != null) {
            builder.contractStartDate(contractAgreement.getContractStart().toGregorianCalendar().toZonedDateTime());
        }

        if (contractAgreement.getContractDate() != null) {
            builder.contractSigningDate(contractAgreement.getContractDate().toGregorianCalendar().toZonedDateTime());
        }

        return builder.build();
    }
}
