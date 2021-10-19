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

package org.eclipse.dataspaceconnector.ids.core.version;

import org.eclipse.dataspaceconnector.ids.spi.version.IdsProtocolVersion;
import org.eclipse.dataspaceconnector.ids.spi.version.InboundProtocolVersionManager;
import org.eclipse.dataspaceconnector.ids.spi.version.ProtocolVersionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InboundProtocolVersionManagerImpl implements InboundProtocolVersionManager {
    private final List<ProtocolVersionProvider> inboundProviders = new LinkedList<>();

    @Override
    public void addInboundProtocolVersionProvider(ProtocolVersionProvider protocolVersionProvider) {
        if (protocolVersionProvider != null) {
            inboundProviders.add(protocolVersionProvider);
        }
    }

    @NotNull
    @Override
    public List<IdsProtocolVersion> getInboundProtocolVersions() {
        return inboundProviders.stream()
                .map(ProtocolVersionProvider::getIdsProtocolVersion)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
