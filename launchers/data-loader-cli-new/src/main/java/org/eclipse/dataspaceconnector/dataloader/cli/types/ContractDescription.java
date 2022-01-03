package org.eclipse.dataspaceconnector.dataloader.cli.types;

import org.eclipse.dataspaceconnector.dataloader.cli.types.constraints.Constraint;
import org.eclipse.dataspaceconnector.dataloader.cli.types.selectors.Selector;

import java.util.List;

public class ContractDescription {
    private final String id;
    private final List<Selector> selectors;
    private final List<Constraint> constraints;

    public ContractDescription(String id, List<Selector> selectors, List<Constraint> constraints) {
        this.selectors = selectors;
        this.id = id;
        this.constraints = constraints;
    }

    public String getId() {
        return id;
    }

    public List<Selector> getSelectors() {
        return selectors;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }
}
