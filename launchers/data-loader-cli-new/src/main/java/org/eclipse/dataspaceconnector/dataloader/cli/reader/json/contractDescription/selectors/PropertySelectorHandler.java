package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.selectors;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.SelectorHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.types.selectors.Selector;

import static org.eclipse.dataspaceconnector.dataloader.cli.util.JsonUtil.getTextValue;

public class PropertySelectorHandler implements SelectorHandler {

    private static final String NODE = "property";
    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";

    @Override
    public String getNodeType() {
        return NODE;
    }

    @Override
    public Selector handle(JsonNode node) throws ReaderException {
        String key = getTextValue(node, KEY_FIELD);
        String value = getTextValue(node, VALUE_FIELD);
        return new org.eclipse.dataspaceconnector.dataloader.cli.types.selectors.PropertySelector(key, value);
    }
}
