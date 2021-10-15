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

package org.eclipse.dataspaceconnector.ids.core.configuration;

import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;
import org.eclipse.dataspaceconnector.ids.spi.types.SecurityProfile;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

// TODO Add warnings for missing (but required) and invalid configuration

public final class ConfigurationProviderImpl implements ConfigurationProvider {

    private static final class Defaults {
        // TODO define where the connector ID is defined and how it should look like
        public static final String ID = IdsId.connector("edc").getValue();
        public static final String TITLE = "Eclipse Dataspace Connector";
        public static final String DESCRIPTION = "Eclipse Dataspace Connector";
        public static final String MAINTAINER = "https://example.com";
        public static final String CURATOR = IdsId.participant("curator").getValue();
        public static final String ENDPOINT = IdsId.participant("maintainer").getValue();
    }

    private final ServiceExtensionContext context;

    public ConfigurationProviderImpl(final ServiceExtensionContext context) {
        this.context = context;
    }

    @Override
    public Optional<URI> resolveId() {
        return resolveUri(context.getSetting(IdsSettings.EDC_IDS_ID, Defaults.ID));
    }

    @Override
    public Optional<String> resolveTitle() {
        return Optional.of(context.getSetting(IdsSettings.EDC_IDS_TITLE, Defaults.TITLE));
    }

    @Override
    public Optional<String> resolveDescription() {
        return Optional.of(context.getSetting(IdsSettings.EDC_IDS_DESCRIPTION, Defaults.DESCRIPTION));
    }

    @Override
    public Optional<URI> resolveMaintainer() {
        return resolveUri(context.getSetting(IdsSettings.EDC_IDS_MAINTAINER, Defaults.MAINTAINER));
    }

    @Override
    public Optional<URI> resolveCurator() {
        return resolveUri(context.getSetting(IdsSettings.EDC_IDS_CURATOR, Defaults.CURATOR));
    }

    @Override
    public Optional<URI> resolveConnectorEndpoint() {
        return resolveUri(context.getSetting(IdsSettings.EDC_IDS_ENDPOINT, Defaults.ENDPOINT));
    }

    @Override
    public Optional<SecurityProfile> resolveSecurityProfile() {
        return Optional.ofNullable(context.getSetting(IdsSettings.EDC_IDS_SECURITY_PROFILE, null))
                .map(this::mapToSecurityProfile);
    }

    private SecurityProfile mapToSecurityProfile(final String securityProfile) {
        return Arrays.stream(SecurityProfile.values())
                .filter(f -> securityProfile.equalsIgnoreCase(f.name()))
                .findFirst()
                .orElse(null);
    }

    private Optional<URI> resolveUri(final String uri) {
        try {
            return Optional.of(new URI(uri));
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }
}
