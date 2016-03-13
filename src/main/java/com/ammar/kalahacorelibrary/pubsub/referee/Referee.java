package com.ammar.kalahacorelibrary.pubsub.referee;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.ammar.kalahacorelibrary.pubsub.Observable;
import com.ammar.kalahacorelibrary.pubsub.ObservableBase;
import com.ammar.kalahacorelibrary.pubsub.Observer;
import com.ammar.kalahacorelibrary.pubsub.pit.Pit;
import com.ammar.kalahacorelibrary.event.Event;
import com.ammar.kalahacorelibrary.event.EventType;
import com.ammar.kalahacorelibrary.player.PlayerType;

/**
 * Referee to decide player's turn and who wins.
 * <p>
 * Created by amhamid on 7/23/15.
 */
public class Referee extends ObservableBase implements Observer {

    private final Map<String, Pit> pitsForPlayer1;
    private final Map<String, Pit> pitsForPlayer2;
    private final Map<PlayerType, Set<String>> emptyPits;
    private final Map<PlayerType, Set<String>> notEmptyPits;
    private PlayerType currentPlayerTurn;
    private PlayerType winner;

    public Referee(final Map<String, Pit> pitsForPlayer1, final Map<String, Pit> pitsForPlayer2) {
        this.pitsForPlayer1 = pitsForPlayer1;
        this.pitsForPlayer2 = pitsForPlayer2;

        emptyPits = new LinkedHashMap<>();
        emptyPits.put(PlayerType.PLAYER_1, new LinkedHashSet<>());
        emptyPits.put(PlayerType.PLAYER_2, new LinkedHashSet<>());

        notEmptyPits = new LinkedHashMap<>();
        notEmptyPits.put(PlayerType.PLAYER_1, new LinkedHashSet<>());
        notEmptyPits.put(PlayerType.PLAYER_2, new LinkedHashSet<>());

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
                decideTheWinnerIfPossible();
                this.currentPlayerTurn = event.getPlayerType();
                break;
            default:
                break;
        }
    }

    private void decideTheWinnerIfPossible() {
        if (emptyPits.get(PlayerType.PLAYER_1).size() == 6) { // all pits for player 1 is empty
            // compare Kalaha pit from player 1 with the rest of player 2
            final int totalSeedPlayer1 = pitsForPlayer1.get("KalahaPit 1").getNumberOfSeeds();

            int totalSeedPlayer2 =
                    pitsForPlayer2.values().stream()
                            .mapToInt(Pit::getNumberOfSeeds)
                            .reduce(0, (total, seeds) -> total + seeds);

            publishWinnerEvent(totalSeedPlayer1, totalSeedPlayer2);
        } else if (emptyPits.get(PlayerType.PLAYER_2).size() == 6) { // all pits for player 2 is empty
            // compare Kalaha pit from player 2 with the rest of player 1
            final int totalSeedPlayer2 = pitsForPlayer2.get("KalahaPit 2").getNumberOfSeeds();

            int totalSeedPlayer1 =
                    pitsForPlayer1.values().stream()
                            .mapToInt(Pit::getNumberOfSeeds)
                            .reduce(0, (total, seeds) -> total + seeds);

            publishWinnerEvent(totalSeedPlayer1, totalSeedPlayer2);
        }
    }

    private void publishWinnerEvent(int totalSeedPlayer1, int totalSeedPlayer2) {
        if (totalSeedPlayer1 > totalSeedPlayer2) {
            publishEvent(PlayerType.PLAYER_1, EventType.WINS, totalSeedPlayer1);
            this.winner = PlayerType.PLAYER_1;
        } else if (totalSeedPlayer2 > totalSeedPlayer1) {
            publishEvent(PlayerType.PLAYER_2, EventType.WINS, totalSeedPlayer2);
            this.winner = PlayerType.PLAYER_2;
        } else {
            // tie game
            publishEvent(PlayerType.PLAYER_1, EventType.TIE_GAME, totalSeedPlayer1);
        }
    }

    @Override
    public void addObserver(final Set<EventType> eventTypes, final Observer observer) {
        eventTypes.stream().forEach(eventType -> addObserver(eventType, observer));
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
