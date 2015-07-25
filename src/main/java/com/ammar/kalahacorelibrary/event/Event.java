package com.ammar.kalahacorelibrary.event;

import com.ammar.kalahacorelibrary.player.PlayerType;

/**
 * Represent an immutable Event object being sent around, containing information like origin pit, number of seed, event type, etc.
 * <p/>
 * Created by amhamid on 7/23/15.
 */
public class Event {
    private final PlayerType playerType;
    private final String originPitIdentifier;
    private final EventType eventType;
    private final int numberOfSeeds;

    public Event(final PlayerType playerType, final String originPitIdentifier, final EventType eventType, final int numberOfSeeds) {
        this.playerType = playerType;
        this.originPitIdentifier = originPitIdentifier;
        this.eventType = eventType;
        this.numberOfSeeds = numberOfSeeds;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public String getOriginPitIdentifier() {
        return originPitIdentifier;
    }

    public EventType getEventType() {
        return eventType;
    }

    public int getNumberOfSeeds() {
        return numberOfSeeds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (numberOfSeeds != event.numberOfSeeds) return false;
        if (playerType != event.playerType) return false;
        if (originPitIdentifier != null ? !originPitIdentifier.equals(event.originPitIdentifier) : event.originPitIdentifier != null)
            return false;
        return eventType == event.eventType;

    }

    @Override
    public int hashCode() {
        int result = playerType != null ? playerType.hashCode() : 0;
        result = 31 * result + (originPitIdentifier != null ? originPitIdentifier.hashCode() : 0);
        result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
        result = 31 * result + numberOfSeeds;
        return result;
    }

    @Override
    public String toString() {
        return "Event {" +
                "playerType=" + playerType +
                ", originPitIdentifier='" + originPitIdentifier + '\'' +
                ", eventType=" + eventType +
                ", numberOfSeeds=" + numberOfSeeds +
                '}';
    }
}
