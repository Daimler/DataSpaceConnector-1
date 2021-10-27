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
 *       Daimler TSS GmbH - Initial Implementation
 *
 */

package org.eclipse.dataspaceconnector.ids.transform;

import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.jetbrains.annotations.Nullable;

public class ResourceToRepresentationTransformer implements IdsTypeTransformer {
    @Override
    public Class getInputType() {
        return null;
    }

    @Override
    public Class getOutputType() {
        return null;
    }

    @Nullable
    @Override
    public Object transform(Object object, TransformerContext context) {
        return null;
    }
}
