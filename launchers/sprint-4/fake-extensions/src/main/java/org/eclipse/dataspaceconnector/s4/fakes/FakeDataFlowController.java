package org.eclipse.dataspaceconnector.s4.fakes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.response.ResponseStatus;
import org.eclipse.dataspaceconnector.spi.transfer.flow.DataFlowController;
import org.eclipse.dataspaceconnector.spi.transfer.flow.DataFlowInitiateResult;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataRequest;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FakeDataFlowController implements DataFlowController {

    private final AssetIndex assetIndex;
    private final OkHttpClient client;

    public FakeDataFlowController(AssetIndex assetIndex, OkHttpClient client) {
        this.assetIndex = assetIndex;
        this.client = client;
    }

    @Override
    public boolean canHandle(DataRequest dataRequest) {
        return Objects.equals(dataRequest.getDestinationType(), "fake");
    }

    @Override
    public @NotNull DataFlowInitiateResult initiateFlow(DataRequest dataRequest) {
        try {

            String url = dataRequest.getDataDestination().getProperty("url");
            Asset asset = assetIndex.findById(dataRequest.getAssetId());
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(asset);

            RequestBody body = RequestBody.create(json, MediaType.get(jakarta.ws.rs.core.MediaType.APPLICATION_JSON));
            Request request = new Request.Builder()
                    .post(body)
                    .url(url)
                    .build();

            client.newCall(request).request();

        } catch (JsonProcessingException e) {
            return DataFlowInitiateResult.failure(ResponseStatus.FATAL_ERROR, e.getMessage());
        }
        return DataFlowInitiateResult.success("yay");
    }
}
