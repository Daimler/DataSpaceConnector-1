package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.dataDescription;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.dataspaceconnector.dataloader.cli.builder.DocumentBuilder;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.DocumentNodeHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.exception.NonTextualNodeException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.exception.NonValueNodeException;
import org.eclipse.dataspaceconnector.dataloader.cli.types.DataDescription;
import org.eclipse.dataspaceconnector.dataloader.cli.types.addresses.Address;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.eclipse.dataspaceconnector.dataloader.cli.reader.json.util.NodeHandlerUtil.findHandler;
import static org.eclipse.dataspaceconnector.dataloader.cli.util.JsonUtil.getJsonNode;

public class DataDescriptionHandler implements DocumentNodeHandler {

    private static final String NODE = "asset";
    private static final String ID_FIELD = "id";
    private static final String PROPERTIES_FIELD = "properties";
    private static final String ADDRESS_FIELD = "address";

    private final List<AddressHandler> addressHandlers;

    public DataDescriptionHandler(List<AddressHandler> addressHandlers) {
        this.addressHandlers = addressHandlers;
    }

    public String getNodeType() {
        return NODE;
    }

    public void handle(DocumentBuilder builder, JsonNode assetNode) throws ReaderException {
        String id = getJsonNode(assetNode, ID_FIELD).textValue();
        Map<String, String> properties = readProperties(assetNode);
        JsonNode addressNode = getJsonNode(assetNode, ADDRESS_FIELD);

        AddressHandler addressHandler = findHandler(addressHandlers, addressNode);
        Address address = addressHandler.handle(addressNode);

        DataDescription dataDescription = new DataDescription(id, properties, address);
        builder.with(dataDescription);
    }

    private Map<String, String> readProperties(JsonNode assetNode) throws ReaderException {
        Map<String, String> properties = new HashMap<>();
        JsonNode propertiesNode = getJsonNode(assetNode, PROPERTIES_FIELD);
        Iterator<Map.Entry<String, JsonNode>> fields = propertiesNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            JsonNode fieldNode = field.getValue();
            if (!fieldNode.isValueNode()) {
                throw new NonValueNodeException(PROPERTIES_FIELD);
            }
            if (!fieldNode.isTextual()) {
                throw new NonTextualNodeException(PROPERTIES_FIELD);
            }

            properties.put(field.getKey(), fieldNode.textValue());
        }

        return properties;
    }

}
