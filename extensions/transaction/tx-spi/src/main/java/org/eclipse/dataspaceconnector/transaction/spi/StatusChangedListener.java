package org.eclipse.dataspaceconnector.transaction.spi;

public interface StatusChangedListener {
    void execute(TransactionStatus status);
}
