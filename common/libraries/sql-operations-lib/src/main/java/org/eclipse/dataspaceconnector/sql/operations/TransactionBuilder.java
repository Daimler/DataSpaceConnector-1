package org.eclipse.dataspaceconnector.sql.operations;

import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.operations.transaction.Transaction;
import org.eclipse.dataspaceconnector.sql.operations.transaction.operations.TransactionOperation;
import org.eclipse.dataspaceconnector.sql.operations.transaction.operations.asset.AssetCreateOperation;
import org.eclipse.dataspaceconnector.sql.operations.transaction.operations.asset.AssetDeleteOperation;
import org.eclipse.dataspaceconnector.sql.operations.transaction.operations.asset.AssetUpdateOperation;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;

import java.util.ArrayList;
import java.util.List;

/**
 * The Transaction Builder creates an executable {@link Transaction} for one or many operations.
 */
public class TransactionBuilder {

    private final ConnectionPool connectionPool;
    private final List<TransactionOperation> transactionOperations;

    /**
     * Constructor of the Transaction Builder
     *
     * @param connectionPool that provides a connection for the query
     */
    public TransactionBuilder(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        this.transactionOperations = new ArrayList<>();
    }

    /**
     * Adds an {@link Asset} create operation to the {@link Transaction}
     *
     * @param asset object to create
     * @return builder instance
     */
    public TransactionBuilder create(Asset asset) {
        transactionOperations.add(new AssetCreateOperation(asset));
        return this;
    }

    /**
     * Adds an {@link Asset} delete operation to the {@link Transaction}
     *
     * @param asset object to delete
     * @return builder instance
     */
    public TransactionBuilder delete(Asset asset) {
        transactionOperations.add(new AssetDeleteOperation(asset));
        return this;
    }

    /**
     * Adds an {@link Asset} update operation to the {@link Transaction}
     *
     * @param asset object to update
     * @return builder instance
     */
    public TransactionBuilder update(Asset asset) {
        transactionOperations.add(new AssetUpdateOperation(asset));
        return this;
    }

    /**
     * @return transaction
     */
    public Transaction build() {
        return new Transaction(connectionPool, transactionOperations);
    }


}
