package org.eclipse.dataspaceconnector.sql.operations;

import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SqlConnectionExtension.class)
public class QueryBuilderComponentTest {

    private ConnectionPool connectionPool;

    @BeforeEach
    public void setup(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Test
    public void testSelectAllQuery() throws SQLException {
        Asset asset1 = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .build();

        Asset asset2 = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .build();

        Transaction transaction = new TransactionBuilder(connectionPool)
                .create(asset1)
                .create(asset2)
                .build();
        transaction.execute();

        // TODO Wait until its verified how an select all looks like

        Query<Asset> query = new QueryBuilder(connectionPool)
                .assets()
                .build();
        List<Asset> assets = query.execute();

        assertThat(assets.stream().map(Asset::getId).collect(Collectors.toUnmodifiableList()))
                .contains(asset1.getId())
                .contains(asset2.getId());
    }

    @Test
    public void testSelectById() throws SQLException {
        Asset asset1 = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .build();

        Asset asset2 = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .build();

        Transaction transaction = new TransactionBuilder(connectionPool)
                .create(asset1)
                .create(asset2)
                .build();
        transaction.execute();

        Query<Asset> query = new QueryBuilder(connectionPool)
                .assets()
                .with(Asset.PROPERTY_ID, asset1.getId())
                .build();
        List<Asset> assets = query.execute();

        assertThat(assets.stream().map(Asset::getId).collect(Collectors.toUnmodifiableList()))
                .contains(asset1.getId());
    }

    @Test
    public void testSelectMultiple() throws SQLException {
        Asset asset1 = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contentType("pdf")
                .version("1.0.0")
                .build();

        Asset asset2 = Asset.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .contentType("pdf")
                .version("1.0.0")
                .build();

        Transaction transaction = new TransactionBuilder(connectionPool)
                .create(asset1)
                .create(asset2)
                .build();
        transaction.execute();

        Query<Asset> query = new QueryBuilder(connectionPool)
                .assets()
                .with(Asset.PROPERTY_CONTENT_TYPE, "pdf")
                .with(Asset.PROPERTY_VERSION, "1.0.0")
                .build();
        List<Asset> assets = query.execute();

        assertThat(assets.stream().map(Asset::getId).collect(Collectors.toUnmodifiableList()))
                .contains(asset1.getId())
                .contains(asset2.getId());
    }
}
