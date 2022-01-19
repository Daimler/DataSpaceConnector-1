package org.eclipse.dataspaceconnector.api.data.client;

import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.api.data.client.configuration.DataManagementApiClientSettings;
import org.eclipse.dataspaceconnector.api.data.client.exceptions.DataManagementApiClientException;
import org.eclipse.dataspaceconnector.api.data.client.exceptions.UnsupportedCriterionOperandTypeException;
import org.eclipse.dataspaceconnector.api.data.client.exceptions.UnsupportedCriterionOperatorException;
import org.eclipse.dataspaceconnector.clients.postgresql.PostgresqlClient;
import org.eclipse.dataspaceconnector.clients.postgresql.PostgresqlClientImpl;
import org.eclipse.dataspaceconnector.clients.postgresql.asset.util.TestPreparedStatementResourceReader;
import org.eclipse.dataspaceconnector.clients.postgresql.connection.ConnectionFactory;
import org.eclipse.dataspaceconnector.clients.postgresql.connection.ConnectionFactoryConfig;
import org.eclipse.dataspaceconnector.clients.postgresql.connection.ConnectionFactoryImpl;
import org.eclipse.dataspaceconnector.clients.postgresql.connection.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.clients.postgresql.connection.pool.commons.CommonsConnectionPool;
import org.eclipse.dataspaceconnector.clients.postgresql.connection.pool.commons.CommonsConnectionPoolConfig;
import org.eclipse.dataspaceconnector.junit.launcher.EdcExtension;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//@IntegrationTest
@Testcontainers
@ExtendWith(EdcExtension.class)
public class DataManagementApiClientIntegrationTest {

    @Container
    @SuppressWarnings("rawtypes")
    private static final PostgreSQLContainer POSTGRES_CONTAINER = new PostgreSQLContainer("postgres:9.6.12");

    @BeforeEach
    void setup() throws SQLException {
        System.setProperty("edc.postgresql.url", POSTGRES_CONTAINER.getJdbcUrl());
        System.setProperty("edc.postgresql.username", POSTGRES_CONTAINER.getUsername());
        System.setProperty("edc.postgresql.password", POSTGRES_CONTAINER.getPassword());
        System.setProperty("edc.api.data.auth.apikey.key", "X-Api-Key");
        System.setProperty("edc.api.data.auth.apikey.value", "foo");

        ConnectionFactoryConfig connectionFactoryConfig = ConnectionFactoryConfig.Builder.newInstance()
                .uri(URI.create(POSTGRES_CONTAINER.getJdbcUrl()))
                .userName(POSTGRES_CONTAINER.getUsername())
                .password(POSTGRES_CONTAINER.getPassword())
                .autoCommit(true)
                .build();

        ConnectionFactory connectionFactory = new ConnectionFactoryImpl(connectionFactoryConfig);
        CommonsConnectionPoolConfig commonsConnectionPoolConfig = CommonsConnectionPoolConfig.Builder.newInstance().build();
        ConnectionPool connectionPool = new CommonsConnectionPool(connectionFactory, commonsConnectionPoolConfig);
        PostgresqlClient postgresqlClient = new PostgresqlClientImpl(connectionPool);

        postgresqlClient.execute(TestPreparedStatementResourceReader.getTablesCreate());
    }

    @Test
    public void testAssetFlow() throws MalformedURLException, UnsupportedCriterionOperandTypeException, DataManagementApiClientException, UnsupportedCriterionOperatorException {
        DataManagementApiClientSettings settings = new DataManagementApiClientSettings(new URL("http://localhost:8181/"), "X-Api-Key", "foo");
        DataManagementApiClient client = new DataManagementApiClient(new OkHttpClient(), settings);

        List<Criterion> criteria = new ArrayList<>();
        criteria.add(new Criterion(Asset.PROPERTY_ID, "=", "1"));

        // test get asset
        List<Asset> assets = client.getAssets(criteria);
        Assertions.assertEquals(0, assets.size());

        // test creation
        client.createAssetAndAddress(Asset.Builder.newInstance().id("1").build(), DataAddress.Builder.newInstance().type("test").build());
        List<Asset> createdAssets = client.getAssets(criteria);
        Assertions.assertEquals(1, createdAssets.size());

        // test update
        client.updateAsset(Asset.Builder.newInstance().id("1").property("foo", "bar").build());
        List<Asset> updatedAssets = client.getAssets(criteria);
        Assertions.assertEquals(1, updatedAssets.size());
        Assertions.assertEquals("bar", updatedAssets.get(0).getProperties().get("foo"));

        // test delete
        client.deleteAsset(updatedAssets.get(0).getId());
        List<Asset> deletedAssets = client.getAssets(criteria);
        Assertions.assertEquals(0, deletedAssets.size());
    }

    @Test
    public void testAddressFlow() throws MalformedURLException, UnsupportedCriterionOperandTypeException, DataManagementApiClientException, UnsupportedCriterionOperatorException {
        DataManagementApiClientSettings settings = new DataManagementApiClientSettings(new URL("http://localhost:8181/"), "X-Api-Key", "foo");
        DataManagementApiClient client = new DataManagementApiClient(new OkHttpClient(), settings);

        List<Criterion> criteria = new ArrayList<>();
        criteria.add(new Criterion("type", "=", "test"));

        Asset asset = Asset.Builder.newInstance().id("1").build();

        // test creation
        client.createAssetAndAddress(asset, DataAddress.Builder.newInstance().type("test").build());
        List<DataAddress> createdAddresses = client.getAddresses(criteria);
        Assertions.assertEquals(1, createdAddresses.size());

        // test update
        client.updateAddress(asset, DataAddress.Builder.newInstance().type("test").property("foo", "bar").build());
        List<DataAddress> updatedAddresses = client.getAddresses(criteria);
        Assertions.assertEquals(1, updatedAddresses.size());
        Assertions.assertEquals(updatedAddresses.get(0).getProperty("foo"), "bar");

        // test delete
        client.deleteAsset(asset.getId());
        List<DataAddress> deletedAddresses = client.getAddresses(criteria);
        Assertions.assertEquals(0, deletedAddresses.size());
    }

    @Test
    public void testDefinitionFlow() throws MalformedURLException, UnsupportedCriterionOperandTypeException, DataManagementApiClientException, UnsupportedCriterionOperatorException {
        DataManagementApiClientSettings settings = new DataManagementApiClientSettings(new URL("http://localhost:8181/"), "X-Api-Key", "foo");
        DataManagementApiClient client = new DataManagementApiClient(new OkHttpClient(), settings);

        ContractDefinition.Builder contractDefinitionBuilder = ContractDefinition.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .accessPolicy(Policy.Builder.newInstance().id("1").build())
                .contractPolicy(Policy.Builder.newInstance().id("1").build())
                .selectorExpression(AssetSelectorExpression.SELECT_ALL);

        // test creation
        client.createContractDefinition(contractDefinitionBuilder.build());
        List<ContractDefinition> createdDefinitions = client.getContractDefinitions();
        Assertions.assertEquals(1, createdDefinitions.size());

        // test update
        client.updateContractDefinition(contractDefinitionBuilder.accessPolicy(Policy.Builder.newInstance().id("2").build()).build());
        List<ContractDefinition> updatedContractDefinitions = client.getContractDefinitions();
        Assertions.assertEquals(1, updatedContractDefinitions.size());
        Assertions.assertEquals(updatedContractDefinitions.get(0).getAccessPolicy().getUid(), "2");

        // test delete
        client.deleteContractDefinition(contractDefinitionBuilder.build().getId());
        List<ContractDefinition> deletedContractDefinitions = client.getContractDefinitions();
        Assertions.assertEquals(0, deletedContractDefinitions.size());
    }
}
