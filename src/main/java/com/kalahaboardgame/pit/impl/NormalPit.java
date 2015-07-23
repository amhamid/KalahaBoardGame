package com.kalahaboardgame.pit.impl;

import com.kalahaboardgame.event.Event;
import com.kalahaboardgame.event.EventType;
import com.kalahaboardgame.pit.Pit;
import com.kalahaboardgame.player.PlayerType;
import org.apache.log4j.Logger;

/**
 * Normal pit is representing one of the 6 pits for each player.
 * So total in a board, there are 12 NormalPit objects.
 * <p/>
 * Created by amhamid on 7/23/15.
 */
public class NormalPit extends Pit {
    private final static Logger logger = Logger.getLogger(NormalPit.class);

    public NormalPit(final PlayerType playerType, final String pitIdentifier, final int initialNumberOfSeeds) {
        super(playerType, pitIdentifier, initialNumberOfSeeds);
    }

    // publish not empty event (after listener registration)
    public void initNotEmptyEvent() {
        final Event notEmptyEvent = new Event(getPlayerType(), getPitIdentifier(), EventType.NOT_EMPTY, getNumberOfSeeds());
        setChanged();
        notifyObservers(notEmptyEvent);
    }

    @Override
    public void initialMove() {
        // get number of seeds
        final int initialNumberOfSeeds = getNumberOfSeeds();

        // publish event initial move
        final Event initialMoveEvent = new Event(getPlayerType(), getPitIdentifier(), EventType.INITIAL_MOVE, initialNumberOfSeeds);
        setChanged();
        notifyObservers(initialMoveEvent);

        // remove all seeds
        removeAllSeed();

        // publish empty event
        final Event emptyEvent = new Event(getPlayerType(), getPitIdentifier(), EventType.EMPTY, getNumberOfSeeds());
        setChanged();
        notifyObservers(emptyEvent);
    }

//    @Override
//    public void update(Observable observable, Object object) {
//        super.update(observable, object);
//    }


}
