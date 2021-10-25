package org.eclipse.dataspaceconnector.ids.api.multipart.transform;

import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerRegistry;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AssetToResourceTransformer implements IdsTypeTransformer<Asset, Resource> {
    private final TransformerRegistry transformerRegistry;

    public AssetToResourceTransformer(TransformerRegistry transformerRegistry) {
        this.transformerRegistry = Objects.requireNonNull(transformerRegistry);
    }

    @Override
    public Class<Asset> getInputType() { return Asset.class;}

    @Override
    public Class<Resource> getOutputType() { return Resource.class; }

    @Override
    public @Nullable Resource transform(Asset object, TransformerContext context) {
        var result = transformerRegistry.transform(object,Representation.class);
        if (result.hasProblems()) {
            // TODO: this is ugly; rework it
            throw new EdcException(result.getProblems().toString());
        }
        Representation representation = result.getOutput();
        ResourceBuilder resourceBuilder = new ResourceBuilder();
        resourceBuilder._representation_(new ArrayList<>(Collections.singletonList(representation)));
        return resourceBuilder.build();
    }
}
