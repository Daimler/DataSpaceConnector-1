package org.eclipse.dataspaceconnector.ids.api.multipart;

import org.eclipse.dataspaceconnector.policy.model.Policy;
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
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transfer.store.TransferProcessStore;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.ContractOffer;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.OfferedAsset;
import org.eclipse.dataspaceconnector.spi.types.domain.message.RemoteMessage;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.TransferProcess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class IdsApiMultipartEndpointV1IntegrationTestServiceExtension implements ServiceExtension {
    private final List<Asset> assets = new LinkedList<>();

    @Override
    public Set<String> provides() {
        return Set.of("edc:iam", "edc:core:contract", "dataspaceconnector:transferprocessstore");
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        context.registerService(IdentityService.class, new FakeIdentityService());
        context.registerService(TransferProcessStore.class, new FakeTransferProcessStore());
        context.registerService(RemoteMessageDispatcherRegistry.class, new FakeRemoteMessageDispatcherRegistry());
        context.registerService(AssetIndex.class, new FakeAssetIndex(assets));
        context.registerService(ContractOfferService.class, new FakeContractOfferService(assets));
    }

    private static class FakeIdentityService implements IdentityService {
        @Override
        public TokenResult obtainClientCredentials(String scope) {
            return TokenResult.Builder.newInstance().build();
        }

        @Override
        public VerificationResult verifyJwtToken(String token, String audience) {
            return new VerificationResult();
        }
    }


    private static class FakeAssetIndex implements AssetIndex {
        private final List<Asset> assets;

        private FakeAssetIndex(List<Asset> assets) {
            this.assets = Objects.requireNonNull(assets);
        }

        @Override
        public Stream<Asset> queryAssets(AssetSelectorExpression expression) {
            return assets.stream();
        }

        @Override
        public Asset findById(String assetId) {
            return assets.stream().filter(a -> a.getId().equals(assetId)).findFirst().orElse(null);
        }
    }

    private static class FakeContractOfferService implements ContractOfferService {
        private final List<Asset> assets;

        private FakeContractOfferService(List<Asset> assets) {
            this.assets = assets;
        }

        @Override
        public ContractOfferQueryResponse queryContractOffers(ContractOfferQuery contractOfferQuery) {
            List<ContractOffer> contractOffers = assets.stream()
                    .map(a -> OfferedAsset.Builder.newInstance()
                            .asset(a)
                            .policy(Policy.Builder.newInstance().build())
                            .build())
                    .map(Collections::singletonList)
                    .map(oa -> ContractOffer.Builder.newInstance()
                            .assets(oa).build())
                    .collect(Collectors.toList());
            return new ContractOfferQueryResponse(contractOffers.stream());
        }
    }

    private static class FakeTransferProcessStore implements TransferProcessStore {
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

    private static class FakeRemoteMessageDispatcherRegistry implements RemoteMessageDispatcherRegistry {

        @Override
        public void register(RemoteMessageDispatcher dispatcher) {
        }

        @Override
        public <T> CompletableFuture<T> send(Class<T> responseType, RemoteMessage message, MessageContext context) {
            return null;
        }
    }
}
