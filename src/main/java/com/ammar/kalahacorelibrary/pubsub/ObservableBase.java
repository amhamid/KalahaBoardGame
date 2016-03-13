package com.ammar.kalahacorelibrary.pubsub;

import com.ammar.kalahacorelibrary.event.Event;
import com.ammar.kalahacorelibrary.event.EventType;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * ObservableBase  serves some common functionalities to the observable class.
 * <p>
 * Created by ahamid on 3/13/16.
 */
public abstract class ObservableBase implements Observable {

    protected Map<EventType, Set<Observer>> observerMap;

    @Override
    public void addObserver(final EventType eventType, final Observer observer) {
        final Set<Observer> observers = new LinkedHashSet<>();
        final Set<Observer> currentObservers = this.observerMap.get(eventType);
        if (Objects.nonNull(currentObservers)) {
            observers.addAll(currentObservers);
        }
        observers.add(observer);

        this.observerMap.put(eventType, observers);
    }

    @Override
    public void notifyObservers(final Event event) {
        final EventType eventType = event.getEventType();
        final Set<Observer> observers = new LinkedHashSet<>();
        final Set<Observer> currentObservers = this.observerMap.get(eventType);
        if (Objects.nonNull(currentObservers)) {
            observers.addAll(currentObservers);
        }

        observers.stream().forEach(observer -> observer.update(this, event));
    }
}
