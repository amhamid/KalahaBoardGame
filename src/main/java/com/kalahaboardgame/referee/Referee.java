package com.kalahaboardgame.referee;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.kalahaboardgame.event.Event;
import com.kalahaboardgame.player.PlayerType;
import com.kalahaboardgame.pubsub.Observable;
import com.kalahaboardgame.pubsub.Observer;

/**
 * Referee to decide player's turn and who wins.
 * <p/>
 * Created by amhamid on 7/23/15.
 */
public class Referee implements Observer {

    private final Set<String> pitsForPlayer1;
    private final Set<String> pitsForPlayer2;
    private final Map<PlayerType, Set<String>> emptyPits;
    private final Map<PlayerType, Set<String>> notEmptyPits;
    private PlayerType currentPlayerTurn;

    public Referee(final Set<String> pitsForPlayer1, final Set<String> pitsForPlayer2) {
        this.pitsForPlayer1 = pitsForPlayer1;
        this.pitsForPlayer2 = pitsForPlayer2;

        emptyPits = new HashMap<>();
        emptyPits.put(PlayerType.PLAYER_1, new LinkedHashSet<String>());
        emptyPits.put(PlayerType.PLAYER_2, new LinkedHashSet<String>());

        notEmptyPits = new HashMap<>();
        notEmptyPits.put(PlayerType.PLAYER_1, new LinkedHashSet<String>());
        notEmptyPits.put(PlayerType.PLAYER_2, new LinkedHashSet<String>());
    }

    @Override
    public void update(final Observable observable, final Event event) {
        final String originPitIdentifier = event.getOriginPitIdentifier();
        final PlayerType playerType;
        if (pitsForPlayer1.contains(originPitIdentifier)) {
            playerType = PlayerType.PLAYER_1;
        } else if (pitsForPlayer2.contains(originPitIdentifier)) {
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
                this.currentPlayerTurn = event.getPlayerType();
                break;
            default:
                break;
        }

        // TODO publish event if player 1 or 2 has 6 empty pits !!! ==> maybe only important to publish who wins !!!

    }

    // defensive copy on set
    public Set<String> getPitsForPlayer1() {
        return Collections.unmodifiableSet(pitsForPlayer1);
    }

    // defensive copy on set
    public Set<String> getPitsForPlayer2() {
        return Collections.unmodifiableSet(pitsForPlayer2);
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
}
