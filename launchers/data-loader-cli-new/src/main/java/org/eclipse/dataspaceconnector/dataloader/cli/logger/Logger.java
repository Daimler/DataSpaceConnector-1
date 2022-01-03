package org.eclipse.dataspaceconnector.dataloader.cli.logger;

public interface Logger {
    void warning(String msg);

    void info(String msg);

    void error(String msg);
}
