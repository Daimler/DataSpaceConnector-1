package org.eclipse.dataspaceconnector.contract.eventing;

import org.eclipse.dataspaceconnector.spi.monitor.Monitor;

import java.util.*;

public class InMemoryEventDispatcher implements EventDispatcher {

    private final InMemoryEventDispatcherRegistry registry;
    private final Monitor monitor;

    public InMemoryEventDispatcher(final Monitor monitor) {
        this.monitor = monitor;
        this.registry = new InMemoryEventDispatcherRegistry();
    }

    public InMemoryEventDispatcher() {
        this.monitor = null;
        this.registry = new InMemoryEventDispatcherRegistry();
    }

    @Override
    public <T extends Event> void registerListener(final Class<T> event, final EventListener<T> listener) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(listener);

        registry.registerListener(event, listener);
    }

    @Override
    public <I extends Event> void emit(final I event) {
        consume(event);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <I extends Event> void consume(final I event) {
        if (event == null) {
            return;
        }

        registry.getListeners(event.getClass())
                .forEach(eventListener -> emit(event, eventListener));
    }

    private <T extends Event> void emit(final T event, final EventListener<T> eventListener) {
        try {
            eventListener.onEvent(event);
        } catch (final Exception exception) {
            if (monitor == null) {
                return;
            }

            monitor.severe("Error invoking EventListener", exception);
        }
    }

    private static class InMemoryEventDispatcherRegistry {

        private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> eventListenerMap = new HashMap<>();

        <T extends Event> void registerListener(final Class<T> event, final EventListener<T> listener) {
            eventListenerMap.computeIfAbsent(event, (k) -> new LinkedList<>()).add(listener);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        <T extends Event> List<EventListener> getListeners(final Class eventClass) {
            return eventListenerMap.computeIfAbsent(eventClass, (k) -> new LinkedList<>());
        }
    }
}
