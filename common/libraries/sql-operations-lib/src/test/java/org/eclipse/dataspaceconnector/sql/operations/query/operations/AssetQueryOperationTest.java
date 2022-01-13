package org.eclipse.dataspaceconnector.sql.operations.query.operations;

import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.operations.SqlConnectionExtension;
import org.eclipse.dataspaceconnector.sql.operations.Transaction;
import org.eclipse.dataspaceconnector.sql.operations.TransactionBuilder;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SqlConnectionExtension.class)
public class AssetQueryOperationTest {

    private ConnectionPool connectionPool;
    private Connection connection;

    @BeforeEach
    public void setup(ConnectionPool connectionPool, Connection connection) {
        this.connectionPool = connectionPool;
        this.connection = connection;
    }

    @Test
    public void testQuery() throws SQLException {
        // prepare
        Asset asset = Asset.Builder.newInstance()
                .property("foo", "bar")
                .build();

        Transaction transaction = new TransactionBuilder(connectionPool)
                .create(asset)
                .build();
        transaction.execute();

        // invoke
        QueryOperation<Asset> queryOperation = new AssetQueryOperation(Collections.singletonMap("foo", "bar"));
        List<Asset> results = queryOperation.invoke(connection);

        // verify
        assertThat(results.stream().map(Asset::getId)).contains(asset.getId());
    }

}
