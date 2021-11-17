package org.eclipse.dataspaceconnector.ids.api.multipart.handler;

import de.fraunhofer.iais.eis.ArtifactResponseMessage;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartRequest;
import org.eclipse.dataspaceconnector.ids.api.multipart.message.MultipartResponse;
import org.eclipse.dataspaceconnector.spi.iam.VerificationResult;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Please note that in IDS an ArtifactResponseHandler should not be necessary, as response messages are return for request messages.
 * But in case of EDC IDS may be used asynchronous, therefore the communication may also start with a response message.
 * <p>
 * For simplicity reasons responses messages are also handled via HTTP POST.
 */
public class ArtifactResponseHandler implements Handler {

    private final Monitor monitor;
    private final String connectorId;

    public ArtifactResponseHandler(@NotNull Monitor monitor, @NotNull String connectorId) {
        this.monitor = Objects.requireNonNull(monitor);
        this.connectorId = Objects.requireNonNull(connectorId);
    }

    @Override
    public boolean canHandle(@NotNull MultipartRequest multipartRequest) {
        Objects.requireNonNull(multipartRequest);

        return multipartRequest.getHeader() instanceof ArtifactResponseMessage;
    }

    @Override
    public MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest,
                                           @NotNull VerificationResult verificationResult) {
        Objects.requireNonNull(multipartRequest);
        Objects.requireNonNull(verificationResult);

        // As data flow should happen out of band, do nothing with the data
        InputStream inputStream = multipartRequest.getPayload();
        BufferedInputStream bf = new BufferedInputStream(inputStream);

        BufferedReader r = new BufferedReader(
                new InputStreamReader(bf, StandardCharsets.UTF_8));

        String text = r.lines().collect(Collectors.joining(System.lineSeparator()));
        monitor.info(String.format("Received Data: %s", text));

        return MultipartResponse.Builder.newInstance()
                .header(ResponseMessageUtil.createDummyResponse(connectorId, multipartRequest.getHeader()))
                .build();
    }


}
