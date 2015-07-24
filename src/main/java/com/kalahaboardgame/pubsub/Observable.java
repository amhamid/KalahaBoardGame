package com.kalahaboardgame.pubsub;

import java.util.Set;

import com.kalahaboardgame.event.Event;
import com.kalahaboardgame.event.EventType;

/**
 * Custom Observable interface (variant of java.util.Observable) where we can add Observer based on event type.
 * This is one of the reason, I created this custom interface, instead of using java.util.Observable, where here we can addObserver based on interested event type.
 * <p/>
 * Created by amhamid on 7/25/15.
 */
public interface Observable {

    void addObserver(EventType eventType, Observer observer);

    void addObserver(Set<EventType> eventTypes, Observer observer);

    void notifyObservers(Event event);

}
