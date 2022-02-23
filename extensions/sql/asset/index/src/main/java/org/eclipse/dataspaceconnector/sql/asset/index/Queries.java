package org.eclipse.dataspaceconnector.sql.asset.index;

import org.eclipse.dataspaceconnector.spi.EdcException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

final class Queries {
    public static final String ASSET_SELECT_ALL;
    public static final String ASSET_SELECT_BY_ID;
    public static final String ASSET_PROPERTIES_SELECT_BY_ASSET_ID;
    public static final String ASSET_DATA_ADDRESS_SELECT_BY_ASSET_ID;
    private static final String LOCATION_BASE = Queries.class.getPackageName().replaceAll("\\.", "/");

    static {
        ASSET_SELECT_ALL = readSqlScriptResource("asset_select_all");
        ASSET_SELECT_BY_ID = readSqlScriptResource("asset_select_by_id");
        ASSET_PROPERTIES_SELECT_BY_ASSET_ID = readSqlScriptResource("asset_properties_select_by_asset_id");
        ASSET_DATA_ADDRESS_SELECT_BY_ASSET_ID = readSqlScriptResource("asset_data_address_select_by_asset_id");
    }

    private static String readSqlScriptResource(String name) {
        Objects.requireNonNull(name);

        String fullyQualifiedResourceLocation = String.format("%s/%s.sql", LOCATION_BASE, name);

        try (InputStream inputStream = Queries.class.getClassLoader().getResourceAsStream(fullyQualifiedResourceLocation)) {
            if (inputStream == null) {
                throw new EdcException(String.format("Resource not found classpath:%s", fullyQualifiedResourceLocation));
            }

            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new EdcException(e);
        }
    }
}
