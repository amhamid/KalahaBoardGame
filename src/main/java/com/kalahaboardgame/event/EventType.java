package com.kalahaboardgame.event;

/**
 * This is to indicate possible event types.
 * <p/>
 * Created by amhamid on 7/23/15.
 */
public enum EventType {
    /**
     * Initial move
     */
    INITIAL_MOVE,
    /**
     * Move
     */
    MOVE,
    /**
     * Last move
     */
    LAST_MOVE,
    /**
     * Capture seeds
     */
    CAPTURE_SEEDS,
    /**
     * Store seeds in Kalaha pit
     */
    STORE_SEEDS,
    /**
     * Stored successfully in Kalaha pit
     */
    STORED,
    /**
     * Change player turn
     */
    CHANGE_TURN,
    /**
     * Empty pit
     */
    EMPTY,
    /**
     * Not empty pit
     */
    NOT_EMPTY
}
