package org.eclipse.dataspaceconnector.ids.api.multipart.handler;

import de.fraunhofer.iais.eis.ArtifactRequestMessage;
import de.fraunhofer.iais.eis.Message;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartRequest;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartResponse;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerRegistry;
import org.eclipse.dataspaceconnector.spi.asset.DataAddressResolver;
import org.eclipse.dataspaceconnector.spi.iam.VerificationResult;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.transfer.store.TransferProcessStore;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataRequest;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.TransferProcess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import static org.eclipse.dataspaceconnector.ids.api.multipart.util.RejectionMessageUtil.badParameters;

public class ArtifactRequestHandler implements Handler {

    private final DataAddressResolver dataAddressResolver;
    private final TransformerRegistry transformerRegistry;
    private final TransferProcessStore transferProcessStore;
    private final String connectorId;
    private final Monitor monitor;

    public ArtifactRequestHandler(
            @NotNull Monitor monitor,
            @NotNull String connectorId,
            @NotNull TransformerRegistry transformerRegistry,
            @NotNull TransferProcessStore transferProcessStore,
            @NotNull DataAddressResolver dataAddressResolver) {
        this.monitor = Objects.requireNonNull(monitor);
        this.connectorId = Objects.requireNonNull(connectorId);
        this.transformerRegistry = Objects.requireNonNull(transformerRegistry);
        this.transferProcessStore = Objects.requireNonNull(transferProcessStore);
        this.dataAddressResolver = Objects.requireNonNull(dataAddressResolver);
    }

    @Override
    public boolean canHandle(@NotNull MultipartRequest multipartRequest) {
        Objects.requireNonNull(multipartRequest);

        return multipartRequest.getHeader() instanceof ArtifactRequestMessage;
    }

    @Override
    public @Nullable MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest, @NotNull VerificationResult verificationResult) {
        Objects.requireNonNull(multipartRequest);
        Objects.requireNonNull(verificationResult);

        ArtifactRequestMessage artifactRequestMessage = (ArtifactRequestMessage) multipartRequest.getHeader();

        URI requestedArtifact = artifactRequestMessage.getRequestedArtifact();
        IdsId idsId = null;
        if (requestedArtifact != null) {
            var result = transformerRegistry.transform(requestedArtifact, IdsId.class);
            if (result.hasProblems() || (idsId = result.getOutput()) == null) {
                String message = String.format("Could not transform URI to IdsId: [%s]", String.join(", ", result.getProblems()));
                monitor.warning(message);
                return createBadParametersErrorMultipartResponse(artifactRequestMessage);
            }
        }

        if (idsId == null || idsId.getType() != IdsType.ARTIFACT) {
            return createBadParametersErrorMultipartResponse(artifactRequestMessage);
        }

        String assetId = idsId.getValue();
        DataAddress dataAddress = dataAddressResolver.resolveForAsset(assetId);
        DataRequest dataRequest = DataRequest.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .asset(null) // TODO remove asset here
                .protocol("ids-sample") // until it's possible to define a data transfer method in IDS, use ids-sample
                .dataDestination(dataAddress)
                .connectorId(connectorId)
                .connectorAddress(artifactRequestMessage.getIssuerConnector().toString()) // TODO use something else
                .build();

        TransferProcess transferProcess = TransferProcess.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .type(TransferProcess.Type.PROVIDER)
                .dataRequest(dataRequest)
                .build();

        transferProcessStore.create(transferProcess);

        return MultipartResponse.Builder.newInstance()
                .header(ResponseMessageUtil.createDummyResponse(connectorId, artifactRequestMessage))
                .build();
    }

    private MultipartResponse createBadParametersErrorMultipartResponse(Message message) {
        return MultipartResponse.Builder.newInstance()
                .header(badParameters(message, connectorId))
                .build();
    }

}
