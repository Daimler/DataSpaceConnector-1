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

import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.ConnectorEndpoint;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import org.eclipse.dataspaceconnector.ids.spi.configuration.ConfigurationProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.ConnectorVersionProvider;
import org.eclipse.dataspaceconnector.ids.spi.version.IdsProtocolVersion;
import org.eclipse.dataspaceconnector.ids.spi.version.InboundProtocolVersionManager;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class BaseConnectorFactory {

    private final ConfigurationProvider configurationProvider;
    private final InboundProtocolVersionManager inboundProtocolVersionManager;
    private final ConnectorVersionProvider connectorVersionProvider;

    public BaseConnectorFactory(
            final ConfigurationProvider configurationProvider,
            final InboundProtocolVersionManager inboundProtocolVersionManager,
            final ConnectorVersionProvider connectorVersionProvider) {
        Objects.requireNonNull(configurationProvider);
        Objects.requireNonNull(inboundProtocolVersionManager);
        Objects.requireNonNull(connectorVersionProvider);

        this.configurationProvider = configurationProvider;
        this.inboundProtocolVersionManager = inboundProtocolVersionManager;
        this.connectorVersionProvider = connectorVersionProvider;
    }

    public BaseConnector createBaseConnector(
            final ResourceCatalog... resourceCatalog) {

        final BaseConnectorBuilder builder = configurationProvider.resolveId()
                .map(BaseConnectorBuilder::new)
                .orElseGet(BaseConnectorBuilder::new);

        builder._resourceCatalog_(new ArrayList<>(Arrays.asList(resourceCatalog)));
        builder._inboundModelVersion_(new ArrayList<>(resolveInboundModelVersion()));
        // TODO There should be a process how the security profile is defined/found

        resolveSecurityProfile().ifPresent(builder::_securityProfile_);
        resolveConnectorEndpoint().ifPresent(builder::_hasDefaultEndpoint_);
        resolveDataSpaceConnectorVersion().ifPresent(builder::_version_);
        resolveMaintainer().ifPresent(builder::_maintainer_);
        resolveCurator().ifPresent(builder::_curator_);
        resolveTitle().map(TypedLiteral::new).map(Util::asList).ifPresent(builder::_title_);
        resolveDescription().map(TypedLiteral::new).map(Util::asList).ifPresent(builder::_description_);

        return builder.build();
    }

    private Optional<SecurityProfile> resolveSecurityProfile() {
        return configurationProvider.resolveSecurityProfile()
                .map(Enum::name)
                .flatMap(name -> Arrays.stream(SecurityProfile.values())
                        .filter(e -> e.name().equalsIgnoreCase(name))
                        .findFirst());
    }

    private Optional<URI> resolveCurator() {
        return configurationProvider.resolveCurator();
    }

    private Optional<ConnectorEndpoint> resolveConnectorEndpoint() {
        return configurationProvider.resolveConnectorEndpoint()
                .map(this::createConnectorEndpoint);
    }

    private ConnectorEndpoint createConnectorEndpoint(final URI uri) {
        final ConnectorEndpointBuilder endpoint = new ConnectorEndpointBuilder();
        endpoint._accessURL_(uri);
        return endpoint.build();
    }

    private Optional<URI> resolveMaintainer() {
        return configurationProvider.resolveMaintainer();
    }

    private Optional<String> resolveDescription() {
        return configurationProvider.resolveDescription();
    }

    private Optional<String> resolveTitle() {
        return configurationProvider.resolveTitle();
    }

    private List<String> resolveInboundModelVersion() {
        return inboundProtocolVersionManager.getInboundProtocolVersions()
                .stream()
                .map(IdsProtocolVersion::getValue)
                .distinct()
                .collect(Collectors.toList());
    }

    private Optional<String> resolveDataSpaceConnectorVersion() {
        return connectorVersionProvider.getVersion();
    }
}
