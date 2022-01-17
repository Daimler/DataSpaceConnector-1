# Transaction

The EDC should support transactions. For starters supporting transactions for the different kind of EDC storage systems
is fine. But the implementation must be open enough to integrate other systems, too.

## Considerations

### Transaction must be impartial to the storage system

A transaction must be able to handle different kind of storage systems (at the same time).

**For example** an EDC application with `Assets`, stored in Azure Cosmos DB, and `ContractDefinitions`, that are
persisted in PostgreSQL. If a transaction consists of updating various `Asset` and `ContractDefinitions`, it should
execute ether all updates successfully or none.

**Another example**. An EDC application has it's `Asset` stored in a PostgreSQL database. After exchanging the `Asset`
storage with another technology (e.g. Azure Cosmos DB), transactions must still work.

### Transactions may affect different kind of operations

A transaction does not only happen in storage systems, it may affect a wide variety of other external systems, too.

**For example**. A transaction that contains two different kind of operations. The sending of a message and storing of
an object. If ether one of them fails, the other operation should not be done, too.

### Transactions are optional

As the EDC supports a large number of extensions and different kind of applications, transactions must be optional.

**For example** inserting data into a storage system, may happen transactional. But it's also possible that an EDC
application has no requirements for transactions and isn't running with any transactional extensions.

The extension, that does the data insertion, must function accordingly in both cases.

### Uncommitted reads

Transactions support uncommitted reads.

**For example** a new `Asset` can be found during the transaction without any prior commit.

## New projects and core components

Assumption of required projects.

### Transaction SPI

#### Transaction Manager

#### Transaction Context

### Transaction Extension

##### Transaction Manager Impl

##### Transaction Context Impl

### SQL Transaction Extension

#### SQL Transaction Context