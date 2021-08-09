package org.eclipse.dataspaceconnector.contract.domain;

import java.util.UUID;

public abstract class AggregateRoot {

    public abstract UUID getAggregateId();

}
