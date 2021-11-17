package org.eclipse.dataspaceconnector.memory.dataflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.Message;
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

        var destination = dataRequest.getDataDestination();

        if (!destination.getType().equals(InMemoryDataFlowSchema.TYPE)) {
            throw new EdcException(String.format("InMemoryFlowController: Destination of type %s is not supported (request %s)", destination.getType(), dataRequest.getId()));
        }

        var key = destination.getProperty(InMemoryDataFlowSchema.ATTRIBUTE_KEY);
        if (key == null || key.isEmpty()) {
            throw new EdcException(String.format("InMemoryFlowController: Missing key attribute (request %s)", dataRequest.getId()));
        }

        var consumerUrl = destination.getProperty(InMemoryDataFlowSchema.ATTRIBUTE_CONSUMER_URL);
        if (consumerUrl == null || consumerUrl.isEmpty()) {
            throw new EdcException(String.format("InMemoryFlowController: Missing consumer url attribute (request %s)", dataRequest.getId()));
        }

        var connectorId = destination.getProperty(InMemoryDataFlowSchema.ATTRIBUTE_CONNECTOR_ID);
        if (connectorId == null || connectorId.isEmpty()) {
            throw new EdcException(String.format("InMemoryFlowController: Missing connector id attribute (request %s)", dataRequest.getId()));
        }

        var correlationMessageJson = destination.getProperty(InMemoryDataFlowSchema.ATTRIBUTE_CORRELATION_MESSAGE);
        if (correlationMessageJson == null || correlationMessageJson.isEmpty()) {
            throw new EdcException(String.format("InMemoryFlowController: Missing correlation message attribute (request %s)", dataRequest.getId()));
        }

        Message correlationMessage;
        try {
            correlationMessage = objectMapper.readValue(correlationMessageJson, Message.class);
        } catch (JsonProcessingException e) {
            throw new EdcException(e);
        }

        var data = inMemoryDataStore.load(key);
        if (data == null || data.length == 0) {
            throw new EdcException(String.format("InMemoryFlowController: No data found for key %s (request %s)", key, dataRequest.getId()));
        }

        RemoteMessage remoteMessage = createSampleMessage(connectorId, correlationMessage, consumerUrl, data);
        remoteMessageDispatcherRegistry.send(Void.class, remoteMessage, null);

        return DataFlowInitiateResponse.OK;
    }

    private RemoteMessage createSampleMessage(String connectorId, Message correlationMessage, String consumerUrl, byte[] data) {
        return new SampleRemoteMessage(connectorId, correlationMessage, consumerUrl, data);
    }

}