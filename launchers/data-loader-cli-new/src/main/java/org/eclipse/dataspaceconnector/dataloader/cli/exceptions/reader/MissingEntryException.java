package org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader;

public class MissingEntryException extends ReaderException {
    public MissingEntryException(String element, String entryAsText) {
        super(String.format("Missing field '%s'. Entry %s.", element, entryAsText));
    }
}
