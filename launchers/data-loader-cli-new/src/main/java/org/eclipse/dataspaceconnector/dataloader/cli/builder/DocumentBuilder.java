package org.eclipse.dataspaceconnector.dataloader.cli.builder;

import org.eclipse.dataspaceconnector.dataloader.cli.types.ContractDescription;
import org.eclipse.dataspaceconnector.dataloader.cli.types.DataDescription;
import org.eclipse.dataspaceconnector.dataloader.cli.types.Document;

import java.util.ArrayList;
import java.util.List;

public class DocumentBuilder {

    private final List<ContractDescription> contractDescriptions;
    private final List<DataDescription> dataDescriptions;

    public DocumentBuilder() {
        contractDescriptions = new ArrayList<>();
        dataDescriptions = new ArrayList<>();
    }

    public DocumentBuilder with(ContractDescription contractDescription) {
        contractDescriptions.add(contractDescription);
        return this;
    }

    public DocumentBuilder with(DataDescription dataDescription) {
        dataDescriptions.add(dataDescription);
        return this;
    }

    public Document build() {
        return new Document(dataDescriptions, contractDescriptions);
    }

}
