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

package org.eclipse.dataspaceconnector.runtime;

import org.eclipse.dataspaceconnector.monitor.MonitorProvider;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;
import org.eclipse.dataspaceconnector.system.DefaultServiceExtensionContext;

import java.util.List;
import java.util.ListIterator;

import static org.eclipse.dataspaceconnector.system.ExtensionLoader.bootServiceExtensions;
import static org.eclipse.dataspaceconnector.system.ExtensionLoader.loadMonitor;
import static org.eclipse.dataspaceconnector.system.ExtensionLoader.loadVault;

public class EdcRuntime {

    private static final String NAME = "Eclipse Dataspace Connector";

    public static void main(String... arg) {
        final TypeManager typeManager = new TypeManager();
        final Monitor monitor = loadMonitor();

        MonitorProvider.setInstance(monitor);

        final DefaultServiceExtensionContext context = new DefaultServiceExtensionContext(typeManager, monitor);
        context.initialize();

        try {

            loadVault(context);

            final List<ServiceExtension> serviceExtensions = context.loadServiceExtensions();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(serviceExtensions, monitor)));

            bootServiceExtensions(serviceExtensions, context);
        } catch (Exception e) {
            monitor.severe("Error booting runtime", e);
            System.exit(-1);  // stop the process
        }
        monitor.info(String.format("%s started", NAME));
    }

    private static void shutdown(List<ServiceExtension> serviceExtensions, Monitor monitor) {
        final ListIterator<ServiceExtension> iter = serviceExtensions.listIterator(serviceExtensions.size());
        while (iter.hasPrevious()) {
            iter.previous().shutdown();
        }
        monitor.info(String.format("%s shutdown", NAME));
    }

}

