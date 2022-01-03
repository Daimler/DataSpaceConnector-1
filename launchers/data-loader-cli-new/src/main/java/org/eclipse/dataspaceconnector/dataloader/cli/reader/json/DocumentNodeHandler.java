package org.eclipse.dataspaceconnector.dataloader.cli.reader.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.dataspaceconnector.dataloader.cli.builder.DocumentBuilder;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;

public interface DocumentNodeHandler extends NodeHandler {
    void handle(DocumentBuilder builder, JsonNode node) throws ReaderException;
}
