package org.eclipse.dataspaceconnector.ids.spi.version;

public class IdsProtocolVersion {
    private final String value;

    public IdsProtocolVersion(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
