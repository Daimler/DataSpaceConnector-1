package org.eclipse.dataspaceconnector.sql.operations;

import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.sql.operations.mapper.ExistsMapper;
import org.eclipse.dataspaceconnector.sql.operations.mapper.PropertyMapper;
import org.eclipse.dataspaceconnector.sql.operations.types.Property;
import org.eclipse.dataspaceconnector.sql.operations.util.PreparedStatementResourceReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

@ExtendWith(SqlConnectionExtension.class)
class TransactionBuilderComponentTest {

    private ConnectionPool connectionPool;
    private Connection connection;

    @BeforeEach
    public void setup(ConnectionPool connectionPool, Connection connection) {
        this.connectionPool = connectionPool;
        this.connection = connection;
    }

    @Test
    public void testAssetAndPropertyCreation() throws SQLException {
        Asset asset = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contentType("pdf")
                .version("1.0.0")
                .build();

        Transaction transaction = new TransactionBuilder(connectionPool)
                .create(asset)
                .build();
        transaction.execute();

        List<Property> properties = executeQuery(connection, new PropertyMapper(),
                PreparedStatementResourceReader.readPropertiesSelectByAssetId(), asset.getId());
        List<Boolean> exists = executeQuery(connection, new ExistsMapper(),
                PreparedStatementResourceReader.readAssetExists(), asset.getId());

        Assertions.assertTrue(exists.size() == 1 && exists.get(0));
        Assertions.assertEquals(3, properties.size());
        assertThat(properties)
                .contains(new Property(Asset.PROPERTY_ID, asset.getId()))
                .contains(new Property(Asset.PROPERTY_CONTENT_TYPE, asset.getContentType()))
                .contains(new Property(Asset.PROPERTY_VERSION, asset.getVersion()));
    }

    @Test
    public void testAssetUpdate() throws SQLException {
        Asset base = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contentType("pdf")
                .version("1.0.0")
                .property("foo", "bar")
                .build();

        Asset asset = Asset.Builder.newInstance()
                .id(base.getId())
                .contentType("pdf")
                .version("1.1.0")
                .name("updatedAsset")
                .build();

        Transaction createTransaction = new TransactionBuilder(connectionPool)
                .create(base)
                .build();

        Transaction updateTransaction = new TransactionBuilder(connectionPool)
                .update(asset)
                .build();

        createTransaction.execute();
        updateTransaction.execute();

        List<Property> properties = executeQuery(connection, new PropertyMapper(),
                PreparedStatementResourceReader.readPropertiesSelectByAssetId(), asset.getId());
        List<Boolean> exists = executeQuery(connection, new ExistsMapper(),
                PreparedStatementResourceReader.readAssetExists(), asset.getId());

        Assertions.assertTrue(exists.size() == 1 && exists.get(0));
        Assertions.assertEquals(4, properties.size());
        assertThat(properties)
                .contains(new Property(Asset.PROPERTY_ID, asset.getId()))
                .contains(new Property(Asset.PROPERTY_CONTENT_TYPE, asset.getContentType()))
                .contains(new Property(Asset.PROPERTY_NAME, asset.getName()))
                .contains(new Property(Asset.PROPERTY_VERSION, asset.getVersion()));
    }

    @Test
    public void testAssetDelete() throws SQLException {
        Asset asset = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contentType("pdf")
                .version("1.0.0")
                .build();

        Transaction createTransaction = new TransactionBuilder(connectionPool)
                .create(asset)
                .build();

        Transaction deleteTransaction = new TransactionBuilder(connectionPool)
                .delete(asset)
                .build();

        createTransaction.execute();
        deleteTransaction.execute();

        List<Property> properties = executeQuery(connection, new PropertyMapper(),
                PreparedStatementResourceReader.readPropertiesSelectByAssetId(), asset.getId());
        List<Boolean> exists = executeQuery(connection, new ExistsMapper(),
                PreparedStatementResourceReader.readAssetExists(), asset.getId());

        Assertions.assertFalse(exists.size() == 1 && exists.get(0));
        Assertions.assertEquals(0, properties.size());
    }

}
