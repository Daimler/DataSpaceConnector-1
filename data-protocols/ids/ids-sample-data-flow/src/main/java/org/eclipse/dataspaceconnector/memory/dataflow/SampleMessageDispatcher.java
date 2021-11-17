package org.eclipse.dataspaceconnector.memory.dataflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ArtifactResponseMessage;
import de.fraunhofer.iais.eis.ArtifactResponseMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import jakarta.ws.rs.core.MediaType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.eclipse.dataspaceconnector.ids.core.util.CalendarUtil;
import org.eclipse.dataspaceconnector.ids.spi.IdsIdParser;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.transform.IdsProtocol;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.message.MessageContext;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcher;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.message.RemoteMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SampleMessageDispatcher implements RemoteMessageDispatcher {

    private final Monitor monitor;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public SampleMessageDispatcher(@NotNull Monitor monitor, @NotNull OkHttpClient okHttpClient) {
        this.monitor = Objects.requireNonNull(monitor);
        this.okHttpClient = Objects.requireNonNull(okHttpClient);
        objectMapper = new ObjectMapper();
    }

    @Override
    public String protocol() {
        return Protocol.IDS_SAMPLE;
    }

    @Override
    public <T> CompletableFuture<T> send(Class<T> responseType, RemoteMessage message, MessageContext context) {

        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        if (!(message instanceof SampleRemoteMessage)) {
            return CompletableFuture.failedFuture(new EdcException(String.format("SampleMessageDispatcher: Can handle handle massages of type %s", SampleRemoteMessage.class.getName())));
        }

        SampleRemoteMessage sampleRemoteMessage = (SampleRemoteMessage) message;

        ArtifactResponseMessage responseMessage = createArtifactResponseMessage(sampleRemoteMessage.getConnectorId(), sampleRemoteMessage.getCorrelationMessage());

        try {
            Request request = createRequest(sampleRemoteMessage.getConsumerUrl(), sampleRemoteMessage.getData(), responseMessage);

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    monitor.info("Sent ArtifactResponseMessage: Failure");
                    completableFuture.completeExceptionally(e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    monitor.info("Sent ArtifactResponseMessage: Success");
                    completableFuture.complete(null);
                }
            });


        } catch (Exception e) {
            monitor.severe(e.getMessage(), e);
        }

        return completableFuture;
    }

    // create the multipart-form-data request having the given message in its "header" multipart payload
    protected Request createRequest(String url, byte[] data, Message message) throws Exception {
        Objects.requireNonNull(message);

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(okhttp3.MediaType.get(MediaType.MULTIPART_FORM_DATA))
                .addPart(createIdsMessageHeaderMultipart(message))
                .addPart(createIdsMessagePayloadMultipart(data))
                .build();

        return new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(url)))
                .addHeader("Content-Type", MediaType.MULTIPART_FORM_DATA)
                .post(multipartBody)
                .build();
    }

    // create the "header" multipart
    private MultipartBody.Part createIdsMessageHeaderMultipart(Message message) throws Exception {
        Headers headers = new Headers.Builder()
                .add("Content-Disposition", "form-data; name=\"header\"")
                .build();

        RequestBody requestBody = RequestBody.create(
                toJson(message),
                okhttp3.MediaType.get(MediaType.APPLICATION_JSON));

        return MultipartBody.Part.create(headers, requestBody);
    }

    // create the "payload" multipart
    private MultipartBody.Part createIdsMessagePayloadMultipart(byte[] data) {
        Headers headers = new Headers.Builder()
                .add("Content-Disposition", "form-data; name=\"payload\"")
                .build();

        RequestBody requestBody = RequestBody.create(
                data,
                okhttp3.MediaType.get(MediaType.APPLICATION_OCTET_STREAM));

        return MultipartBody.Part.create(headers, requestBody);
    }

    private static ArtifactResponseMessage createArtifactResponseMessage(
            @Nullable String connectorId,
            @Nullable Message correlationMessage) {

        URI messageId = URI.create(String.join(IdsIdParser.DELIMITER, IdsIdParser.SCHEME, IdsType.MESSAGE.getValue(), UUID.randomUUID().toString()));
        ArtifactResponseMessageBuilder builder = new ArtifactResponseMessageBuilder(messageId);

        builder._contentVersion_(IdsProtocol.INFORMATION_MODEL_VERSION);
        builder._modelVersion_(IdsProtocol.INFORMATION_MODEL_VERSION);

        String connectorIdUrn = String.join(
                IdsIdParser.DELIMITER,
                IdsIdParser.SCHEME,
                IdsType.CONNECTOR.getValue(),
                connectorId);

        URI connectorIdUri = URI.create(connectorIdUrn);

        builder._issuerConnector_(connectorIdUri);
        builder._senderAgent_(connectorIdUri);

        builder._issued_(CalendarUtil.gregorianNow());

        if (correlationMessage != null) {
            URI id = correlationMessage.getId();
            if (id != null) {
                builder._correlationMessage_(id);
            }

            URI senderAgent = correlationMessage.getSenderAgent();
            if (senderAgent != null) {
                builder._recipientAgent_(new ArrayList<>(Collections.singletonList(senderAgent)));
            }

            URI issuerConnector = correlationMessage.getIssuerConnector();
            if (issuerConnector != null) {
                builder._recipientConnector_(new ArrayList<>(Collections.singletonList(issuerConnector)));
            }
        }

        return builder.build();
    }

    private String toJson(Message message) throws Exception {
        return objectMapper.writeValueAsString(message);
    }

}
