package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.exception;

import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;

public class NonTextualNodeException extends ReaderException {

    public NonTextualNodeException(String fieldName) {
        super(String.format("Expected textual node(s) in field %s", fieldName));
    }
}