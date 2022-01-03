package org.eclipse.dataspaceconnector.dataloader.cli.reader;

import org.eclipse.dataspaceconnector.dataloader.cli.types.Document;

import java.io.IOException;
import java.io.InputStream;

public interface Reader {
    Document read(InputStream stream) throws IOException;
}
