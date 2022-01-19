package org.eclipse.dataspaceconnector.kafka;

public class TransactionRollbackException extends RuntimeException {
    public TransactionRollbackException(String message) {
        super(message);
    }

    public TransactionRollbackException(Throwable cause) {
        super(cause);
    }

    public TransactionRollbackException(String message, Throwable cause) {
        super(message, cause);
    }
}