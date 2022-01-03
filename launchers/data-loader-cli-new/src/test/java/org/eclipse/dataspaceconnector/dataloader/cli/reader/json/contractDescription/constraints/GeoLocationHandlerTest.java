package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.constraints;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.eclipse.dataspaceconnector.dataloader.cli.types.constraints.Constraint;
import org.eclipse.dataspaceconnector.dataloader.cli.types.constraints.GeoLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeoLocationHandlerTest {
    private static final String NODE = "" +
            "{" +
            "   \"type\": \"geo-location\",\n" +
            "   \"location\": \"eu\"\n" +
            "}";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private GeoLocationHandler geoLocationHandler;

    @BeforeEach
    public void setup() {
        geoLocationHandler = new GeoLocationHandler();
    }

    @Test
    public void testHandlesConstraintContent() throws Exception {
        JsonNode node = OBJECT_MAPPER.readTree(NODE);

        Constraint constraint = geoLocationHandler.handle(node);

        Assertions.assertThat(constraint)
                .isInstanceOfAny(GeoLocation.class);
        Assertions.assertThat(((GeoLocation) constraint).getLocation())
                .isEqualTo("eu");
    }
}
