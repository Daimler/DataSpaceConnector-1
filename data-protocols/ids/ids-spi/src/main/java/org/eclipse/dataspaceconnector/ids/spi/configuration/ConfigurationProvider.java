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

package org.eclipse.dataspaceconnector.ids.spi.configuration;

import org.eclipse.dataspaceconnector.ids.spi.types.SecurityProfile;

import java.net.URI;
import java.util.Optional;

/**
 * Provider for the IDS configuration.
 */
public interface ConfigurationProvider {
    Optional<URI> resolveId();

    Optional<String> resolveTitle();

    Optional<String> resolveDescription();

    Optional<URI> resolveMaintainer();

    Optional<URI> resolveCurator();

    Optional<URI> resolveConnectorEndpoint();

    Optional<SecurityProfile> resolveSecurityProfile();
}
