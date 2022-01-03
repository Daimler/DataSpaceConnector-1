package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.UnexpectedValueException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.NodeHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.util.JsonUtil;

import java.util.List;

public final class NodeHandlerUtil {

    private static final String TYPE_FIELD = "type";

    public static <T extends NodeHandler> T findHandler(List<T> handlers, JsonNode node) throws ReaderException {

        String type = JsonUtil.getTextValue(node, TYPE_FIELD);

        T handler = handlers.stream()
                .filter(h -> h.getNodeType().equals(type))
                .findFirst()
                .orElse(null);

        if (handler == null) {
            String[] supportedTypes = handlers.stream()
                    .map(NodeHandler::getNodeType)
                    .toArray(String[]::new);
            throw new UnexpectedValueException(TYPE_FIELD, node.asText(), supportedTypes);
        }

        return handler;
    }
}
