package org.eclipse.dataspaceconnector.transaction.spi;

public interface TransactionContext {

    TransactionStatus getStatus();

    void commit();

    void rollback();
}
