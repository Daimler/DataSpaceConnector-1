package org.eclipse.dataspaceconnector.api.data.client;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.eclipse.dataspaceconnector.api.data.client.configuration.DataManagementApiClientSettings;
import org.eclipse.dataspaceconnector.api.data.client.exceptions.DataManagementApiClientException;
import org.eclipse.dataspaceconnector.api.data.client.exceptions.DataManagementApiClientIOException;
import org.eclipse.dataspaceconnector.api.data.client.exceptions.HttpRequestFailedException;
import org.eclipse.dataspaceconnector.api.data.client.exceptions.InvalidServiceUrlException;
import org.eclipse.dataspaceconnector.api.data.client.exceptions.UnsupportedCriterionOperandTypeException;
import org.eclipse.dataspaceconnector.api.data.client.exceptions.UnsupportedCriterionOperatorException;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataAddress;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.eclipse.dataspaceconnector.api.data.client.util.Serialization.deserialize;
import static org.eclipse.dataspaceconnector.api.data.client.util.Serialization.serialize;

public class DataManagementApiClient {

    private static final String MEDIATYPE_JSON = "application/json";
    private static final String ASSET_PATH = "api/data/asset";
    private static final String ADDRESS_PATH = "api/data/address";
    private static final String CONTRACT_DEFINITION_PATH = "api/data/contract-definition";

    private final OkHttpClient okHttpClient;
    private final DataManagementApiClientSettings settings;

    public DataManagementApiClient(OkHttpClient okHttpClient, DataManagementApiClientSettings settings) {
        this.okHttpClient = okHttpClient;
        this.settings = settings;
    }

    public void createAssetAndAddress(Asset asset, DataAddress dataAddress) throws DataManagementApiClientException {
        URL url = getUrl(ASSET_PATH);

        String assetJson = serialize(asset);
        String dataAddressJson = serialize(dataAddress);
        String json = String.format("{ \"asset\": %s, \"address\": %s }", assetJson, dataAddressJson);

        RequestBody requestBody = RequestBody.create(json, MediaType.get(MEDIATYPE_JSON));
        Request request = newRequestBuilder()
                .url(url)
                .put(requestBody)
                .build();

        sendRequest(request);
    }

    public void updateAsset(Asset asset) throws DataManagementApiClientException {
        URL url = getUrl(ASSET_PATH);
        String json = serialize(asset);

        RequestBody requestBody = RequestBody.create(json, MediaType.get(MEDIATYPE_JSON));
        Request request = newRequestBuilder()
                .url(url)
                .post(requestBody)
                .build();

        sendRequest(request);
    }

    public List<Asset> getAssets(List<Criterion> criteria) throws DataManagementApiClientException {
        String queryParameters = criteriaToQueryParameters(criteria);
        URL url = getUrl(ASSET_PATH + "?" + queryParameters);

        Request request = newRequestBuilder()
                .url(url)
                .get()
                .build();

        ResponseBody responseBody = sendRequest(request);

        return deserialize(responseBody.byteStream(), new TypeReference<List<Asset>>() {
        });
    }

    public void deleteAsset(String id) throws DataManagementApiClientException {
        URL url = getUrl(ASSET_PATH + "/" + id);

        Request request = newRequestBuilder()
                .url(url)
                .delete()
                .build();

        sendRequest(request);
    }

    public List<DataAddress> getAddresses(List<Criterion> criteria) throws DataManagementApiClientException, UnsupportedCriterionOperatorException, UnsupportedCriterionOperandTypeException {
        String queryParameters = criteriaToQueryParameters(criteria);
        URL url = getUrl(ADDRESS_PATH + "?" + queryParameters);

        Request request = newRequestBuilder()
                .url(url)
                .get()
                .build();

        ResponseBody responseBody = sendRequest(request);

        return deserialize(responseBody.byteStream(), new TypeReference<List<DataAddress>>() {
        });
    }

    public void updateAddress(Asset asset, DataAddress dataAddress) throws DataManagementApiClientException {
        URL url = getUrl(ADDRESS_PATH);

        String assetJson = serialize(asset);
        String dataAddressJson = serialize(dataAddress);
        String json = String.format("{ \"asset\": %s, \"address\": %s }", assetJson, dataAddressJson);

        RequestBody requestBody = RequestBody.create(json, MediaType.get(MEDIATYPE_JSON));
        Request request = newRequestBuilder()
                .url(url)
                .post(requestBody)
                .build();

        sendRequest(request);
    }

    public List<ContractDefinition> getContractDefinitions() throws DataManagementApiClientException {
        URL url = getUrl(CONTRACT_DEFINITION_PATH);

        Request request = newRequestBuilder()
                .url(url)
                .get()
                .build();

        ResponseBody responseBody = sendRequest(request);

        return deserialize(responseBody.byteStream(), new TypeReference<List<ContractDefinition>>() {
        });
    }

    public void createContractDefinition(ContractDefinition contractDefinition) throws DataManagementApiClientException {
        URL url = getUrl(CONTRACT_DEFINITION_PATH);
        String json = serialize(contractDefinition);

        RequestBody requestBody = RequestBody.create(json, MediaType.get(MEDIATYPE_JSON));

        Request request = newRequestBuilder()
                .url(url)
                .put(requestBody)
                .build();

        sendRequest(request);
    }

    public void updateContractDefinition(ContractDefinition contractDefinition) throws DataManagementApiClientException {
        URL url = getUrl(CONTRACT_DEFINITION_PATH);
        String json = serialize(contractDefinition);

        RequestBody requestBody = RequestBody.create(json, MediaType.get(MEDIATYPE_JSON));

        Request request = newRequestBuilder()
                .url(url)
                .post(requestBody)
                .build();

        sendRequest(request);
    }

    public void deleteContractDefinition(String id) throws DataManagementApiClientException {
        URL url = getUrl(CONTRACT_DEFINITION_PATH + "/" + id);

        Request request = newRequestBuilder()
                .url(url)
                .delete()
                .build();

        sendRequest(request);
    }

    private Request.Builder newRequestBuilder() {
        Request.Builder builder = new Request.Builder();
        builder.header(settings.getApiKey(), settings.getApiKeyValue());
        return builder;
    }

    private ResponseBody sendRequest(Request request) throws DataManagementApiClientException {
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new HttpRequestFailedException(response.code());
            }

            return response.body();

        } catch (IOException e) {
            throw new DataManagementApiClientIOException(e);
        }
    }

    private URL getUrl(String relativePath) throws InvalidServiceUrlException {
        try {
            return new URL(settings.getServiceUrl(), relativePath);
        } catch (MalformedURLException e) {
            throw new InvalidServiceUrlException(settings.getServiceUrl(), e);
        }
    }

    private static String criteriaToQueryParameters(List<Criterion> criteria) throws UnsupportedCriterionOperatorException, UnsupportedCriterionOperandTypeException {
        StringBuilder parameterBuilder = new StringBuilder();
        for (Criterion criterion : criteria) {
            if (!(criterion.getOperator().equals("eq") || criterion.getOperator().equals("="))) {
                throw new UnsupportedCriterionOperatorException(criterion.getOperator());
            }
            if (!(criterion.getOperandLeft() instanceof String)) {
                throw new UnsupportedCriterionOperandTypeException(criterion.getOperandLeft().getClass().getTypeName());
            }
            if (!(criterion.getOperandRight() instanceof String)) {
                throw new UnsupportedCriterionOperandTypeException(criterion.getOperandRight().getClass().getTypeName());
            }

            if (parameterBuilder.length() != 0) {
                parameterBuilder.append("&");
            }

            parameterBuilder.append(String.format("%s=%s", criterion.getOperandLeft(), criterion.getOperandRight()));

        }

        return parameterBuilder.toString();
    }
}
