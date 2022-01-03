package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.selectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.eclipse.dataspaceconnector.dataloader.cli.types.selectors.PropertySelector;
import org.eclipse.dataspaceconnector.dataloader.cli.types.selectors.Selector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PropertySelectorHandlerTest {
    private static final String NODE = "" +
            "{" +
            "   \"type\": \"property\"," +
            "   \"key\": \"department\"," +
            "   \"value\": \"research\"" +
            "}";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private PropertySelectorHandler propertySelectorHandler;

    @BeforeEach
    public void setup() {
        propertySelectorHandler = new PropertySelectorHandler();
    }

    @Test
    public void testHandlesSelectorContent() throws Exception {
        JsonNode node = OBJECT_MAPPER.readTree(NODE);

        Selector selector = propertySelectorHandler.handle(node);

        Assertions.assertThat(selector)
                .isInstanceOfAny(PropertySelector.class);
        Assertions.assertThat(((PropertySelector) selector).getKey())
                .isEqualTo("department");
        Assertions.assertThat(((PropertySelector) selector).getValue())
                .isEqualTo("research");
    }
}
