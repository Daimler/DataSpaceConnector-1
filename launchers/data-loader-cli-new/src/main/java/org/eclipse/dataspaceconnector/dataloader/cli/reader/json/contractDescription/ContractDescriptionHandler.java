package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.dataspaceconnector.dataloader.cli.builder.DocumentBuilder;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.DocumentNodeHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.exception.NonArrayNodeException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.util.NodeHandlerUtil;
import org.eclipse.dataspaceconnector.dataloader.cli.types.ContractDescription;
import org.eclipse.dataspaceconnector.dataloader.cli.types.constraints.Constraint;
import org.eclipse.dataspaceconnector.dataloader.cli.types.selectors.Selector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.eclipse.dataspaceconnector.dataloader.cli.util.JsonUtil.getJsonNode;
import static org.eclipse.dataspaceconnector.dataloader.cli.util.JsonUtil.getTextValue;

public class ContractDescriptionHandler implements DocumentNodeHandler {

    private static final String NODE = "contract-definition";

    private static final String ID_FIELD = "id";
    private static final String CONSTRAINTS_FIELD = "constraints";
    private static final String SELECTORS_FIELD = "selectors";

    private final List<ConstraintHandler> constraintHandlers;
    private final List<SelectorHandler> selectorHandlers;

    public ContractDescriptionHandler(List<ConstraintHandler> constraintHandlers, List<SelectorHandler> selectorHandlers) {
        this.constraintHandlers = constraintHandlers;
        this.selectorHandlers = selectorHandlers;
    }

    @Override
    public String getNodeType() {
        return NODE;
    }

    @Override
    public void handle(DocumentBuilder builder, JsonNode node) throws ReaderException {
        String id = getTextValue(node, ID_FIELD);
        List<Constraint> constraints = readConstraints(node);
        List<Selector> selectors = readSelectors(node);

        ContractDescription contractDescription = new ContractDescription(id, selectors, constraints);
        builder.with(contractDescription);
    }

    private List<Constraint> readConstraints(JsonNode contractDescriptionNode) throws ReaderException {
        List<Constraint> constraints = new ArrayList<>();
        JsonNode constraintsNode = getJsonNode(contractDescriptionNode, CONSTRAINTS_FIELD);

        if (!constraintsNode.isArray()) {
            throw new NonArrayNodeException(CONSTRAINTS_FIELD);
        }

        Iterator<JsonNode> node = constraintsNode.elements();

        while (node.hasNext()) {
            JsonNode constraintNode = node.next();
            ConstraintHandler constraintHandler = NodeHandlerUtil.findHandler(constraintHandlers, constraintNode);
            Constraint constraint = constraintHandler.handle(constraintNode);
            constraints.add(constraint);
        }

        return constraints;
    }

    private List<Selector> readSelectors(JsonNode contractDescriptionNode) throws ReaderException {
        List<Selector> selectors = new ArrayList<>();
        JsonNode selectorsNode = getJsonNode(contractDescriptionNode, SELECTORS_FIELD);

        if (!selectorsNode.isArray()) {
            throw new NonArrayNodeException(SELECTORS_FIELD);
        }

        Iterator<JsonNode> node = selectorsNode.elements();

        while (node.hasNext()) {
            JsonNode selectorNode = node.next();
            SelectorHandler selectorHandler = NodeHandlerUtil.findHandler(selectorHandlers, selectorNode);
            Selector selector = selectorHandler.handle(selectorNode);
            selectors.add(selector);
        }

        return selectors;
    }
}
