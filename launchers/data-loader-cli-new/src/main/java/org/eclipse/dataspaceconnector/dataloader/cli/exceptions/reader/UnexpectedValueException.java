package org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader;

public class UnexpectedValueException extends ReaderException {

    public UnexpectedValueException(String fieldName, String currentValue, String... expectedValues) {
        super(String.format("Invalid value in field '%s'. (was '%s', expected '%s').",
                fieldName,
                currentValue,
                String.join(", ", expectedValues)));
    }

}
