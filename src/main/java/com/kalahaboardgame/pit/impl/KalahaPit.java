package com.kalahaboardgame.pit.impl;

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

}
