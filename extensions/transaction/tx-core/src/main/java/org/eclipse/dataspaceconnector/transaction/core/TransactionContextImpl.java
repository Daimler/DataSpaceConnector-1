package org.eclipse.dataspaceconnector.transaction.core;

import org.eclipse.dataspaceconnector.transaction.spi.TransactionContext;
import org.eclipse.dataspaceconnector.transaction.spi.TransactionStatus;

import java.util.concurrent.atomic.AtomicInteger;

public class TransactionContextImpl implements TransactionContext {

    private final AtomicInteger commitCounter = new AtomicInteger();

    private TransactionStatus status;
    private final Runnable commitCallback;
    private final Runnable rollbackCallback;

    public TransactionContextImpl(Runnable commitCallback, Runnable rollbackCallback) {
        this.status = TransactionStatus.NEW;
        this.commitCallback = commitCallback;
        this.rollbackCallback = rollbackCallback;
    }

    @Override
    public void commit() {
        if (status != TransactionStatus.ACTIVE) {
            throw new IllegalStateException();
        }
        if (commitCounter.get() <= 1) {
            commitCallback.run();
        }
    }

    @Override
    public void rollback() {
        if (status != TransactionStatus.ACTIVE) {
            throw new IllegalStateException();
        }
        rollbackCallback.run();
    }

    @Override
    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public void incrementCounter() {
        commitCounter.incrementAndGet();
    }

    public void decrementCounter() {
        commitCounter.decrementAndGet();
    }

}
