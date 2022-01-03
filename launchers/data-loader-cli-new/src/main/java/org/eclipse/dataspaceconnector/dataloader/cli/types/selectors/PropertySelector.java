package org.eclipse.dataspaceconnector.dataloader.cli.types.selectors;

public class PropertySelector implements Selector {
    private final String key;
    private final String value;

    public PropertySelector(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
