package com.ammar.kalahacorelibrary.player;

/**
 * Player type: player 1 and player 2.
 * <p/>
 * Created by amhamid on 7/23/15.
 */
public enum PlayerType {
    PLAYER_1,
    PLAYER_2;

    public PlayerType changeTurn() {
        final PlayerType playerTurn;
        switch (this) {
            case PLAYER_1:
                playerTurn = PLAYER_2;
                break;
            case PLAYER_2:
                playerTurn = PLAYER_1;
                break;
            default:
                throw new IllegalStateException("Unknown Player type: should either be Player 1 or 2");
        }

        return playerTurn;
    }
}
