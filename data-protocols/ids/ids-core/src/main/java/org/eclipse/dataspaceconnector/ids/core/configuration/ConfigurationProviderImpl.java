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

// TODO No Lazy Loading
// TODO Create validation classes for generalized setting validation (for whoever uses them).
// TODO Move this out and hand configuration POJOs to their corresponding service
// TODO Add warnings for missing and required and invalid configuration
public class ConfigurationProviderImpl implements ConfigurationProvider {

    private static class Defaults {
        // TODO define where the connector ID is defined and how it should look like
        public static final String ID = IdsId.connector("edc").getValue();
        public static final String TITLE = "Eclipse Dataspace Connector";
        public static final String DESCRIPTION = "Eclipse Dataspace Connector";
        public static final String MAINTAINER = "https://example.com";
        public static final String CURATOR = IdsId.participant("curator").getValue();
        public static final String ENDPOINT = IdsId.participant("maintainer").getValue();
    }

    private final ServiceExtensionContext context;

    public ConfigurationProviderImpl(ServiceExtensionContext context) {
        this.context = context;
    }

    @Override
    public URI resolveId() {
        return resolveUri(context.getSetting(IdsSettings.EDC_IDS_ID, Defaults.ID));
    }

    @Override
    public String resolveTitle() {
        return context.getSetting(IdsSettings.EDC_IDS_TITLE, Defaults.TITLE);
    }

    @Override
    public String resolveDescription() {
        return context.getSetting(IdsSettings.EDC_IDS_DESCRIPTION, Defaults.DESCRIPTION);
    }

    @Override
    public URI resolveMaintainer() {
        return resolveUri(context.getSetting(IdsSettings.EDC_IDS_MAINTAINER, Defaults.MAINTAINER));
    }

    @Override
    public URI resolveCurator() {
        return resolveUri(context.getSetting(IdsSettings.EDC_IDS_CURATOR, Defaults.CURATOR));
    }

    @Override
    public URI resolveConnectorEndpoint() {
        return resolveUri(context.getSetting(IdsSettings.EDC_IDS_ENDPOINT, Defaults.ENDPOINT));
    }

    @Override
    public SecurityProfile resolveSecurityProfile() {
        String securityProfileValue = context.getSetting(IdsSettings.EDC_IDS_SECURITY_PROFILE, null);
        SecurityProfile securityProfile = null;
        if (securityProfileValue != null) {
            securityProfile = mapToSecurityProfile(securityProfileValue);
        }
        return securityProfile;
    }

    private SecurityProfile mapToSecurityProfile(String securityProfile) {
        if (securityProfile == null) {
            return null;
        }

        for (SecurityProfile profile : SecurityProfile.values()) {
            if (profile.name().equalsIgnoreCase(securityProfile)) {
                return profile;
            }
        }

        return null;
    }

    private URI resolveUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException ignored) {
            return null;
        }
    }
}
