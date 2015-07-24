package com.kalahaboardgame.referee;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import com.kalahaboardgame.event.Event;
import com.kalahaboardgame.event.EventUtils;
import com.kalahaboardgame.player.PlayerType;

/**
 * Referee to decide player's turn and who wins.
 *
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

        emptyPits = new HashMap<PlayerType, Set<String>>();
        emptyPits.put(PlayerType.PLAYER_1, new HashSet<String>());
        emptyPits.put(PlayerType.PLAYER_2, new HashSet<String>());

        notEmptyPits = new HashMap<PlayerType, Set<String>>();
        notEmptyPits.put(PlayerType.PLAYER_1, new HashSet<String>());
        notEmptyPits.put(PlayerType.PLAYER_2, new HashSet<String>());
    }

    public void update(Observable observable, Object object) {
        EventUtils.assertIfUpdateContainsValidEventObject(object);

        final Event event = (Event) object;
        final String originPitIdentifier = event.getOriginPitIdentifier();

        final PlayerType playerType;
        if(pitsForPlayer1.contains(originPitIdentifier)) {
            playerType = PlayerType.PLAYER_1;
        } else if(pitsForPlayer2.contains(originPitIdentifier)) {
            playerType = PlayerType.PLAYER_2;
        } else {
            throw new IllegalArgumentException("Pit doesn't belong to any player: " +originPitIdentifier);
        }

        final Set<String> emptyPitIdentifiers = emptyPits.get(playerType);
        final Set<String> notEmptyPitIdentifiers = notEmptyPits.get(playerType);

        switch(event.getEventType()) {
            case EMPTY:
                emptyPitIdentifiers.add(originPitIdentifier);
                emptyPits.put(playerType, emptyPitIdentifiers);

                // also check in the notEmptyPits map and if exists also remove it from there
                notEmptyPitIdentifiers.remove(originPitIdentifier);
                notEmptyPits.put(playerType, notEmptyPitIdentifiers);

                break;
            case NOT_EMPTY:
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

        // TODO publish event if player 1 or 2 has 6 empty pits !!!

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
