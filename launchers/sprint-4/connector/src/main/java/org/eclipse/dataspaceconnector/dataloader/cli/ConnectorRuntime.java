/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */
package org.eclipse.dataspaceconnector.dataloader.cli;

import org.eclipse.dataspaceconnector.boot.system.runtime.BaseRuntime;
import org.eclipse.dataspaceconnector.spi.security.PrivateKeyResolver;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

public class ConnectorRuntime extends BaseRuntime {

    public static void main(String[] args) {
        var runtime = new ConnectorRuntime();
        runtime.boot();
    }

    @Override
    protected void initializeVault(ServiceExtensionContext context) {
        context.registerService(Vault.class, new InMemoryVault());
        context.registerService(PrivateKeyResolver.class, new NullPrivateKeyResolver());
    }

    @Override
    protected void initializeContext(ServiceExtensionContext context) {
        super.initializeContext(context);
    }
}
