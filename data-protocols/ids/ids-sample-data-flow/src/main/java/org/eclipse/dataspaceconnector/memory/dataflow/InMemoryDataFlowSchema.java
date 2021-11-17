package org.eclipse.dataspaceconnector.memory.dataflow;

import org.eclipse.dataspaceconnector.spi.types.domain.schema.Schema;
import org.eclipse.dataspaceconnector.spi.types.domain.schema.SchemaAttribute;

public class InMemoryDataFlowSchema extends Schema {

    public static final String TYPE = "in-memory-data-flow";
    public static final String ATTRIBUTE_KEY = "key";
    public static final String ATTRIBUTE_CONNECTOR_ID = "connector-id";
    public static final String ATTRIBUTE_CONSUMER_URL = "consumer-url";
    public static final String ATTRIBUTE_CORRELATION_MESSAGE = "correlation-message";

    @Override
    protected void addAttributes() {
        attributes.add(new SchemaAttribute(ATTRIBUTE_KEY, true));
        attributes.add(new SchemaAttribute(ATTRIBUTE_CONNECTOR_ID, true));
        attributes.add(new SchemaAttribute(ATTRIBUTE_CORRELATION_MESSAGE, true));
        attributes.add(new SchemaAttribute(ATTRIBUTE_CONSUMER_URL, true));
    }

    @Override
    public String getName() {
        return TYPE;
    }
}
