package org.eclipse.dataspaceconnector.ids.spi;

/**
 * ID / URI parser for IDS resources.
 */
public class IdsIdParser {
    public static final String SCHEME = "urn";
    public static final String DELIMITER = ":";

    public static IdsId parse(String urn) {
        if (urn == null) {
            throw new IllegalArgumentException("urn must not be null");
        }
        String[] parts = urn.split(DELIMITER);

        String scheme = parts[0];
        if (parts.length < 3 || !scheme.equalsIgnoreCase(SCHEME)) {
            throw new IllegalArgumentException(String.format("Unexpected scheme: %s", scheme));
        }
        String typeString = parts[1];
        IdsType type = IdsType.fromValue(typeString);

        String[] idValues = new String[parts.length - 2];
        System.arraycopy(parts, 2, idValues, 0, parts.length - 2);
        String idValue = String.join(":", idValues);

        return IdsId.Builder.newInstance().type(type).value(idValue).build();
    }
}
