package org.eclipse.dataspaceconnector.transaction.core;

import org.eclipse.dataspaceconnector.transaction.spi.TransactionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionContextImpl implements TransactionContext {

    private final AtomicInteger commitCounter = new AtomicInteger();

    private final List<Runnable> onBeforeCommit = new ArrayList<>();
    private final List<Runnable> onCommit = new ArrayList<>();
    private final List<Runnable> onAfterCommit = new ArrayList<>();
    private final List<Runnable> onBeforeRollback = new ArrayList<>();
    private final List<Runnable> onRollback = new ArrayList<>();
    private final List<Runnable> onAfterRollback = new ArrayList<>();

    @Override
    public void onBeforeCommit(Runnable run) {
        onBeforeCommit.add(run);
    }

    @Override
    public void onCommit(Runnable run) {
        onCommit.add(run);
    }

    @Override
    public void onAfterCommit(Runnable run) {
        onAfterCommit.add(run);
    }

    @Override
    public void onBeforeRollback(Runnable run) {
        onBeforeRollback.add(run);
    }

    @Override
    public void onRollback(Runnable run) {
        onRollback.add(run);
    }

    @Override
    public void onAfterRollback(Runnable run) {
        onAfterRollback.add(run);
    }

    @Override
    public void commit() {
        // TODO Check state
        if (commitCounter.get() == 1) {
            onBeforeCommit.forEach(Runnable::run);
            onCommit.forEach(Runnable::run);
            onAfterCommit.forEach(Runnable::run);
        }
    }

    @Override
    public void rollback() {
        onBeforeRollback.forEach(Runnable::run);
        onRollback.forEach(Runnable::run);
        onAfterRollback.forEach(Runnable::run);
    }

    public void incrementCounter() {
        commitCounter.incrementAndGet();
    }
}
