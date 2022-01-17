package org.eclipse.dataspaceconnector.transaction.spi;

public interface TransactionManager {
    TransactionContext beginTransaction();
}
