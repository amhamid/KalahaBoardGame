package com.kalahaboardgame.pubsub.pit;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.kalahaboardgame.event.Event;
import com.kalahaboardgame.event.EventType;
import com.kalahaboardgame.player.PlayerType;
import com.kalahaboardgame.pubsub.Observable;
import com.kalahaboardgame.pubsub.Observer;

/**
 * Interface for Pit.
 * A Pit is both Observable and Observer. The idea is that A pit should be able to generate events and process events.
 * A pit should be agnostic to other pit's location in the board. Only the Board should know the configuration for the pits location.
 * <p>
 * Created by amhamid on 7/23/15.
 */
public abstract class Pit implements Observable, Observer {

    private final PlayerType playerType;
    private final String pitIdentifier;
    private final Map<EventType, Set<Observer>> observerMap;
    private int numberOfSeeds;

    public Pit(final PlayerType playerType, final String pitIdentifier, final int initialNumberOfSeeds) {
        this.playerType = playerType;
        this.pitIdentifier = pitIdentifier;
        this.numberOfSeeds = initialNumberOfSeeds;
        this.observerMap = new LinkedHashMap<>();
    }

    public abstract void initialMove();

    public abstract void publishNotEmptyEvent();

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

    public PlayerType getPlayerType() {
        return playerType;
    }

    public String getPitIdentifier() {
        return pitIdentifier;
    }

    public int getNumberOfSeeds() {
        return numberOfSeeds;
    }

    protected void publishEvent(final PlayerType playerType, final EventType eventType, final int numberOfSeeds) {
        final Event event = new Event(playerType, getPitIdentifier(), eventType, numberOfSeeds);
        notifyObservers(event);
    }

    public void setNumberOfSeeds(int numberOfSeeds) {
        this.numberOfSeeds = numberOfSeeds;
    }

    @Override
    public String toString() {
        return getPitIdentifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pit pit = (Pit) o;

        if (numberOfSeeds != pit.numberOfSeeds) return false;
        if (playerType != pit.playerType) return false;
        return !(pitIdentifier != null ? !pitIdentifier.equals(pit.pitIdentifier) : pit.pitIdentifier != null);

    }

    @Override
    public int hashCode() {
        int result = playerType != null ? playerType.hashCode() : 0;
        result = 31 * result + (pitIdentifier != null ? pitIdentifier.hashCode() : 0);
        result = 31 * result + numberOfSeeds;
        return result;
    }
}
