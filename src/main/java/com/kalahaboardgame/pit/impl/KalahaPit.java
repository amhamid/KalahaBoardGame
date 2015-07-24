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
 * <p/>
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
                final int numberOfEventNeedToBePropagated;
                // only update Kalaha pit when current player is the owner of the Kalaha pit
                if(getPlayerType() == event.getPlayerType()) {
                    addOneSeed();
                    numberOfEventNeedToBePropagated = event.getNumberOfSeeds() - 1;
                } else {
                    // propagate the last move event, since player is not the owner of the kalaha pit
                    numberOfEventNeedToBePropagated = event.getNumberOfSeeds();
                }

                if (numberOfEventNeedToBePropagated == 1) { // this means that the next move is the last move
                    publishEvent(event.getPlayerType(), EventType.LAST_MOVE, numberOfEventNeedToBePropagated);
                } else if (numberOfEventNeedToBePropagated > 1) {
                    publishEvent(event.getPlayerType(), EventType.MOVE, numberOfEventNeedToBePropagated);
                }

                break;
            case LAST_MOVE:
                if(getPlayerType() == event.getPlayerType()) {
                    addOneSeed();
                    // for the last move in kalaha pit, the current player can play again
                    publishEvent(event.getPlayerType(), EventType.CHANGE_TURN, event.getNumberOfSeeds()); // in CHANGE_TURN, number of seed is ignored
                } else {
                    // propagate the last move event, since player is not the owner of the kalaha pit
                    publishEvent(event.getPlayerType(), EventType.LAST_MOVE, event.getNumberOfSeeds());
                }

                break;
            default:
                break;
        }
    }

}
