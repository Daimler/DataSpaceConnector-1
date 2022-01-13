package org.eclipse.dataspaceconnector.transaction.tx;

public interface TransactionContext {

    void commit();

    void rollback();

}
