package org.eclipse.dataspaceconnector.ids.transform;

import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import org.easymock.EasyMock;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.OfferedAsset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

public class OfferedAssetToResourceTransformerTest {
    private static final String ASSET_ID = "asset_id";
    private static final URI RESOURCE_ID_URI = URI.create("urn:resource:1");

    // subject
    private OfferedAssetToResourceTransformer offeredAssetToResourceTransformer;

    // mocks
    private OfferedAsset offeredAsset;
    private Asset asset;
    private Policy policy;
    private TransformerContext context;

    @BeforeEach
    void setUp() {
        offeredAssetToResourceTransformer = new OfferedAssetToResourceTransformer();
        offeredAsset = EasyMock.createMock(OfferedAsset.class);
        asset = EasyMock.createMock(Asset.class);
        policy = EasyMock.createMock(Policy.class);
        context = EasyMock.createMock(TransformerContext.class);

        EasyMock.expect(asset.getId()).andReturn(ASSET_ID).anyTimes();
        EasyMock.expect(offeredAsset.getAsset()).andReturn(asset).anyTimes();
        EasyMock.expect(offeredAsset.getPolicy()).andReturn(policy).anyTimes();
    }


    @Test
    void testThrowsNullPointerExceptionForAll() {
        EasyMock.replay(asset, offeredAsset, policy, context);

        Assertions.assertThrows(NullPointerException.class, () -> {
            offeredAssetToResourceTransformer.transform(null, null);
        });
    }

    @Test
    void testThrowsNullPointerExceptionForContext() {
        EasyMock.replay(asset, offeredAsset, policy, context);


        Assertions.assertThrows(NullPointerException.class, () -> {
            offeredAssetToResourceTransformer.transform(offeredAsset, null);
        });
    }

    @Test
    void testReturnsNull() {
        EasyMock.replay(asset, offeredAsset, policy, context);


        var result = offeredAssetToResourceTransformer.transform(null, context);

        Assertions.assertNull(result);
    }

    @Test
    void testSuccessfulSimple() {
        // prepare

        var representation = new RepresentationBuilder().build();
        EasyMock.expect(context.transform(EasyMock.anyObject(Asset.class), EasyMock.eq(Representation.class))).andReturn(representation);

        var contractOffer = new ContractOfferBuilder().build();
        EasyMock.expect(context.transform(EasyMock.anyObject(Policy.class), EasyMock.eq(ContractOffer.class))).andReturn(contractOffer);

        EasyMock.expect(context.transform(EasyMock.anyObject(IdsId.class), EasyMock.eq(URI.class))).andReturn(RESOURCE_ID_URI);

        context.reportProblem(EasyMock.anyString());
        EasyMock.expectLastCall().atLeastOnce();

        // record
        EasyMock.replay(asset, offeredAsset, policy, context);

        // invoke
        var result = offeredAssetToResourceTransformer.transform(offeredAsset, context);

        // verify
        Assertions.assertNotNull(result);
        // verify representation
        Assertions.assertNotNull(result.getRepresentation());
        Assertions.assertEquals(1, result.getRepresentation().size());
        Assertions.assertEquals(representation, result.getRepresentation().get(0));

        // verify contract offer
        Assertions.assertNotNull(result.getContractOffer());
        Assertions.assertEquals(1, result.getContractOffer().size());
        Assertions.assertEquals(contractOffer, result.getContractOffer().get(0));
    }
}
