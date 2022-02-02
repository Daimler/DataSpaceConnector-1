package org.eclipse.dataspaceconnector.dataloader.cli;

import org.eclipse.dataspaceconnector.spi.result.Result;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryVault implements Vault {
    private final Map<String, String> vaultMap = new ConcurrentHashMap<>();

    @Override
    public @Nullable String resolveSecret(String key) {
        return vaultMap.get(key);
    }

    @Override
    public Result<Void> storeSecret(String key, String value) {
        vaultMap.put(key, value);
        return Result.success();
    }

    @Override
    public Result<Void> deleteSecret(String key) {
        vaultMap.remove(key);
        return Result.success();
    }
}
