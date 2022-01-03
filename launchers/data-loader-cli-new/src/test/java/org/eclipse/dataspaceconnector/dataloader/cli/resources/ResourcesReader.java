package org.eclipse.dataspaceconnector.dataloader.cli.resources;

import java.io.InputStream;

public class ResourcesReader {
    private static final String EXAMPLE_JSON = "example.json";

    public InputStream readExampleJson() {
        return readContent(EXAMPLE_JSON);
    }

    private static InputStream readContent(String resourceName) {
        return ResourcesReader.class.getClassLoader().getResourceAsStream(resourceName);
    }
}
