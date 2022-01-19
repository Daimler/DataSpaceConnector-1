package org.eclipse.dataspaceconnector.transaction.core;

import org.eclipse.dataspaceconnector.transaction.spi.StatusChangedListener;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionContext;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionManager;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionStatus;

import java.util.ArrayList;
import java.util.List;

public class TransactionManagerImpl implements TransactionManager {

    private final ThreadLocal<TransactionContextImpl> contextThreadLocal = new ThreadLocal<>();
    private final ThreadLocal<List<StatusChangedListener>> statusChangedListenerList = ThreadLocal.withInitial(ArrayList::new);

    @Override
    public TransactionContext beginTransaction() {
        if (contextThreadLocal.get() == null) {
            contextThreadLocal.set(new TransactionContextImpl(this::commit, this::rollback));
            updateState(TransactionStatus.ACTIVE);
        }

        TransactionContextImpl context = contextThreadLocal.get();
        context.incrementCounter();
        return context;
    }

    @Override
    public TransactionStatus getTransactionStatus() {
        TransactionContextImpl context = contextThreadLocal.get();

        if(context == null) {
            return TransactionStatus.INACTIVE;
        }

        return context.getStatus();
    }

    @Override
    public void commit() {
        TransactionContextImpl context = contextThreadLocal.get();
        context.decrementCounter();

        // TODO check initial state
        updateState(TransactionStatus.COMMIT);
        updateState(TransactionStatus.COMMIT_COMPLETE);
    }

    @Override
    public void rollback() {
        // TODO check initial state
        updateState(TransactionStatus.ROLLBACK);
        updateState(TransactionStatus.ROLLBACK_COMPLETE);
    }

    @Override
    public void onStatusChanged(StatusChangedListener statusChangedListener) {
        statusChangedListenerList.get().add(statusChangedListener);
    }

    private void updateState(TransactionStatus status) {
        contextThreadLocal.get().setStatus(status);
        statusChangedListenerList.get().forEach(s -> s.execute(status));
    }
}
