package org.eclipse.dataspaceconnector.datamgt.spi.exceptions;

public class IdentifierAlreadyExistsException extends Exception {
    private final String identifier;

    public IdentifierAlreadyExistsException(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
