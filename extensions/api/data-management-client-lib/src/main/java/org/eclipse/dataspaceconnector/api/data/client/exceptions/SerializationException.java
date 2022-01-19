package org.eclipse.dataspaceconnector.api.data.client.exceptions;

public class SerializationException extends DataManagementApiClientException {
    public SerializationException(Exception e) {
        super(e);
    }
}
