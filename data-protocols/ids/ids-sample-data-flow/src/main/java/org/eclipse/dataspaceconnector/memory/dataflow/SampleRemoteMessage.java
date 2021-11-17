package org.eclipse.dataspaceconnector.memory.dataflow;

import de.fraunhofer.iais.eis.Message;
import org.eclipse.dataspaceconnector.spi.types.domain.message.RemoteMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SampleRemoteMessage implements RemoteMessage {

    private final Message correlationMessage;
    private final String connectorId;
    private final String consumerUrl;
    private final byte[] data;

    public SampleRemoteMessage(@NotNull String connectorId, @NotNull Message correlationMessage, @NotNull String consumerUrl, byte[] data) {
        this.connectorId = Objects.requireNonNull(connectorId);
        this.correlationMessage = Objects.requireNonNull(correlationMessage);
        this.consumerUrl = Objects.requireNonNull(consumerUrl);
        this.data = Objects.requireNonNull(data);

    }

    @Override
    public String getProtocol() {
        return Protocol.IDS_SAMPLE;
    }

    public Message getCorrelationMessage() {
        return correlationMessage;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public String getConsumerUrl() {
        return consumerUrl;
    }

    public byte[] getData() {
        return data;
    }
}
