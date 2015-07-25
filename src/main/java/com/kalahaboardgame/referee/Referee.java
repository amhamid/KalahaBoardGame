package com.kalahaboardgame.referee;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.kalahaboardgame.event.Event;
import com.kalahaboardgame.event.EventType;
import com.kalahaboardgame.pit.Pit;
import com.kalahaboardgame.player.PlayerType;
import com.kalahaboardgame.pubsub.Observable;
import com.kalahaboardgame.pubsub.Observer;

/**
 * Referee to decide player's turn and who wins.
 * <p/>
 * Created by amhamid on 7/23/15.
 */
public class Referee implements Observable, Observer {

    private final Map<String, Pit> pitsForPlayer1;
    private final Map<String, Pit> pitsForPlayer2;
    private final Map<PlayerType, Set<String>> emptyPits;
    private final Map<PlayerType, Set<String>> notEmptyPits;
    private PlayerType currentPlayerTurn;
    private PlayerType winner;
    private final Map<EventType, Set<Observer>> observerMap;

    public Referee(final Map<String, Pit> pitsForPlayer1, final Map<String, Pit> pitsForPlayer2) {
        this.pitsForPlayer1 = pitsForPlayer1;
        this.pitsForPlayer2 = pitsForPlayer2;

        emptyPits = new LinkedHashMap<>();
        emptyPits.put(PlayerType.PLAYER_1, new LinkedHashSet<String>());
        emptyPits.put(PlayerType.PLAYER_2, new LinkedHashSet<String>());

        notEmptyPits = new LinkedHashMap<>();
        notEmptyPits.put(PlayerType.PLAYER_1, new LinkedHashSet<String>());
        notEmptyPits.put(PlayerType.PLAYER_2, new LinkedHashSet<String>());

        observerMap = new LinkedHashMap<>();
    }

    @Override
    public void update(final Observable observable, final Event event) {
        final String originPitIdentifier = event.getOriginPitIdentifier();
        final PlayerType playerType;
        if (pitsForPlayer1.containsKey(originPitIdentifier)) {
            playerType = PlayerType.PLAYER_1;
        } else if (pitsForPlayer2.containsKey(originPitIdentifier)) {
            playerType = PlayerType.PLAYER_2;
        } else {
            throw new IllegalArgumentException("Pit doesn't belong to any player: " + originPitIdentifier);
        }

        final Set<String> emptyPitIdentifiers = emptyPits.get(playerType);
        final Set<String> notEmptyPitIdentifiers = notEmptyPits.get(playerType);

        switch (event.getEventType()) {
            case EMPTY:
                // add to emptyPits
                emptyPitIdentifiers.add(originPitIdentifier);
                emptyPits.put(playerType, emptyPitIdentifiers);

                // also check in the notEmptyPits map and if exists also remove it from there
                notEmptyPitIdentifiers.remove(originPitIdentifier);
                notEmptyPits.put(playerType, notEmptyPitIdentifiers);

                if (emptyPits.get(PlayerType.PLAYER_1).size() == 6) { // all pits for player 1 is empty
                    // compare Kalaha pit from player 1 with the rest of player 2
                    final int totalSeedPlayer1 = pitsForPlayer1.get("KalahaPit 1").getNumberOfSeeds();

                    int totalSeedPlayer2 = 0;
                    for (final Pit pit : pitsForPlayer2.values()) {
                        totalSeedPlayer2 += pit.getNumberOfSeeds();
                    }

                    publishWinnerEvent(totalSeedPlayer1, totalSeedPlayer2);

                } else if (emptyPits.get(PlayerType.PLAYER_2).size() == 6) { // all pits for player 2 is empty
                    // compare Kalaha pit from player 2 with the rest of player 1
                    final int totalSeedPlayer2 = pitsForPlayer2.get("KalahaPit 2").getNumberOfSeeds();

                    int totalSeedPlayer1 = 0;
                    for (final Pit pit : pitsForPlayer1.values()) {
                        totalSeedPlayer1 += pit.getNumberOfSeeds();
                    }

                    publishWinnerEvent(totalSeedPlayer1, totalSeedPlayer2);
                }
                break;
            case NOT_EMPTY:
                // add to nonEmptyPits
                notEmptyPitIdentifiers.add(originPitIdentifier);
                notEmptyPits.put(playerType, notEmptyPitIdentifiers);

                // also check in the emptyPits map and if exists also remove it from there
                emptyPitIdentifiers.remove(originPitIdentifier);
                emptyPits.put(playerType, emptyPitIdentifiers);
                break;
            case CHANGE_TURN:
                this.currentPlayerTurn = event.getPlayerType();
                break;
            default:
                break;
        }
    }

    private void publishWinnerEvent(int totalSeedPlayer1, int totalSeedPlayer2) {
        if (totalSeedPlayer1 > totalSeedPlayer2) {
            publishEvent(PlayerType.PLAYER_1, EventType.WINS, totalSeedPlayer1);
            this.winner = PlayerType.PLAYER_1;
        } else if (totalSeedPlayer2 > totalSeedPlayer1) {
            publishEvent(PlayerType.PLAYER_2, EventType.WINS, totalSeedPlayer2);
            this.winner = PlayerType.PLAYER_2;
        } else { // tie game
            publishEvent(PlayerType.PLAYER_1, EventType.TIE_GAME, totalSeedPlayer1);
        }
    }

    @Override
    public void addObserver(final EventType eventType, final Observer observer) {
        final Set<Observer> observers = new LinkedHashSet<>();
        final Set<Observer> currentObservers = this.observerMap.get(eventType);
        if (currentObservers != null) {
            observers.addAll(currentObservers);
        }
        observers.add(observer);

        this.observerMap.put(eventType, observers);
    }

    @Override
    public void addObserver(final Set<EventType> eventTypes, final Observer observer) {
        for (final EventType eventType : eventTypes) {
            addObserver(eventType, observer);
        }
    }

    @Override
    public void notifyObservers(final Event event) {
        final EventType eventType = event.getEventType();
        final Set<Observer> observers = new LinkedHashSet<>();
        final Set<Observer> currentObservers = this.observerMap.get(eventType);
        if (currentObservers != null) {
            observers.addAll(currentObservers);
        }

        for (final Observer observer : observers) {
            observer.update(this, event);
        }
    }

    private void publishEvent(final PlayerType playerType, final EventType eventType, final int numberOfSeeds) {
        final Event event = new Event(playerType, "Referee", eventType, numberOfSeeds);
        notifyObservers(event);
    }

    // defensive copy on map
    public Map<PlayerType, Set<String>> getEmptyPits() {
        return Collections.unmodifiableMap(emptyPits);
    }

    // defensive copy on map
    public Map<PlayerType, Set<String>> getNotEmptyPits() {
        return Collections.unmodifiableMap(notEmptyPits);
    }

    public PlayerType getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    public PlayerType getWinner() {
        return winner;
    }

    @Override
    public String toString() {
        return "Referee ";
    }
}
