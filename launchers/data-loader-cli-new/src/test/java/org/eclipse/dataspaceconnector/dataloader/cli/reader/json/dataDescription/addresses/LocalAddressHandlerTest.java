package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.dataDescription.addresses;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.eclipse.dataspaceconnector.dataloader.cli.types.addresses.Address;
import org.eclipse.dataspaceconnector.dataloader.cli.types.addresses.LocalAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class LocalAddressHandlerTest {
    private static final String NODE = "" +
            "{" +
            "   \"type\": \"file\"," +
            "   \"path\": \"/tmp/file1.txt\"" +
            "}";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private LocalAddressHandler localAddressHandler;

    @BeforeEach
    public void setup() {
        localAddressHandler = new LocalAddressHandler();
    }

    @Test
    public void testHandlesAddressContent() throws Exception {
        JsonNode node = OBJECT_MAPPER.readTree(NODE);

        Address address = localAddressHandler.handle(node);

        Assertions.assertThat(address)
                .isInstanceOfAny(LocalAddress.class);
        Assertions.assertThat(((LocalAddress) address).getPath())
                .isEqualTo(Path.of("/tmp/file1.txt"));
    }
}
