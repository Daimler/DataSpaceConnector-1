package org.eclipse.dataspaceconnector.memory.dataflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.transfer.flow.DataFlowController;
import org.eclipse.dataspaceconnector.spi.transfer.flow.DataFlowInitiateResponse;
import org.eclipse.dataspaceconnector.spi.types.domain.message.RemoteMessage;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataRequest;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InMemoryFlowController implements DataFlowController {
    private final Monitor monitor;
    private final InMemoryDataStore inMemoryDataStore;
    private final RemoteMessageDispatcherRegistry remoteMessageDispatcherRegistry;
    private final ObjectMapper objectMapper;

    public InMemoryFlowController(@NotNull Monitor monitor, @NotNull InMemoryDataStore inMemoryDataStore,
                                  @NotNull
                                          RemoteMessageDispatcherRegistry remoteMessageDispatcherRegistry) {
        this.monitor = Objects.requireNonNull(monitor);
        this.inMemoryDataStore = Objects.requireNonNull(inMemoryDataStore);
        this.remoteMessageDispatcherRegistry = Objects.requireNonNull(remoteMessageDispatcherRegistry);
        objectMapper = new ObjectMapper();
    }

    @Override
    public boolean canHandle(DataRequest dataRequest) {
        return dataRequest.getDataDestination().getType().equalsIgnoreCase(InMemoryDataFlowSchema.TYPE);
    }

    @Override
    public @NotNull DataFlowInitiateResponse initiateFlow(DataRequest dataRequest) {

        var consumerUrl = dataRequest.getConnectorAddress();
        var connectorId = dataRequest.getConnectorId();

        var destination = dataRequest.getDataDestination();

        if (!destination.getType().equals(InMemoryDataFlowSchema.TYPE)) {
            throw new EdcException(String.format("InMemoryFlowController: Destination of type %s is not supported (request %s)", destination.getType(), dataRequest.getId()));
        }

        var key = destination.getProperty(InMemoryDataFlowSchema.ATTRIBUTE_KEY);
        if (key == null || key.isEmpty()) {
            throw new EdcException(String.format("InMemoryFlowController: Missing key attribute (request %s)", dataRequest.getId()));
        }

        var data = inMemoryDataStore.load(key);
        if (data == null || data.length == 0) {
            throw new EdcException(String.format("InMemoryFlowController: No data found for key %s (request %s)", key, dataRequest.getId()));
        }

        RemoteMessage remoteMessage = createSampleMessage(connectorId, consumerUrl, data);
        remoteMessageDispatcherRegistry.send(Void.class, remoteMessage, null);

        return DataFlowInitiateResponse.OK;
    }

    private RemoteMessage createSampleMessage(String connectorId, String consumerUrl, byte[] data) {
        return new SampleRemoteMessage(connectorId, consumerUrl, data);
    }

}