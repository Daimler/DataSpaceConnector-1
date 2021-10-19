# Business terms

| Name                                   | Description                                         |
|:---                                    |:---                                                 |
| **Artifact**                           |
| **Asset**                              | * has one content type<br/>* can be a finite as a (set of) file(s) or non finite as a service, stream</br>* are the _unit of sharing_<br/>* can point to one or more (physical) asset elements
| **Asset Element**                      |
| **Asset Index**                        | * manages assets<br/>* provided by an extension<br/>* may support external catalogs<br/>* can be queried 
| **Connector**                          |
| **Connector Directory**                |
| **Contract**                           |
| **Contract Agreement**                 | * points to a **Contract Offer**<br/>* results from a **Contract Negotiation Process**<br/>* has a start date and may have a expiry date and a cancellation date
| **Contract Negotiation**               | * MVP: only possible to accept already offered contracts. Counter offers are rejected automatically.
| **Contract Offer**                     | * set of obligations and permissions<br/>* generated on the fly on provider side (see **Contract Offer Framework**)<br/>* are immutable<br/>* persisted in **Contract Negotiation Process** once the negotiation has started<br/>
| **Contract Offer Framework**           | * generates **Contract Offer Templates**<br/>* provided by **Extensions**<br/>* may be implemented in custom extensions to created contract offers based on existing systems
| **Contract Offer Template**            | Blueprint of a **Contract Offer**
| **Consumer**                           |
| **Data**                               |
| **Dataspace**                          |
| **Identity Provider**                  |
| **IDS Broker**                         | IDS version of the **Connector Directory**
| **Policy**                             | logical collection of rules
| **Provider**                           |
| **Resource**                           |
| **Resource Manifest**                  |
| **Rule**                               | * bound to a **Contract Offer**, **Contract Agreement** or **Contract Offer Framework**<br/>* exist independent from an **Asset**
| **Transfer Process**                   | * based on a **Contract Agreement**


# Technical terms

| Name                                   | Description                                         |
|:---                                    |:---                                                 |
| **Extension**                          | Java module that adds functionality to the EDC. Loaded via Java SPI. Can register services at the context.|
| **Feature** | A name for functionality an extension offers. Just a string. Other extensions can used it to express dependency on that functionality. |
| **Service** | A java class providing logic. Service instances can be registered and found via the context. |
