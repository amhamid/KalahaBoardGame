package com.kalahaboardgame.pit.impl;

import java.util.Observable;

import com.kalahaboardgame.event.Event;
import com.kalahaboardgame.event.EventType;
import com.kalahaboardgame.event.EventUtils;
import com.kalahaboardgame.pit.Pit;
import com.kalahaboardgame.player.PlayerType;

/**
 * Normal pit is representing one of the 6 pits for each player.
 * So total in a board, there are 12 NormalPit objects.
 * <p/>
 * Created by amhamid on 7/23/15.
 */
public class NormalPit extends Pit {

    public NormalPit(final PlayerType playerType, final String pitIdentifier, final int initialNumberOfSeeds) {
        super(playerType, pitIdentifier, initialNumberOfSeeds);
    }

    @Override
    public void initialMove() {
        // get number of seeds
        final int initialNumberOfSeeds = getNumberOfSeeds();

        // remove all seeds
        removeAllSeed();

        // publish empty event
        publishEvent(getPlayerType(), EventType.EMPTY, getNumberOfSeeds());

        // publish event initial move
        publishEvent(getPlayerType(), EventType.INITIAL_MOVE, initialNumberOfSeeds);
    }

    @Override
    public void publishNotEmptyEvent() {
        publishEvent(getPlayerType(), EventType.NOT_EMPTY, getNumberOfSeeds());
    }

    public void update(Observable observable, Object object) {
        EventUtils.assertIfUpdateContainsValidEventObject(object);

        final Event event = (Event) object;
        switch (event.getEventType()) {
            case INITIAL_MOVE:
            case MOVE:
                addOneSeed();
                publishEvent(event.getPlayerType(), EventType.NOT_EMPTY, getNumberOfSeeds());

                // propagate event with original event with number of seeds - 1
                final int numberOfSeedsInTheEventThatNeedToBePropagated = event.getNumberOfSeeds() - 1;
                if (numberOfSeedsInTheEventThatNeedToBePropagated == 1) {
                    publishEvent(event.getPlayerType(), EventType.LAST_MOVE, numberOfSeedsInTheEventThatNeedToBePropagated);
                } else if (numberOfSeedsInTheEventThatNeedToBePropagated > 1) {
                    publishEvent(event.getPlayerType(), EventType.MOVE, numberOfSeedsInTheEventThatNeedToBePropagated);
                }
                break;
            case LAST_MOVE:
                if (getNumberOfSeeds() == 0) { // if current pit is empty
                    addOneSeed();
                    publishEvent(event.getPlayerType(), EventType.LAST_MOVE_EMPTY_PIT, getNumberOfSeeds());
                } else {
                    addOneSeed();
                    publishEvent(event.getPlayerType(), EventType.NOT_EMPTY, getNumberOfSeeds());
                }

                // for the last move in normal pit, switch player turn (player may only play again, when the last seed is in his own Kalaha pit)
                publishEvent(event.getPlayerType().changeTurn(), EventType.CHANGE_TURN, getNumberOfSeeds());
                break;
            default:
                break;
        }
    }

}
