package org.eclipse.dataspaceconnector.transaction.spi;

public interface TransactionManager {

    void onStatusChanged(StatusChangedListener statusChangedListener);

    TransactionContext beginTransaction();

    TransactionStatus getTransactionStatus();

    void commit();

    void rollback();
}
