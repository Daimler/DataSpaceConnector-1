package org.eclipse.dataspaceconnector.api.data.client.configuration;

import java.net.URL;

public class DataManagementApiClientSettings {
    private final URL serviceUrl;
    private final String apiKey;
    private final String apiKeyValue;

    public DataManagementApiClientSettings(URL serviceUrl, String apiKey, String apiKeyValue) {
        this.serviceUrl = serviceUrl;
        this.apiKey = apiKey;
        this.apiKeyValue = apiKeyValue;
    }

    public URL getServiceUrl() {
        return serviceUrl;
    }

    public String getApiKeyValue() {
        return apiKeyValue;
    }

    public String getApiKey() {
        return apiKey;
    }
}
