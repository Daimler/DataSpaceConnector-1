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

package org.eclipse.dataspaceconnector.ids.api.multipart.version;

import de.fraunhofer.iais.eis.Connector;
import org.eclipse.dataspaceconnector.ids.spi.version.IdsProtocolVersion;
import org.eclipse.dataspaceconnector.ids.spi.version.ProtocolVersionProvider;

import java.util.Optional;

public class ProtocolVersionProviderImpl implements ProtocolVersionProvider {
    private static final String VERSION = "4.0.0";

    // TODO Remove fallback version after the Java information model provides the version programmatically
    // GitHub Issue https://github.com/International-Data-Spaces-Association/Java-Representation-of-IDS-Information-Model/issues/10
    @Override
    public IdsProtocolVersion getIdsProtocolVersion() {
        return Optional.ofNullable(Connector.class.getPackage().getImplementationVersion())
                .map(IdsProtocolVersion::new)
                .orElseGet(() -> new IdsProtocolVersion(VERSION));
    }
}
