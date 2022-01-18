package org.eclipse.dataspaceconnector.transaction.spi;

public interface TransactionManager {

    void onStatusChanged(StatusChangedListener statusChangedListener);

    TransactionContext beginTransaction();

    void commit();

    void rollback();
}
