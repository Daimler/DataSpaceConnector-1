package org.eclipse.dataspaceconnector.transaction.spi;

public interface TransactionContext {

    public void onBeforeCommit(Runnable run);

    public void onCommit(Runnable run);

    public void onAfterCommit(Runnable run);

    public void onBeforeRollback(Runnable run);

    public void onRollback(Runnable run);

    public void onAfterRollback(Runnable run);

    void commit();

    void rollback();
}
