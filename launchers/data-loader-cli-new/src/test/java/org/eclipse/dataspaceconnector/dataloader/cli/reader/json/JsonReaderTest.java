package org.eclipse.dataspaceconnector.dataloader.cli.reader.json;

import org.assertj.core.api.Assertions;
import org.eclipse.dataspaceconnector.dataloader.cli.logger.Logger;
import org.eclipse.dataspaceconnector.dataloader.cli.resources.ResourcesReader;
import org.eclipse.dataspaceconnector.dataloader.cli.types.Document;
import org.eclipse.dataspaceconnector.dataloader.cli.types.addresses.LocalAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

public class JsonReaderTest {

    private JsonReader jsonReader;

    @BeforeEach
    public void setup() {
        Logger logger = Mockito.mock(Logger.class);
        JsonReaderFactory jsonReaderFactory = new JsonReaderFactory();
        jsonReader = jsonReaderFactory.create(logger);
    }

    @Test
    public void testReaderComponents() throws IOException {
        // prepare
        ResourcesReader resourcesReader = new ResourcesReader();
        InputStream exampleJson = resourcesReader.readExampleJson();

        // invoke
        Document document = jsonReader.read(exampleJson);

        // verify
        Assertions.assertThat(document.getDataDescriptions())
                .isNotNull()
                .size()
                .isEqualTo(1);

        Assertions.assertThat(document.getDataDescriptions().get(0).getId())
                .isEqualTo("asset-1");

        Assertions.assertThat(document.getDataDescriptions().get(0).getProperties())
                .isNotNull()
                .contains(Map.entry("department", "research"))
                .size()
                .isEqualTo(1);

        Assertions.assertThat(document.getDataDescriptions().get(0).getAddress())
                .isInstanceOf(LocalAddress.class);

        Assertions.assertThat(((LocalAddress) document.getDataDescriptions().get(0).getAddress()).getPath())
                .isEqualTo(Path.of("/tmp/file1.txt"));
    }
}
