package org.eclipse.dataspaceconnector.contract.eventing;

import java.util.*;

public interface EventDispatcher {

    <I extends Event> void emit(final I event);

    <I extends Event> void consume(final I event);

    <T extends Event> void registerListener(Class<T> event, EventListener<T> listener);

    default <I extends Event> void emit(final I...event) {
        emit(Optional.ofNullable(event).map(Arrays::asList).orElseGet(Collections::emptyList));
    }

    default <I extends Event> void emit(final Collection<I> events) {
        Optional.ofNullable(events).orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .forEach(this::emit);
    }

    default <I extends Event> void consume(final I...event) {
        consume(Optional.ofNullable(event).map(Arrays::asList).orElseGet(Collections::emptyList));
    }

    default <I extends Event> void consume(final Collection<I> events) {
        Optional.ofNullable(events).orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .forEach(this::consume);
    }

}
