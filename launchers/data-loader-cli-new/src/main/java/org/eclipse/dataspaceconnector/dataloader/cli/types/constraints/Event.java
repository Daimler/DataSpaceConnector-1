package org.eclipse.dataspaceconnector.dataloader.cli.types.constraints;

public class Event implements Constraint {
    private final String name;

    public Event(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
