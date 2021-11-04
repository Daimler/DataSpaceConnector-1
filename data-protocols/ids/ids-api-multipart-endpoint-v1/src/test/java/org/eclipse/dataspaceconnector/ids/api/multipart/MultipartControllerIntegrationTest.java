package org.eclipse.dataspaceconnector.ids.api.multipart;

import org.eclipse.dataspaceconnector.junit.launcher.EdcExtension;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.iam.TokenResult;
import org.eclipse.dataspaceconnector.spi.iam.VerificationResult;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;

@ExtendWith(EdcExtension.class)
public class MultipartControllerIntegrationTest {

    @BeforeEach
    protected void before(EdcExtension extension) {
        extension.registerSystemExtension(IamFakeExtension.class, new IamFakeExtension());
    }

    @Test
    void test() {

    }

    private class IamFakeExtension implements ServiceExtension {

        @Override
        public Set<String> provides() {
            return Set.of(IdentityService.FEATURE);
        }

        @Override
        public void initialize(ServiceExtensionContext context) {
            context.registerService(IdentityService.class, new FakeIdentityService());
        }

        private class FakeIdentityService implements IdentityService {
            @Override
            public TokenResult obtainClientCredentials(String scope) {
                return null;
            }

            @Override
            public VerificationResult verifyJwtToken(String token, String audience) {
                return new VerificationResult();
            }
        }

    }
}
