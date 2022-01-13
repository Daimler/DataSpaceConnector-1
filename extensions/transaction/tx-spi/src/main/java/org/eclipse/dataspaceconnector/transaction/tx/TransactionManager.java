package org.eclipse.dataspaceconnector.transaction.tx;

public interface TransactionManager {

    TransactionContext beginTransaction();
}
