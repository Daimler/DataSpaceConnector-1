package org.eclipse.dataspaceconnector.api.data.client.exceptions;

import java.io.IOException;

public class DataManagementApiClientIOException extends DataManagementApiClientException {

    public DataManagementApiClientIOException(IOException e) {
        super(e);
    }
}
