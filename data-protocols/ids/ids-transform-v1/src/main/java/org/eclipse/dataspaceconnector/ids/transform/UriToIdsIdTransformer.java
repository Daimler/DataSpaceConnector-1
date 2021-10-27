package org.eclipse.dataspaceconnector.ids.transform;

import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsIdParser;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public class UriToIdsIdTransformer implements IdsTypeTransformer<URI, IdsId> {
    @Override
    public Class<URI> getInputType() {
        return URI.class;
    }

    @Override
    public Class<IdsId> getOutputType() {
        return IdsId.class;
    }

    @Override
    public @Nullable IdsId transform(URI object, TransformerContext context) {
        if (object == null) {
            return null;
        }
        try {
            return IdsIdParser.parse(object.getScheme() + IdsIdParser.DELIMITER + object.getSchemeSpecificPart());
        } catch (IllegalArgumentException e) {
            context.reportProblem(String.format("Could not transform URI to IdsId: %s", e.getMessage()));
        }
        return null;
    }
}
