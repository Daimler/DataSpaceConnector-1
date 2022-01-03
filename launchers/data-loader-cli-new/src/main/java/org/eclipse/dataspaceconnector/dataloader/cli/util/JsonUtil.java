package org.eclipse.dataspaceconnector.dataloader.cli.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.MissingEntryException;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.exception.NonTextualNodeException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.exception.NonValueNodeException;

public final class JsonUtil {

    public static String getTextValue(JsonNode node, String fieldName) throws ReaderException {
        JsonNode valueNode = getJsonNode(node, fieldName);
        if (!valueNode.isValueNode()) {
            throw new NonValueNodeException(fieldName);
        }
        if (!valueNode.isTextual()) {
            throw new NonTextualNodeException(fieldName);
        }
        return valueNode.textValue();
    }

    public static JsonNode getJsonNode(JsonNode node, String fieldName) throws MissingEntryException {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode instanceof NullNode) {
            throw new MissingEntryException(fieldName, node.asText());
        }
        return fieldNode;
    }
}
