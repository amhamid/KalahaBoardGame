package com.kalahaboardgame.pit;

import java.util.Observable;
import java.util.Observer;

import com.kalahaboardgame.event.Event;
import com.kalahaboardgame.event.EventType;
import com.kalahaboardgame.event.EventUtils;
import com.kalahaboardgame.player.PlayerType;
import org.apache.log4j.Logger;

/**
 * Interface for Pit.
 * A Pit is both Observable and Observer. The idea is that A pit should be able to generate events and process events.
 * A pit should be agnostic to other pit's location in the board. Only the Board should know the configuration for the pits location.
 * <p/>
 * Created by amhamid on 7/23/15.
 */
public abstract class Pit extends Observable implements Observer {

    private final static Logger logger = Logger.getLogger(Pit.class);

    private final PlayerType playerType;
    private final String pitIdentifier;
    private int numberOfSeeds;

    public Pit(final PlayerType playerType, final String pitIdentifier, final int initialNumberOfSeeds) {
        this.playerType = playerType;
        this.pitIdentifier = pitIdentifier;
        this.numberOfSeeds = initialNumberOfSeeds;
    }

    public abstract void initialMove();

    public void update(Observable observable, Object object) {
        EventUtils.assertIfUpdateContainsValidEventObject(object);


        final Event event = (Event) object;

        switch (event.getEventType()) {
            case INITIAL_MOVE:
            case MOVE:
                logger.info(getPitIdentifier() + ": Receiving " + event);

                // update its number of seeds + 1
                addOneSeed();

                // send not empty event
                final Event notEmptyEvent = new Event(event.getPlayerType(), this.pitIdentifier, EventType.NOT_EMPTY, getNumberOfSeeds());
                setChanged();
                notifyObservers(notEmptyEvent);

                // propagate event with original event with number of seeds - 1
                final int numberOfSeedsInTheEvent = event.getNumberOfSeeds();
                if(numberOfSeedsInTheEvent > 1) {
                    final Event propagateEvent = new Event(event.getPlayerType(), this.pitIdentifier, EventType.MOVE, numberOfSeedsInTheEvent-1);
                    setChanged();
                    notifyObservers(propagateEvent);
                }

                break;
            default:
                break;
        }
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public String getPitIdentifier() {
        return pitIdentifier;
    }

    public int getNumberOfSeeds() {
        return numberOfSeeds;
    }

    protected void setNumberOfSeeds(int numberOfSeeds) {
        this.numberOfSeeds = numberOfSeeds;
    }

    protected void addOneSeed() {
        this.numberOfSeeds++;
    }

    protected void removeOneSeed() {
        this.numberOfSeeds--;
    }

    protected void removeAllSeed() {
        this.numberOfSeeds = 0;
    }

}
