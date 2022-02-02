package org.eclipse.dataspaceconnector.dataloader.cli;

import org.eclipse.dataspaceconnector.spi.security.KeyParser;
import org.eclipse.dataspaceconnector.spi.security.PrivateKeyResolver;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class NullPrivateKeyResolver implements PrivateKeyResolver {
    @Override
    public <T> @Nullable T resolvePrivateKey(String id, Class<T> keyType) {
        return null;
    }

    @Override
    public <T> void addParser(KeyParser<T> parser) {
    }

    @Override
    public <T> void addParser(Class<T> forType, Function<String, T> parseFunction) {
    }
}
