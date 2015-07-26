package com.ammar.kalahacorelibrary.pubsub.audit;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.ammar.kalahacorelibrary.event.Event;
import com.ammar.kalahacorelibrary.event.EventType;
import com.ammar.kalahacorelibrary.pubsub.Observable;
import com.ammar.kalahacorelibrary.pubsub.Observer;
import org.apache.log4j.Logger;

/**
 * Replay-able logger, to recreate situation based on events
 * <p/>
 * Created by amhamid on 7/24/15.
 */
public class ReplayableEventPublisher implements Observable, Observer {
    private final Logger logger = Logger.getLogger(ReplayableEventPublisher.class);

    private final Map<EventType, Set<Observer>> observerMap;

    public ReplayableEventPublisher() {
        this.observerMap = new LinkedHashMap<>();
    }

    public void update(final Observable observable, final Event event) {
        logger.info("Publisher: " + observable + " - " + event);
    }

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
    public void addObserver(final Set<EventType> eventTypes, final Observer observer) {
        eventTypes.stream().forEach(eventType -> addObserver(eventType, observer));
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
