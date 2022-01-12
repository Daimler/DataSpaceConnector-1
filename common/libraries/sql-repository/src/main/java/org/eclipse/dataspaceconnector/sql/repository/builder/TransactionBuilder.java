package org.eclipse.dataspaceconnector.sql.repository.builder;

import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.sql.repository.types.Operation;

import java.util.ArrayList;
import java.util.List;

public class TransactionBuilder {

    private final ConnectionPool connectionPool;
    private final List<Operation> transactionOperations;

    public TransactionBuilder(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.transactionOperations = new ArrayList<>();
    }

    public TransactionBuilder createAsset(Asset asset){
        return this;
    }

    public TransactionBuilder deleteAsset(Asset asset) {
//        transactionOperations.add(new DeleteAssetOperation);
        return this;
    }

    public Transaction build() {
        return new Transaction(connectionPool, transactionOperations);
    }


}
