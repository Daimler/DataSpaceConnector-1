package org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader;

public class ReaderException extends Exception {
    private final String reason;

    public ReaderException(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
