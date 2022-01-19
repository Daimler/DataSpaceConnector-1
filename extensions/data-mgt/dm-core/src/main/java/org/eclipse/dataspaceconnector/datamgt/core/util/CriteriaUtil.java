package org.eclipse.dataspaceconnector.datamgt.core.util;

import jakarta.ws.rs.core.UriInfo;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CriteriaUtil {

    private CriteriaUtil() {
    }

    public static List<Criterion> getCriteriaFromUri(UriInfo ui) {
        List<Criterion> criteria = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : ui.getQueryParameters().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().stream().findFirst().orElse(null);

            criteria.add(new Criterion(key, "=", value));
        }
        return criteria;
    }
}
