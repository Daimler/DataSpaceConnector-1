package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.dataDescription;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.eclipse.dataspaceconnector.dataloader.cli.builder.DocumentBuilder;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;
import org.eclipse.dataspaceconnector.dataloader.cli.types.DataDescription;
import org.eclipse.dataspaceconnector.dataloader.cli.types.Document;
import org.eclipse.dataspaceconnector.dataloader.cli.types.addresses.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Map;

public class DataDescriptionHandlerTest {
    private static final String NODE = "" +
            "{" +
            "    \"type\": \"asset\"," +
            "    \"id\": \"asset-1\"," +
            "    \"properties\": {" +
            "      \"foo\": \"bar\"" +
            "    }," +
            "    \"address\": {" +
            "      \"type\": \"foo\"" +
            "    }" +
            "}";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private DataDescriptionHandler dataDescriptionHandler;

    // mocks
    private Address address;

    @BeforeEach
    public void setup() throws ReaderException {
        address = Mockito.mock(Address.class);
        AddressHandler addressHandler = Mockito.mock(AddressHandler.class);

        Mockito.when(addressHandler.handle(Mockito.any()))
                .thenReturn(address);

        Mockito.when(addressHandler.getNodeType())
                .thenReturn("foo");

        dataDescriptionHandler = new DataDescriptionHandler(
                Collections.singletonList(addressHandler));
    }

    @Test
    public void testHandlesDescriptionContent() throws Exception {
        JsonNode node = OBJECT_MAPPER.readTree(NODE);
        DocumentBuilder documentBuilder = new DocumentBuilder();

        dataDescriptionHandler.handle(documentBuilder, node);

        Document document = documentBuilder.build();
        DataDescription contractDescription = document.getDataDescriptions().get(0);

        Assertions.assertThat(contractDescription.getId())
                .isEqualTo("asset-1");
        Assertions.assertThat(contractDescription.getProperties())
                .contains(Map.entry("foo", "bar"));
        Assertions.assertThat(contractDescription.getAddress())
                .isEqualTo(address);
    }
}
