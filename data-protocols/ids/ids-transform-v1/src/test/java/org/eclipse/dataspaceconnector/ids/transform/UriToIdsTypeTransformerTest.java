package org.eclipse.dataspaceconnector.ids.transform;

import org.easymock.EasyMock;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UriToIdsTypeTransformerTest {

    private static final IdsType IDS_ID_TYPE = IdsType.ARTIFACT;
    private static final String IDS_ID_VALUE = "32d39d70-68f7-44f3-b8b2-27550f2081f4";
    private static final java.net.URI URI = java.net.URI.create("urn:artifact:32d39d70-68f7-44f3-b8b2-27550f2081f4");

    // subject
    UriToIdsTypeTransformer uriToIdsTypeTransformer;

    // mocks
    private TransformerContext context;

    @BeforeEach
    public void setup() {
        uriToIdsTypeTransformer = new UriToIdsTypeTransformer();
        context = EasyMock.createMock(TransformerContext.class);
    }

    @Test
    void testThrowsNullPointerExceptionForAll() {
        EasyMock.replay(context);

        Assertions.assertThrows(NullPointerException.class, () -> {
            uriToIdsTypeTransformer.transform(null, null);
        });
    }

    @Test
    void testThrowsNullPointerExceptionForContext() {
        EasyMock.replay(context);

        Assertions.assertThrows(NullPointerException.class, () -> {
            uriToIdsTypeTransformer.transform(URI, null);
        });
    }

    @Test
    void testReturnsNull() {
        EasyMock.replay(context);

        var result = uriToIdsTypeTransformer.transform(null, context);

        Assertions.assertNull(result);
    }

    @Test
    void testSuccessfulSimple() {
        // record
        EasyMock.replay(context);

        // invoke
        var result = uriToIdsTypeTransformer.transform(URI, context);

        // verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(IDS_ID_TYPE, result);
    }
}
