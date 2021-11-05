package org.eclipse.dataspaceconnector.ids.api.multipart;

import org.eclipse.dataspaceconnector.ids.api.multipart.controller.MultipartController;
import org.eclipse.dataspaceconnector.ids.transform.IdsTransformServiceExtension;
import org.eclipse.dataspaceconnector.junit.launcher.EdcExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

abstract class AbstractMultipartControllerIntegrationTest {

    @BeforeEach
    void setProps() {
        for (Map.Entry<String, String> entry : getSystemProperties().entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }

    @BeforeEach
    protected void before(EdcExtension extension) {
        extension.registerSystemExtension(ServiceExtension.class, new IdsTransformServiceExtension());
        extension.registerSystemExtension(ServiceExtension.class, new IdsMultipartApiServiceExtension());
        extension.registerSystemExtension(ServiceExtension.class, new IdsApiMultipartEndpointV1IntegrationTestServiceExtension());
    }

    @AfterEach
    void unsetProps() {
        for (String key : getSystemProperties().keySet()) {
            System.clearProperty(key);
        }
    }

    protected int getPort() {
        return 8181;
    }

    protected String getUrl() {
        return String.format("http://localhost:%s/api%s", getPort(), MultipartController.PATH);
    }

    protected abstract Map<String, String> getSystemProperties();
}
