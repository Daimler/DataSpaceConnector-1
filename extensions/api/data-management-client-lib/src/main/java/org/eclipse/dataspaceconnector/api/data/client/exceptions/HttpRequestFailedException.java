package org.eclipse.dataspaceconnector.api.data.client.exceptions;

public class HttpRequestFailedException extends DataManagementApiClientException {
    private final int statusCode;

    public HttpRequestFailedException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
