package org.eclipse.dataspaceconnector.transaction.spi;

public enum TransactionStatus {
    INACTIVE, // no transaction planned
    NEW, // transaction has been initiated
    ACTIVE, // transaction is open
    ROLLBACK, // transaction is rolling back
    ROLLBACK_COMPLETE, // transaction rollback complete
    COMMIT, // transaction commited
    COMMIT_COMPLETE, // transaction commit complete
}
