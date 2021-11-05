package org.eclipse.dataspaceconnector.ids.api.multipart;

import org.eclipse.dataspaceconnector.junit.launcher.EdcExtension;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferQuery;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferQueryResponse;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferService;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.iam.TokenResult;
import org.eclipse.dataspaceconnector.spi.iam.VerificationResult;
import org.eclipse.dataspaceconnector.spi.message.MessageContext;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcher;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.dataspaceconnector.spi.protocol.web.WebService;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transfer.store.TransferProcessStore;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.message.RemoteMessage;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.TransferProcess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@ExtendWith(EdcExtension.class)
public class MultipartControllerIntegrationTest {

    @BeforeEach
    protected void before(EdcExtension extension) {
        extension.registerSystemExtension(ServiceExtension.class, new IamFakeExtension());
    }

    @Test
    void test() {

    }

    private class IamFakeExtension implements ServiceExtension {

        @Override
        public Set<String> provides() {
            return Set.of(IdentityService.FEATURE, "edc:core:contract", "dataspaceconnector:transferprocessstore");
        }

        @Override
        public void initialize(ServiceExtensionContext context) {
            context.registerService(IdentityService.class, new FakeIdentityService());
            context.registerService(ContractOfferService.class, new FakeContractOfferService());
            context.registerService(TransferProcessStore.class, new FakeTransferProcessStore());
            context.registerService(RemoteMessageDispatcherRegistry.class, new FakeRemoteMessageDispatcherRegistry());
            context.registerService(WebService.class, new FakeWebService());
            context.registerService(AssetIndex.class, new FakeAssetIndex());
        }

        private class FakeWebService implements WebService {
            @Override
            public void registerController(Object controller) {

            }
        }

        private class FakeAssetIndex implements AssetIndex {
            @Override
            public Stream<Asset> queryAssets(AssetSelectorExpression expression) {
                return null;
            }

            @Override
            public Asset findById(String assetId) {
                return null;
            }
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

        private class FakeContractOfferService implements ContractOfferService {
            @Override
            public ContractOfferQueryResponse queryContractOffers(ContractOfferQuery contractOfferQuery) {
                return new ContractOfferQueryResponse(Stream.empty());
            }
        }

        private class FakeTransferProcessStore implements TransferProcessStore {
            @Override
            public TransferProcess find(String id) {
                return null;
            }

            @Override
            public @Nullable String processIdForTransferId(String id) {
                return null;
            }

            @Override
            public @NotNull List<TransferProcess> nextForState(int state, int max) {
                return null;
            }

            @Override
            public void create(TransferProcess process) {

            }

            @Override
            public void update(TransferProcess process) {

            }

            @Override
            public void delete(String processId) {

            }

            @Override
            public void createData(String processId, String key, Object data) {

            }

            @Override
            public void updateData(String processId, String key, Object data) {

            }

            @Override
            public void deleteData(String processId, String key) {

            }

            @Override
            public void deleteData(String processId, Set<String> keys) {

            }

            @Override
            public <T> T findData(Class<T> type, String processId, String resourceDefinitionId) {
                return null;
            }
        }

        private class FakeRemoteMessageDispatcherRegistry implements RemoteMessageDispatcherRegistry {

            @Override
            public void register(RemoteMessageDispatcher dispatcher) {

            }

            @Override
            public <T> CompletableFuture<T> send(Class<T> responseType, RemoteMessage message, MessageContext context) {
                return null;
            }
        }
    }
}
