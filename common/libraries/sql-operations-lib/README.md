# SQL Operations Library

The SQL Operations Library helps to query and update EDC domain objects.

## Query Builder

The `QueryBuilder` creates an executable `Query` for domain objects. A query consists of the target domain objects and
filters.

### Usage

```java
class Examples {

    public List<Asset> findAsset(String assetId) throws SQLException {
        Query<Asset> query = new QueryBuilder(connectionPool)
                .assets()
                .with(Asset.PROPERTY_ID, assetId)
                .build();
        return query.execute();
    }

}
```

## Transaction Builder

The `TransactionBuilder` creates an executable `Transaction` for one or many operations.

### Usage

```java
class Examples {

    public void create(Asset asset) throws SQLException {
        Transaction transaction = new TransactionBuilder(connectionPool)
                .create(asset)
                .build();
        transaction.execute();
    }

    public void delete(Asset asset) throws SQLException {
        Transaction transaction = new TransactionBuilder(connectionPool)
                .delete(asset)
                .build();
        transaction.execute();
    }

    public void createMany(Asset a, Asset b, Asset c) throws SQLException {
        Transaction transaction = new TransactionBuilder(connectionPool)
                .create(a)
                .create(b)
                .create(c)
                .build();
        transaction.execute();
    }

}
```