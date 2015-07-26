package com.ammar.kalahacorelibrary.pubsub.pit.impl;

import com.ammar.kalahacorelibrary.event.Event;
import com.ammar.kalahacorelibrary.event.EventType;
import com.ammar.kalahacorelibrary.player.PlayerType;
import com.ammar.kalahacorelibrary.pubsub.Observable;
import com.ammar.kalahacorelibrary.pubsub.pit.Pit;

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
        publishEvent(getPlayerType(), EventType.EMPTY, getNumberOfSeeds());

        // publish event initial move
        publishEvent(getPlayerType(), EventType.INITIAL_MOVE, initialNumberOfSeeds);
    }

    @Override
    public void publishNotEmptyEvent() {
        publishEvent(getPlayerType(), EventType.NOT_EMPTY, getNumberOfSeeds());
    }

    @Override
    public void update(final Observable observable, final Event event) {
        switch (event.getEventType()) {
            case INITIAL_MOVE:
            case MOVE:
                
                if(shouldSendCaptureEvent(event)) {
                    // publish CAPTURE_SEEDS event when current pit is empty and event has only 1 seed and current player owns the pit
                    publishEvent(event.getPlayerType(), EventType.CAPTURE_SEEDS, 1);
                } else {
                    // propagate event with original event with number of seeds - 1
                    addOneSeed();
                    publishEvent(event.getPlayerType(), EventType.NOT_EMPTY, getNumberOfSeeds());

                    final int numberOfSeedsInTheEventThatNeedToBePropagated = event.getNumberOfSeeds() - 1;
                    if (numberOfSeedsInTheEventThatNeedToBePropagated == 1) {
                        publishEvent(event.getPlayerType(), EventType.LAST_MOVE, numberOfSeedsInTheEventThatNeedToBePropagated);
                    } else if (numberOfSeedsInTheEventThatNeedToBePropagated > 1) {
                        publishEvent(event.getPlayerType(), EventType.MOVE, numberOfSeedsInTheEventThatNeedToBePropagated);
                    }
                }
                break;
            case LAST_MOVE:
                if (shouldSendCaptureEvent(event)) { // empty pit
                    // no need to update seed with 1, since this is a trigger for CAPTURE_SEEDS event
                    publishEvent(event.getPlayerType(), EventType.CAPTURE_SEEDS, 1);
                } else {
                    addOneSeed();
                    publishEvent(event.getPlayerType(), EventType.NOT_EMPTY, getNumberOfSeeds());
                }

                // for the last move in normal pit, switch player turn (player may only play again, when the last seed is in his own Kalaha pit)
                publishEvent(event.getPlayerType().changeTurn(), EventType.CHANGE_TURN, getNumberOfSeeds());
                break;
            case CAPTURE_SEEDS:
                final int numberOfSeeds = getNumberOfSeeds();
                removeAllSeed();
                publishEvent(getPlayerType(), EventType.EMPTY, getNumberOfSeeds());
                publishEvent(event.getPlayerType(), EventType.STORE_SEEDS, numberOfSeeds + event.getNumberOfSeeds());
                break;
            default:
                break;
        }
    }

    // check if current pit is empty and event has only 1 seed and current player owns the pit
    // this is as an indication whether we should send CAPTURE event
    private boolean shouldSendCaptureEvent(Event event) {
        return getNumberOfSeeds() == 0 && event.getNumberOfSeeds() == 1 && getPlayerType() == event.getPlayerType();
    }

    // for testing purpose only !!
    // set number of seeds in Pit class has protected visibility
    public void setNumberOfSeedsForTestPurposeOnly(int numberOfSeeds) {
        super.setNumberOfSeeds(numberOfSeeds);
        if(getNumberOfSeeds() == 0) {
            publishEvent(getPlayerType(), EventType.EMPTY, getNumberOfSeeds());
        } else {
            publishEvent(getPlayerType(), EventType.NOT_EMPTY, getNumberOfSeeds());
        }
    }

    // for testing purpose only !!
    public void removeAllSeedForTestPurposeOnly() {
        setNumberOfSeedsForTestPurposeOnly(0);
    }
}
