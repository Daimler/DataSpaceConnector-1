package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.dataDescription;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.NodeHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.types.addresses.Address;

public interface AddressHandler extends NodeHandler {
    Address handle(JsonNode node) throws ReaderException;
}
