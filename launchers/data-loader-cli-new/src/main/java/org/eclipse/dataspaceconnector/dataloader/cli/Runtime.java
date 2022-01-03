package org.eclipse.dataspaceconnector.dataloader.cli;

import org.eclipse.dataspaceconnector.system.runtime.BaseRuntime;
import picocli.CommandLine;

public class Runtime extends BaseRuntime {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Test()).execute(args);
        System.exit(exitCode);
    }
}
