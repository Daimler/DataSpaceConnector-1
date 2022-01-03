package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.exception;

import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;

public class NonArrayNodeException extends ReaderException {

    public NonArrayNodeException(String fieldName) {
        super(String.format("Expected array node in field %s", fieldName));
    }
}
