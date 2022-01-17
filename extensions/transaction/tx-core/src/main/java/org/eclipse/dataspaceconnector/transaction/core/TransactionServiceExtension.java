package org.eclipse.dataspaceconnector.transaction.core;

import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionManager;

public class TransactionServiceExtension implements ServiceExtension {

    @Override
    public void initialize(ServiceExtensionContext context) {
        TransactionManager transactionManager = new TransactionManagerImpl();
        context.registerService(TransactionManager.class, transactionManager);
    }
}
