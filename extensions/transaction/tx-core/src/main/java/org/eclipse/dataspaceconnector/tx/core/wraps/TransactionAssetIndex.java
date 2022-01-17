package org.eclipse.dataspaceconnector.tx.core.wraps;

import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.transaction.tx.TransactionContext;
import org.eclipse.dataspaceconnector.transaction.tx.TransactionManager;
import org.eclipse.dataspaceconnector.transaction.tx.Transactional;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

// or aspect J

@Transactional(key = "asset")
class TransactionAssetIndex implements AssetIndex {
    private final AssetIndex delegate;
    private final TransactionManager transactionManager;

    TransactionAssetIndex(TransactionManager transactionManager, AssetIndex assetIndex) {
        this.delegate = assetIndex;
        this.transactionManager = transactionManager;
    }

    @Override
    public Stream<Asset> queryAssets(AssetSelectorExpression expression) {
        @SuppressWarnings("DuplicatedCode")
        TransactionContext context = transactionManager.beginTransaction();
        try {
            Stream<Asset> stream = delegate.queryAssets(expression);
            context.commit();
            return stream;
        } catch (Exception e) {
            context.rollback();
            throw e;
        }
    }

    @Override
    public Stream<Asset> queryAssets(List<Criterion> criteria) {
        @SuppressWarnings("DuplicatedCode")
        TransactionContext context = transactionManager.beginTransaction();
        try {
            Stream<Asset> stream = delegate.queryAssets(criteria);
            context.commit();
            return stream;
        } catch (Exception e) {
            context.rollback();
            throw e;
        }
    }

    @Override
    public @Nullable Asset findById(String assetId) {
        TransactionContext context = transactionManager.beginTransaction();
        try {
            Asset asset = delegate.findById(assetId);
            context.commit();
            return asset;
        } catch (Exception e) {
            context.rollback();
            throw e;
        }
    }
}
