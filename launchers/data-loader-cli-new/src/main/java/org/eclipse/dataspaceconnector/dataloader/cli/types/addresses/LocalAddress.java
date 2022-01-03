package org.eclipse.dataspaceconnector.dataloader.cli.types.addresses;

import java.nio.file.Path;

public class LocalAddress implements Address {
    private final Path path;

    public LocalAddress(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
