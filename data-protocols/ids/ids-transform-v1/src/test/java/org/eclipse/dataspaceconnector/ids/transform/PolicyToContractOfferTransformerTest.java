package org.eclipse.dataspaceconnector.ids.transform;

import org.easymock.EasyMock;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.policy.model.Duty;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.policy.model.Prohibition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;

public class PolicyToContractOfferTransformerTest {
    private static final String ASSIGNER = "https://assigner.com/";
    private static final URI ASSIGNER_URI = URI.create("https://assigner.com/");

    // subject
    private PolicyToContractOfferTransformer policyToContractOfferTransformer;

    // mocks
    private Policy policy;
    private TransformerContext context;

    @BeforeEach
    void setUp() {
        policyToContractOfferTransformer = new PolicyToContractOfferTransformer();
        policy = EasyMock.createMock(Policy.class);
        context = EasyMock.createMock(TransformerContext.class);
    }

    @Test
    void testThrowsNullPointerExceptionForAll() {
        EasyMock.replay(policy, context);

        Assertions.assertThrows(NullPointerException.class, () -> {
            policyToContractOfferTransformer.transform(null, null);
        });
    }

    @Test
    void testThrowsNullPointerExceptionForContext() {
        EasyMock.replay(policy, context);

        Assertions.assertThrows(NullPointerException.class, () -> {
            policyToContractOfferTransformer.transform(policy, null);
        });
    }

    @Test
    void testReturnsNull() {
        EasyMock.replay(policy, context);

        var result = policyToContractOfferTransformer.transform(null, context);

        Assertions.assertNull(result);
    }

    @Test
    void testSuccessfulSimple() {
        // prepare

        Permission edcPermission = EasyMock.createMock(Permission.class);
        de.fraunhofer.iais.eis.Permission idsPermission = EasyMock.createMock(de.fraunhofer.iais.eis.Permission.class);
        Prohibition edcProhibition = EasyMock.createMock(Prohibition.class);
        de.fraunhofer.iais.eis.Prohibition idsProhibition = EasyMock.createMock(de.fraunhofer.iais.eis.Prohibition.class);
        Duty edcObligation = EasyMock.createMock(Duty.class);
        de.fraunhofer.iais.eis.Duty idsObligation = EasyMock.createMock(de.fraunhofer.iais.eis.Duty.class);

        EasyMock.expect(policy.getAssigner()).andReturn(ASSIGNER);
        EasyMock.expect(policy.getPermissions()).andReturn(Collections.singletonList(edcPermission));
        EasyMock.expect(policy.getProhibitions()).andReturn(Collections.singletonList(edcProhibition));
        EasyMock.expect(policy.getObligations()).andReturn(Collections.singletonList(edcObligation));

        EasyMock.expect(context.transform(EasyMock.eq(ASSIGNER), EasyMock.eq(URI.class))).andReturn(ASSIGNER_URI);
        EasyMock.expect(context.transform(EasyMock.anyObject(Permission.class), EasyMock.eq(de.fraunhofer.iais.eis.Permission.class))).andReturn(idsPermission);
        EasyMock.expect(context.transform(EasyMock.anyObject(Prohibition.class), EasyMock.eq(de.fraunhofer.iais.eis.Prohibition.class))).andReturn(idsProhibition);
        EasyMock.expect(context.transform(EasyMock.anyObject(Duty.class), EasyMock.eq(de.fraunhofer.iais.eis.Duty.class))).andReturn(idsObligation);

        context.reportProblem(EasyMock.anyString());
        EasyMock.expectLastCall().atLeastOnce();

        // record
        EasyMock.replay(policy, context);

        // invoke
        var result = policyToContractOfferTransformer.transform(policy, context);

        // verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(ASSIGNER_URI, result.getProvider());
        Assertions.assertEquals(1, result.getObligation().size());
        Assertions.assertEquals(idsObligation, result.getObligation().get(0));
        Assertions.assertEquals(1, result.getPermission().size());
        Assertions.assertEquals(idsPermission, result.getPermission().get(0));
        Assertions.assertEquals(1, result.getProhibition().size());
        Assertions.assertEquals(idsProhibition, result.getProhibition().get(0));
    }
}
