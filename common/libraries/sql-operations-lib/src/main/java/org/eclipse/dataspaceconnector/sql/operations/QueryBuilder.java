package org.eclipse.dataspaceconnector.sql.operations;

import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.sql.operations.query.QueryImpl;
import org.eclipse.dataspaceconnector.sql.operations.query.operations.AssetQueryOperation;
import org.eclipse.dataspaceconnector.sql.pool.ConnectionPool;

import java.util.HashMap;
import java.util.Map;

/**
 * The QueryImpl Builder creates an executable {@link QueryImpl} for domain objects. A query consists of the target domain objects and filters.
 */
public class QueryBuilder {

    private final ConnectionPool connectionPool;

    /**
     * Constructor of the QueryImpl Builder
     *
     * @param connectionPool that provides a connection for the query
     */
    public QueryBuilder(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * QueryImpl for asset objects
     *
     * @return builder for query filters
     */
    public FilterBuilder<Asset> assets() {
        return new AssetQueryBuilder(connectionPool);
    }

    /**
     * The Filter Builder enriches a query with filters.
     *
     * @param <T> query target
     */
    public abstract static class FilterBuilder<T> {

        private final Map<String, Object> properties;

        private FilterBuilder() {
            properties = new HashMap<>();
        }

        /**
         * Filter for domain objects where a specific key/value information does apply.
         *
         * @param key   filter key
         * @param value filter value
         * @return builder instance
         */
        public FilterBuilder<T> with(String key, Object value) {
            properties.put(key, value);
            return this;
        }

        /**
         * @return query
         */
        public abstract Query<T> build();

        protected Map<String, Object> getProperties() {
            return properties;
        }
    }

    private static class AssetQueryBuilder extends FilterBuilder<Asset> {
        private final ConnectionPool connectionPool;

        public AssetQueryBuilder(ConnectionPool connectionPool) {
            this.connectionPool = connectionPool;
        }

        @Override
        public Query<Asset> build() {
            return new QueryImpl<>(connectionPool, new AssetQueryOperation(getProperties()));
        }
    }
}
