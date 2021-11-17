package org.eclipse.dataspaceconnector.memory.dataflow;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryDataStore {
    private final ConcurrentMap<String, byte[]> dataMap = new ConcurrentHashMap<>();

    public byte[] load(@NotNull String id) {
        return dataMap.get(Objects.requireNonNull(id));
    }

    public void save(@NotNull String key, byte[] data) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(data);

        dataMap.put(key, data);
    }
}
