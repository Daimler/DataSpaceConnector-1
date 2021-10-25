package org.eclipse.dataspaceconnector.ids.api.multipart.transform;

import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.jetbrains.annotations.Nullable;

public class ResourceToRepresentationTransformer implements IdsTypeTransformer {
    @Override
    public Class getInputType() {
        return null;
    }

    @Override
    public Class getOutputType() {
        return null;
    }

    @Nullable
    @Override
    public Object transform(Object object, TransformerContext context) {
        return null;
    }
}
