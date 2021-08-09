package org.eclipse.dataspaceconnector.contract.eventing;

@FunctionalInterface
public interface EventListener<E extends Event> {

    void onEvent(E event);
}
