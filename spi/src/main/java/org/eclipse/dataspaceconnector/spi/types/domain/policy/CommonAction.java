package org.eclipse.dataspaceconnector.spi.types.domain.policy;

public enum CommonAction {
    ALL("ALL");

    CommonAction(String type) {
        this.type = type;
    }

    private final String type;

    public String getType() {
        return type;
    }
}
