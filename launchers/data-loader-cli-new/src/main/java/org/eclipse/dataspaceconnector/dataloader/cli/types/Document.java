package org.eclipse.dataspaceconnector.dataloader.cli.types;

import java.util.List;

public class Document {
    private final List<DataDescription> dataDescriptions;
    private final List<ContractDescription> contractDescriptions;

    public Document(List<DataDescription> dataDescriptions, List<ContractDescription> contractDescriptions) {
        this.dataDescriptions = dataDescriptions;
        this.contractDescriptions = contractDescriptions;
    }

    public List<DataDescription> getDataDescriptions() {
        return dataDescriptions;
    }

    public List<ContractDescription> getContractDescriptions() {
        return contractDescriptions;
    }
}
