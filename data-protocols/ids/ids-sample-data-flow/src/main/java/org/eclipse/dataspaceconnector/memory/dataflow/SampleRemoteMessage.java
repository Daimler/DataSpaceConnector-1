package org.eclipse.dataspaceconnector.memory.dataflow;

import org.eclipse.dataspaceconnector.spi.types.domain.message.RemoteMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SampleRemoteMessage implements RemoteMessage {

    private final String connectorId;
    private final String consumerUrl;
    private final byte[] data;

    public SampleRemoteMessage(@NotNull String connectorId, @NotNull String consumerUrl, byte[] data) {
        this.connectorId = Objects.requireNonNull(connectorId);
        this.consumerUrl = Objects.requireNonNull(consumerUrl);
        this.data = Objects.requireNonNull(data);
    }

    @Override
    public String getProtocol() {
        return Protocol.IDS_SAMPLE;
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
