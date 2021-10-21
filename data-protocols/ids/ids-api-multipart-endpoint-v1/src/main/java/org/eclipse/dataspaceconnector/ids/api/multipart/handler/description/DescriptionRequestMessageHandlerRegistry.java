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

package org.eclipse.dataspaceconnector.ids.api.multipart.handler.description;

import org.eclipse.dataspaceconnector.ids.spi.IdsId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// TODO Remove this class and add the registry to the description request handler
public class DescriptionRequestMessageHandlerRegistry {
    private final Map<IdsId.Type, DescriptionRequestMessageHandler> descriptionRequestMessageHandlers = new HashMap<>();

    public void add(IdsId.Type type, DescriptionRequestMessageHandler descriptionRequestMessageHandler) {
        Objects.requireNonNull(descriptionRequestMessageHandler);

        descriptionRequestMessageHandlers.put(type, descriptionRequestMessageHandler);
    }

    public DescriptionRequestMessageHandler get(IdsId.Type type) {
        return descriptionRequestMessageHandlers.get(type);
    }
}
