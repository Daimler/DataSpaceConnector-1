## Transaction Manager SPI & Extension

The SPI provides the transaction interfaces and the Extension makes an implementation of the manager available in
then `ServiceExtensionContext`.

```java
class TransactionManager {

    void onStatusChanged(StatusChangedListener statusChangedListener);

    TransactionContext beginTransaction();

    TransactionStatus getStatus();

    void commit();

    void rollback();
}
```

```java
class TransactionContext {

    TransactionStatus getStatus();
}
```

Other components, that intent do become transactional, may listen for transaction changes and act accordingly.

The corresponding service extension may look like this:

```java
class TransactionServiceExtension implements ServiceExtension {

    @Override
    public void initialize(ServiceExtensionContext context) {
        TransactionManager transactionManager = new TransactionManagerImpl();
        context.registerService(TransactionManager.class, transactionManager);
    }
}
```

## Connection Pool Library

Extensions, that implement a connection pool, may extend an abstract implementation from this library, that already
supports transactional.

```java
class BaseConnectionPool implements ConnectionPool {


    /**
     * Constructor without transaction capabilities
     */
    public ConnectionPoolBase() {
    }

    /**
     * Constructor with transaction capabilities 
     * @param transactionManager transaction manager
     */
    public ConnectionPoolBase(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;

        registerListener(transactionManager);
    }

    /**
     * Get a new connection. If a transaction is active, each call within the same transaction gets the same connection.
     * Otherwise, a new connection from the pool is returned.
     *
     * Additionally, transactional connections won't do a commit on the connection outside the transaction. And rollbacks  
     * on the transactions will be not only valid for the single connection, but for the while transaction.
     *
     * @return connection
     *
     */
    @Override
    public Connection getConnection() {
        return /* connection */;
    }

    /**
     * Returns a connection to the connection pool.
     * If the connection is used within a transaction, nothing is done. 
     *
     * @param connection connection
     */
    @Override
    public void returnConnection(Connection connection) {
        /* return code */
    }

    /**
     * Registers listener to the transaction manager (if available)
     * @param transactionManager transaction manager
     */
    private void registerListener(TransactionManager transactionManager) {

        transactionManager.onStatusChanged(status ->
        {
            switch (status) {
                case NEW:
                    /* do nothing */
                    break;
                case ACTIVE:
                    /* get new transaction connection */
                    break;
                case ROLLBACK:
                    /* rollback transaction connection */
                    break;
                case COMMIT:
                    /* commit transaction connection */
                    break;
                case ROLLBACK_COMPLETE:
                case COMMIT_COMPLETE:
                    /* return transaction connection */
                    break;
            }
        });
    }

    protected abstract Connection getConnectionInternal() throws SQLException;

    protected abstract void returnConnectionInternal(Connection connection) throws SQLException;
}
```

Then create a connection pool implementation.

```java
class CommonsConnectionPool extends ConnectionPoolBase implements AutoCloseable {
    private final GenericObjectPool<Connection> connectionObjectPool;

    public CommonsConnectionPool(TransactionManager transactionManager, ConnectionFactory connectionFactory,
                                 CommonsConnectionPoolConfig commonsConnectionPoolConfig) {
        super(transactionManager);

        /* construct */
    }

    public CommonsConnectionPool(ConnectionFactory connectionFactory,
                                 CommonsConnectionPoolConfig commonsConnectionPoolConfig) {

        /* construct */
    }

    @Override
    public Connection getConnectionInternal() throws SQLException {
        try {
            return connectionObjectPool.borrowObject();
        } catch (SQLException sqlException) {
            throw sqlException;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void returnConnectionInternal(Connection connection) {
        Objects.requireNonNull(connection, "connection");

        connectionObjectPool.returnObject(connection);
    }

    /* more code */
}
```

## Contract Definition Store Extension

```java
class SqlContractNegotiationStoreServiceExtension implements ServiceExtension {

    @Override
    public void initialize(ServiceExtensionContext context) {
        TransactionManager transactionManager = context.getService(TransactionManager.class, null); /*optional*/

        ConnectionFactory connectionFactory = null /* new Factory */;
        CommonsConnectionPoolConfig poolConfig = null /* new Commons Pool Config */;
        CommonsConnectionPool pool = new CommonsConnectionPool(transactionManager, connectionFactory, poolConfig);

        ContractDefintionStore store = new SqlContractDefintionStore(pool);
        context.registerService(ContractDefintionStore.class, store);
    }
}
```

## Contract Agreement Store Extension

```java
class SqlContractAgreementStoreServiceExtension implements ServiceExtension {

    @Override
    public void initialize(ServiceExtensionContext context) {
        TransactionManager transactionManager = context.getService(TransactionManager.class, null); /*optional*/

        ConnectionFactory connectionFactory = null /* new Factory */;
        CommonsConnectionPoolConfig poolConfig = null /* new Commons Pool Config */;
        CommonsConnectionPool pool = new CommonsConnectionPool(transactionManager, connectionFactory, poolConfig);

        ContractAgreementStore store = new SqlContractAgreementStore(pool);
        context.registerService(ContractAgreementStore.class, store);
    }
}
```

## Kafka Message Dispatcher Extension

```java
class SqlContractAgreementStoreServiceExtension implements ServiceExtension {

    @Override
    public void initialize(ServiceExtensionContext context) {
        TransactionManager transactionManager = context.getService(TransactionManager.class, null); /*optional*/


        ConnectionFactory connectionFactory = null /* new Factory */;
        CommonsConnectionPoolConfig poolConfig = null /* new Commons Pool Config */;
        CommonsConnectionPool pool = new CommonsConnectionPool(transactionManager, connectionFactory, poolConfig);

        RemoteMessageDispatcher kafkaDispatcher = null /* new Dispatcher */;

        RemoteMessageDispatcherRegistry registry = context.getService(RemoteMessageDispatcherRegistry.class);
        registry.register(kafkaDispatcher);
    }
}
```

## An extension that completes a negotiation

```java
class NegotiationClass {

    private ContractNegotiationStore contractNegotiationStore; /* SqlContractNegotiationStoreExtension */
    private ContractAgreementStore contractAgreementStore; /* SqlContractNegotiationStoreExtension */
    private RemoteMessageDispatcher remoteMessageDispatcher;
    private TransactionManager transactionManager;
    
    public void complete(Contract contract) {
        try {
            TransactionContext context = transactionManager.beginTransaction();

            contractNegotiationStore.delete(contract);
            contractAgreementStore.add(contract);
            
            RemoteMessage msg = new KafkaMessage("Contract Complete");
            remoteMessageDispatcher.send(msg);

            context.commit();
        } catch(Exception e) {
            context.rollback();
        }
    }
    
}
```
