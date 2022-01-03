package org.eclipse.dataspaceconnector.dataloader.cli.types;

import org.eclipse.dataspaceconnector.dataloader.cli.types.addresses.Address;

import java.util.Map;

public class DataDescription {

    private final String id;
    private final Map<String, String> properties;
    private final Address address;

    public DataDescription(String id, Map<String, String> properties, Address address) {
        this.id = id;
        this.properties = properties;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public Address getAddress() {
        return address;
    }
}
