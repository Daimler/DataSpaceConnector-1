package org.eclipse.dataspaceconnector.dataloader.cli.types.constraints;

public class GeoLocation implements Constraint {
    private final String location;

    public GeoLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
