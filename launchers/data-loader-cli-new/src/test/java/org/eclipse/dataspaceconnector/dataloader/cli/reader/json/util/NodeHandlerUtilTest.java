package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.NodeHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class NodeHandlerUtilTest {

    private static final String NODE = "{ \"type\": \"asset\" }";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void testHandlesDescriptionContent() throws Exception {
        JsonNode node = OBJECT_MAPPER.readTree(NODE);

        List<NodeHandler> handlers = getHandlers("foo", "bar", "asset");
        NodeHandler handler = NodeHandlerUtil.findHandler(handlers, node);

        Assertions.assertThat(handler.getNodeType())
                .isEqualTo("asset");
    }

    private List<NodeHandler> getHandlers(String... types) {
        List<NodeHandler> nodeHandlers = new ArrayList<>();
        for (String type : types) {
            nodeHandlers.add(() -> type);
        }

        return nodeHandlers;
    }

}
