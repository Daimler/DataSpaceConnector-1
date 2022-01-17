package org.eclipse.dataspaceconnector.transaction.core;

import org.eclipse.dataspaceconnector.transaction.spi.TransactionContext;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionManager;

public class TransactionManagerImpl implements TransactionManager {

    private final ThreadLocal<TransactionContextImpl> contextThreadLocal = ThreadLocal.withInitial(TransactionContextImpl::new);

    @Override
    public TransactionContext beginTransaction() {
        TransactionContextImpl context = contextThreadLocal.get();
        context.incrementCounter();

        context.onAfterCommit(contextThreadLocal::remove);
        context.onAfterRollback(contextThreadLocal::remove);

        return context;
    }

}
