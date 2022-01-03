package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.constraints;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.ConstraintHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.types.constraints.Constraint;
import org.eclipse.dataspaceconnector.dataloader.cli.types.constraints.GeoLocation;

import static org.eclipse.dataspaceconnector.dataloader.cli.util.JsonUtil.getTextValue;

public class GeoLocationHandler implements ConstraintHandler {

    private static final String NODE = "geo-location";
    private static final String LOCATION_FIELD = "location";

    @Override
    public String getNodeType() {
        return NODE;
    }

    @Override
    public Constraint handle(JsonNode node) throws ReaderException {
        String location = getTextValue(node, LOCATION_FIELD);
        return new GeoLocation(location);
    }
}
