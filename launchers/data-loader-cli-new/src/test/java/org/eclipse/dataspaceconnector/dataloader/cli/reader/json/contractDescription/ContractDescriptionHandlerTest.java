package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.eclipse.dataspaceconnector.dataloader.cli.builder.DocumentBuilder;
import org.eclipse.dataspaceconnector.dataloader.cli.types.ContractDescription;
import org.eclipse.dataspaceconnector.dataloader.cli.types.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class ContractDescriptionHandlerTest {

    private static final String NODE = "" +
            "{" +
            "    \"type\": \"contract-definition\"," +
            "    \"id\": \"cd-1\"," +
            "    \"selectors\": []," +
            "    \"constraints\": []" +
            "  }";


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ContractDescriptionHandler contractDescriptionHandler;

    @BeforeEach
    public void setup() {
        contractDescriptionHandler = new ContractDescriptionHandler(
                Collections.emptyList(),
                Collections.emptyList());
    }

    @Test
    public void testHandlesDescriptionContent() throws Exception {
        JsonNode node = OBJECT_MAPPER.readTree(NODE);
        DocumentBuilder documentBuilder = new DocumentBuilder();

        contractDescriptionHandler.handle(documentBuilder, node);

        Document document = documentBuilder.build();
        ContractDescription contractDescription = document.getContractDescriptions().get(0);

        Assertions.assertThat(contractDescription.getId())
                .isEqualTo("cd-1");
    }
}
