package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.exception;

import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;

public class NonValueNodeException extends ReaderException {

    public NonValueNodeException(String fieldName) {
        super(String.format("Expected value node(s) in field %s", fieldName));
    }
}
