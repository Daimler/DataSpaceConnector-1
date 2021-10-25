package org.eclipse.dataspaceconnector.ids.api.multipart.transform;

import de.fraunhofer.iais.eis.Resource;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.Nullable;

public class ResourceToAssetTransformer implements IdsTypeTransformer<Resource, Asset> {
    @Override
    public Class<Resource> getInputType() {
        return Resource.class;
    }

    @Override
    public Class<Asset> getOutputType() {
        return Asset.class;
    }

    @Override
    public @Nullable Asset transform(Resource object, TransformerContext context) {
        return null;
    }
}
