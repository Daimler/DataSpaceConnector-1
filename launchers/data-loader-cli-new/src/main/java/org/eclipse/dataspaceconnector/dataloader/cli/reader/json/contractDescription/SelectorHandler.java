package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.NodeHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.types.selectors.Selector;

public interface SelectorHandler extends NodeHandler {
    Selector handle(JsonNode node) throws ReaderException;
}
