package org.eclipse.dataspaceconnector.transaction.spi;

public enum TransactionStatus {
    NEW,
    ACTIVE,
    ROLLBACK,
    ROLLBACK_COMPLETE,
    COMMIT,
    COMMIT_COMPLETE,
}
