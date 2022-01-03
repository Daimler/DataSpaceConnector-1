package org.eclipse.dataspaceconnector.dataloader.cli;

import picocli.CommandLine;

import java.util.Arrays;

@CommandLine.Command(name = "Test CLI", version = "Test 1.0", mixinStandardHelpOptions = true)
public class Test implements Runnable {
    @CommandLine.Option(names = { "-s", "--font-size" }, description = "Font size")
    int fontSize = 14;

    @CommandLine.Parameters(paramLabel = "<word>", defaultValue = "Hello, picocli",
            description = "Words to be translated into ASCII art.")
    private String[] words = { "Hello,", "picocli" };

    @Override
    public void run() {
        System.out.println(Arrays.toString(words));
    }
}
