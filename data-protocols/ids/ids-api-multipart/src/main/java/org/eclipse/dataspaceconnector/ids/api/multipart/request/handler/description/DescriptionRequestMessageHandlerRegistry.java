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

package org.eclipse.dataspaceconnector.ids.api.multipart.request.handler.description;

import org.eclipse.dataspaceconnector.ids.spi.IdsId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DescriptionRequestMessageHandlerRegistry {
    private final Map<IdsId.Type, DescriptionRequestMessageHandler> descriptionRequestMessageHandlers = new HashMap<>();

    public void add(final IdsId.Type type, final DescriptionRequestMessageHandler descriptionRequestMessageHandler) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(descriptionRequestMessageHandler);

        descriptionRequestMessageHandlers.put(type, descriptionRequestMessageHandler);
    }

    public DescriptionRequestMessageHandler get(final IdsId.Type type) {
        Objects.requireNonNull(type);

        return descriptionRequestMessageHandlers.get(type);
    }
}
