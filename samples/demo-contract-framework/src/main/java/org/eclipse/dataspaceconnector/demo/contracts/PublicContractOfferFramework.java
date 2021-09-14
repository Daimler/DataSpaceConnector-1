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

package org.eclipse.dataspaceconnector.demo.contracts;

import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferFramework;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferFrameworkQuery;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferTemplate;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.OfferedAsset;
import org.eclipse.dataspaceconnector.spi.types.domain.policy.Action;
import org.eclipse.dataspaceconnector.spi.types.domain.policy.Rule;
import org.eclipse.dataspaceconnector.spi.types.domain.policy.UsagePolicy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Creates free of use contract offers for all assets.
 */
public class PublicContractOfferFramework implements ContractOfferFramework {

    @Override
    public Stream<ContractOfferTemplate> queryTemplates(final ContractOfferFrameworkQuery query) {
        return Stream.of(FixtureContractOfferTemplate.INSTANCE);
    }

    enum FixtureContractOfferTemplate implements ContractOfferTemplate {
        INSTANCE;

        @Override
        public Stream<ContractOffer> getTemplatedOffers(final Stream<Asset> assets) {
            return assets.map(this::createContractOffer);
        }

        @Override
        public Optional<AssetSelectorExpression> getSelectorExpression() {
            return Optional.of(AssetSelectorExpression.builder().build());
        }

        private ContractOffer createContractOffer(final Asset asset) {
            final ContractOffer.Builder builder = ContractOffer.builder();

            final Rule rule = Rule.builder()
                    .action(Action.ALL)
                    .constraints(Collections.emptyList())
                    .build();

            final List<Rule> rules = Collections.singletonList(rule);

            final UsagePolicy usagePolicy = UsagePolicy.builder()
                    .rules(rules)
                    .build();

            final OfferedAsset offeredAsset = OfferedAsset.builder()
                    .asset(asset)
                    .usagePolicy(usagePolicy)
                    .build();

            builder.assets(Collections.singletonList(offeredAsset));

            return builder.build();
        }
    }
}
