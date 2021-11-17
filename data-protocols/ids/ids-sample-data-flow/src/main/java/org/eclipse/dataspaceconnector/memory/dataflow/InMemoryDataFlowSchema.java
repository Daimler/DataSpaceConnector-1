package org.eclipse.dataspaceconnector.memory.dataflow;

import org.eclipse.dataspaceconnector.spi.types.domain.schema.Schema;
import org.eclipse.dataspaceconnector.spi.types.domain.schema.SchemaAttribute;

public class InMemoryDataFlowSchema extends Schema {

    public static final String TYPE = "in-memory-data-flow";
    public static final String ATTRIBUTE_KEY = "key";

    @Override
    protected void addAttributes() {
        attributes.add(new SchemaAttribute(ATTRIBUTE_KEY, true));
    }

    @Override
    public String getName() {
        return TYPE;
    }
}
