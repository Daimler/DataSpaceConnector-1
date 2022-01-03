package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.constraints;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.ConstraintHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.types.constraints.Constraint;
import org.eclipse.dataspaceconnector.dataloader.cli.types.constraints.Event;

import static org.eclipse.dataspaceconnector.dataloader.cli.util.JsonUtil.getTextValue;

public class EventHandler implements ConstraintHandler {
    private static final String NODE = "event";
    private static final String NAME_FIELD = "name";

    @Override
    public String getNodeType() {
        return NODE;
    }

    @Override
    public Constraint handle(JsonNode node) throws ReaderException {
        String name = getTextValue(node, NAME_FIELD);
        return new Event(name);
    }
}
