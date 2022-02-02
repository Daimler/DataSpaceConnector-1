package org.eclipse.dataspaceconnector.s4.fakes;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.eclipse.dataspaceconnector.spi.asset.DataAddressResolver;
import org.eclipse.dataspaceconnector.spi.response.ResponseStatus;
import org.eclipse.dataspaceconnector.spi.transfer.flow.DataFlowController;
import org.eclipse.dataspaceconnector.spi.transfer.flow.DataFlowInitiateResult;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataRequest;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class FakeDataFlowController implements DataFlowController {

    private final OkHttpClient client;
    private final DataAddressResolver dataAddressResolver;

    public FakeDataFlowController(OkHttpClient client, DataAddressResolver dataAddressResolver) {
        this.client = client;
        this.dataAddressResolver = dataAddressResolver;
    }

    @Override
    public boolean canHandle(DataRequest dataRequest) {
        return Objects.equals(dataRequest.getDestinationType(), "fake-api");
    }

    @Override
    public @NotNull DataFlowInitiateResult initiateFlow(DataRequest dataRequest) {
        String url = dataRequest.getDataDestination().getProperty("url");
        String data = dataAddressResolver.resolveForAsset(dataRequest.getAssetId()).getProperty("data");

        RequestBody body = RequestBody.create(data, MediaType.get(jakarta.ws.rs.core.MediaType.TEXT_PLAIN));
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return DataFlowInitiateResult.failure(ResponseStatus.FATAL_ERROR, e.getMessage());
        }

        return DataFlowInitiateResult.success("yay");
    }
}
