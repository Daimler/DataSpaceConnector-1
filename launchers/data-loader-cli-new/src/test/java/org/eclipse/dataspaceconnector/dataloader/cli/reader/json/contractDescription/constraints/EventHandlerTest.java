package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.constraints;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.eclipse.dataspaceconnector.dataloader.cli.types.constraints.Constraint;
import org.eclipse.dataspaceconnector.dataloader.cli.types.constraints.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EventHandlerTest {

    private static final String NODE = "" +
            "{" +
            "   \"type\": \"event\",\n" +
            "   \"name\": \"quality-alert\"\n" +
            "}";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private EventHandler eventHandler;

    @BeforeEach
    public void setup() {
        eventHandler = new EventHandler();
    }

    @Test
    public void testHandlesConstraintContent() throws Exception {
        JsonNode node = OBJECT_MAPPER.readTree(NODE);

        Constraint constraint = eventHandler.handle(node);

        Assertions.assertThat(constraint)
                .isInstanceOfAny(Event.class);
        Assertions.assertThat(((Event) constraint).getName())
                .isEqualTo("quality-alert");
    }
}
