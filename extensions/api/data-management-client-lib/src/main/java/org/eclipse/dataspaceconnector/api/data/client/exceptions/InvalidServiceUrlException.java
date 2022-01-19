package org.eclipse.dataspaceconnector.api.data.client.exceptions;

import java.net.URL;

public class InvalidServiceUrlException extends DataManagementApiClientException {
    private final URL serviceUrl;

    public InvalidServiceUrlException(URL serviceUrl, Exception e) {
        super(e);
        this.serviceUrl = serviceUrl;
    }

    public URL getServiceUrl() {
        return serviceUrl;
    }
}
