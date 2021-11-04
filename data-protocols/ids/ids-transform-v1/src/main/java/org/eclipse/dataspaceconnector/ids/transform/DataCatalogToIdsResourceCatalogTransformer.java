package org.eclipse.dataspaceconnector.ids.transform;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.ids.spi.types.DataCatalog;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.OfferedAsset;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DataCatalogToIdsResourceCatalogTransformer implements IdsTypeTransformer<DataCatalog, ResourceCatalog> {

    @Override
    public Class<DataCatalog> getInputType() {
        return DataCatalog.class;
    }

    @Override
    public Class<ResourceCatalog> getOutputType() {
        return ResourceCatalog.class;
    }

    @Override
    public @Nullable ResourceCatalog transform(DataCatalog object, TransformerContext context) {
        Objects.requireNonNull(context);
        if (object == null) {
            return null;
        }

        IdsId idsId = IdsId.Builder.newInstance()
                .type(IdsType.CATALOG)
                .value(object.getId())
                .build();

        URI id = context.transform(idsId, URI.class);
        ResourceCatalogBuilder resourceCatalogBuilder;
        if (id == null) {
            // TODO problem?
            resourceCatalogBuilder = new ResourceCatalogBuilder();
        } else {
            resourceCatalogBuilder = new ResourceCatalogBuilder(id);
        }

        List<Resource> resources = new LinkedList<>();
        List<ContractOffer>  contractOffers = object.getContractOffers();
        if (contractOffers != null) {
            for (ContractOffer contractOffer : contractOffers) {
                for (OfferedAsset offeredAsset : contractOffer.getAssets()) {
                    Resource resource = context.transform(offeredAsset, Resource.class);
                    if (resource != null) {
                        resources.add(resource);
                    }
                }
            }
        }

        resourceCatalogBuilder._offeredResource_(new ArrayList<>(resources));

        return resourceCatalogBuilder.build();
    }
}
