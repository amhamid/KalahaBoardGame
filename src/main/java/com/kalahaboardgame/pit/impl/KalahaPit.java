package com.kalahaboardgame.pit.impl;

import java.util.Observable;

import com.kalahaboardgame.event.Event;
import com.kalahaboardgame.event.EventType;
import com.kalahaboardgame.event.EventUtils;
import com.kalahaboardgame.pit.Pit;
import com.kalahaboardgame.player.PlayerType;

/**
 * Kalaha pit is representing one of one store pit for each player.
 * So total in a board, there are 2 KalahaPit objects.
 *
 * Created by amhamid on 7/23/15.
 */
public class KalahaPit extends Pit {

    public KalahaPit(final PlayerType playerType, final String pitIdentifier, final int initialNumberOfSeeds) {
        super(playerType, pitIdentifier, initialNumberOfSeeds);
    }

    @Override
    public void initialMove() {
        throw new IllegalStateException("Kalaha Pit should never have an initial move, it only be able to receive seeds but never be able to move them");
    }

    public void update(Observable observable, Object object) {
        EventUtils.assertIfUpdateContainsValidEventObject(object);

        final Event event = (Event) object;
        switch (event.getEventType()) {
            case INITIAL_MOVE:
            case MOVE:
                // update its number of seeds + 1
                addOneSeed();

                // propagate event with original event with number of seeds - 1
                final int numberOfSeedsInTheEvent = event.getNumberOfSeeds();
                if(numberOfSeedsInTheEvent ==  2) { // this means that the next move is the last move
                    final Event lastMoveEvent = new Event(event.getPlayerType(), getPitIdentifier(), EventType.LAST_MOVE, numberOfSeedsInTheEvent - 1);
                    setChanged();
                    notifyObservers(lastMoveEvent);
                } else if (numberOfSeedsInTheEvent > 2) {
                    final Event moveEvent = new Event(event.getPlayerType(), getPitIdentifier(), EventType.MOVE, numberOfSeedsInTheEvent - 1);
                    setChanged();
                    notifyObservers(moveEvent);
                }

                break;
            case LAST_MOVE:
                addOneSeed();
                // for the last move in kalaha pit, the current player can play again
                publishEvent(event.getPlayerType(), EventType.CHANGE_TURN, getNumberOfSeeds());

                break;
            default:
                break;
        }
    }

}
