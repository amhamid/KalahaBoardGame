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
import org.apache.log4j.Logger;

/**
 * Referee to decide player's turn and who wins.
 *
 * Created by amhamid on 7/23/15.
 */
public class Referee implements Observer {

    private final static Logger logger = Logger.getLogger(Referee.class);
    private final Map<PlayerType, Set<String>> emptyPits;
    private final Map<PlayerType, Set<String>> notEmptyPits;

    public Referee() {
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
        final PlayerType playerType = event.getPlayerType();
        final String originPitIdentifier = event.getOriginPitIdentifier();
        final Set<String> emptyPitIdentifiers = emptyPits.get(playerType);
        final Set<String> notEmptyPitIdentifiers = notEmptyPits.get(playerType);

        switch(event.getEventType()) {
            case EMPTY:
                emptyPitIdentifiers.add(originPitIdentifier);
                emptyPits.put(playerType, emptyPitIdentifiers);

                // also check in the notEmptyPits map and if exists also remove it from there
                notEmptyPitIdentifiers.remove(originPitIdentifier);
                notEmptyPits.put(playerType, notEmptyPitIdentifiers);

                logger.info("Referee receiving " + event);

                break;
            case NOT_EMPTY:
                notEmptyPitIdentifiers.add(originPitIdentifier);
                notEmptyPits.put(playerType, notEmptyPitIdentifiers);

                // also check in the emptyPits map and if exists also remove it from there
                emptyPitIdentifiers.remove(originPitIdentifier);
                emptyPits.put(playerType, emptyPitIdentifiers);

                logger.info("Referee receiving " + event);

                break;
            default:
                break;
        }

        // TODO publish event if player 1 or 2 has 6 empty pits !!!

    }

    // defensive copy on map
    public Map<PlayerType, Set<String>> getEmptyPits() {
        return Collections.unmodifiableMap(emptyPits);
    }

    // defensive copy on map
    public Map<PlayerType, Set<String>> getNotEmptyPits() {
        return Collections.unmodifiableMap(notEmptyPits);
    }
}
