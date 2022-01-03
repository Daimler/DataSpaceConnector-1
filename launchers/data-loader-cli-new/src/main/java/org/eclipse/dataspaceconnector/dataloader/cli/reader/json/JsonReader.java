package org.eclipse.dataspaceconnector.dataloader.cli.reader.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.dataloader.cli.builder.DocumentBuilder;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;
import org.eclipse.dataspaceconnector.dataloader.cli.logger.Logger;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.Reader;
import org.eclipse.dataspaceconnector.dataloader.cli.types.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import static org.eclipse.dataspaceconnector.dataloader.cli.reader.json.util.NodeHandlerUtil.findHandler;

public class JsonReader implements Reader {

    private final ObjectMapper objectMapper;
    private final Logger logger;
    private final List<DocumentNodeHandler> nodeHandlers;

    public JsonReader(Logger logger, List<DocumentNodeHandler> nodeHandlers) {
        this.nodeHandlers = nodeHandlers;
        this.objectMapper = new ObjectMapper();
        this.logger = logger;
    }

    @Override
    public Document read(InputStream stream) throws IOException {
        JsonNode rootNode = objectMapper.readTree(stream);
        return readDocument(rootNode);
    }

    private Document readDocument(JsonNode documentNode) {

        DocumentBuilder documentBuilder = new DocumentBuilder();

        Iterator<JsonNode> documentElements = documentNode.elements();
        while (documentElements.hasNext()) {
            try {
                JsonNode documentChildNode = documentElements.next();
                readNode(documentBuilder, documentChildNode);
            } catch (ReaderException e) {
                logger.error(e.getReason());
            }
        }

        return documentBuilder.build();
    }

    private void readNode(DocumentBuilder documentBuilder, JsonNode documentChildNode) throws ReaderException {
        DocumentNodeHandler handler = findHandler(nodeHandlers, documentChildNode);
        handler.handle(documentBuilder, documentChildNode);
    }
}
