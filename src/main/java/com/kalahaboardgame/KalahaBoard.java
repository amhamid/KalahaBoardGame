package com.kalahaboardgame;

import com.kalahaboardgame.logger.ReplayAbilityLogger;
import com.kalahaboardgame.pit.impl.KalahaPit;
import com.kalahaboardgame.pit.impl.NormalPit;
import com.kalahaboardgame.player.PlayerType;
import com.kalahaboardgame.referee.Referee;

/**
 * Kalaha Board.
 * This object configure the board and initialize pits and its location.
 * <p/>
 * Created by amhamid on 7/23/15.
 */
public class KalahaBoard {

    // set up normal pit for Player 1
    private final NormalPit pit1;
    private final NormalPit pit2;
    private final NormalPit pit3;
    private final NormalPit pit4;
    private final NormalPit pit5;
    private final NormalPit pit6;
    private final KalahaPit kalahaPitPlayer1;

    // set up normal pit for Player 2
    private final NormalPit pit7;
    private final NormalPit pit8;
    private final NormalPit pit9;
    private final NormalPit pit10;
    private final NormalPit pit11;
    private final NormalPit pit12;
    private final KalahaPit kalahaPitPlayer2;

    private final Referee referee;
    private final ReplayAbilityLogger replayAbilityLogger;

    public KalahaBoard(final int initialNumberOfSeeds) {
        referee = new Referee();
        replayAbilityLogger = new ReplayAbilityLogger();

        // set up pits for Player 1
        pit1 = new NormalPit(PlayerType.PLAYER_1, "Pit 1", initialNumberOfSeeds);
        pit2 = new NormalPit(PlayerType.PLAYER_1, "Pit 2",initialNumberOfSeeds);
        pit3 = new NormalPit(PlayerType.PLAYER_1, "Pit 3",initialNumberOfSeeds);
        pit4 = new NormalPit(PlayerType.PLAYER_1, "Pit 4",initialNumberOfSeeds);
        pit5 = new NormalPit(PlayerType.PLAYER_1, "Pit 5",initialNumberOfSeeds);
        pit6 = new NormalPit(PlayerType.PLAYER_1, "Pit 6",initialNumberOfSeeds);
        kalahaPitPlayer1 = new KalahaPit(PlayerType.PLAYER_1, "KalahaPit 1", 0);

        // set up pits for Player 2
        pit7 = new NormalPit(PlayerType.PLAYER_2, "Pit 7", initialNumberOfSeeds);
        pit8 = new NormalPit(PlayerType.PLAYER_2, "Pit 8", initialNumberOfSeeds);
        pit9 = new NormalPit(PlayerType.PLAYER_2, "Pit 9", initialNumberOfSeeds);
        pit10 = new NormalPit(PlayerType.PLAYER_2, "Pit 10", initialNumberOfSeeds);
        pit11 = new NormalPit(PlayerType.PLAYER_2, "Pit 11", initialNumberOfSeeds);
        pit12 = new NormalPit(PlayerType.PLAYER_2, "Pit 12", initialNumberOfSeeds);
        kalahaPitPlayer2 = new KalahaPit(PlayerType.PLAYER_2, "KalahaPit 2", 0);
    }

    public void configureBoard() {
        registerNeighbor();
        registerReferee();
        registerReplayAbilityLogger();
    }

    /**
     * Register neighbors
     *
     * e.g.
     * - pit2 is listening on events from pit1
     * - pit3 is listening on events from pit4
     * - and so on, until we have a circular connection (a connected graph)
     */
    private void registerNeighbor() {
        pit1.addObserver(pit2);
        pit2.addObserver(pit3);
        pit3.addObserver(pit4);
        pit4.addObserver(pit5);
        pit5.addObserver(pit6);
        pit6.addObserver(kalahaPitPlayer1);
        kalahaPitPlayer1.addObserver(pit7);
        pit7.addObserver(pit8);
        pit8.addObserver(pit9);
        pit9.addObserver(pit10);
        pit10.addObserver(pit11);
        pit11.addObserver(pit12);
        pit12.addObserver(kalahaPitPlayer2);
        kalahaPitPlayer2.addObserver(pit1);
    }

    /**
     * Register referee (basically the referee listens to all pits)
     */
    private void registerReferee() {
        pit1.addObserver(referee);
        pit1.initNotEmptyEvent();

        pit2.addObserver(referee);
        pit2.initNotEmptyEvent();

        pit3.addObserver(referee);
        pit3.initNotEmptyEvent();

        pit4.addObserver(referee);
        pit4.initNotEmptyEvent();

        pit5.addObserver(referee);
        pit5.initNotEmptyEvent();

        pit6.addObserver(referee);
        pit6.initNotEmptyEvent();

        kalahaPitPlayer1.addObserver(referee);
        // kalaha pit doesn't need to send not empty event

        pit7.addObserver(referee);
        pit7.initNotEmptyEvent();

        pit8.addObserver(referee);
        pit8.initNotEmptyEvent();

        pit9.addObserver(referee);
        pit9.initNotEmptyEvent();

        pit10.addObserver(referee);
        pit10.initNotEmptyEvent();

        pit11.addObserver(referee);
        pit11.initNotEmptyEvent();

        pit12.addObserver(referee);
        pit12.initNotEmptyEvent();

        kalahaPitPlayer2.addObserver(referee);
        // kalaha pit doesn't need to send not empty event
    }

    // TODO register opposites !!

    // register logger listener (for event replay-ability)
    private void registerReplayAbilityLogger() {
        pit1.addObserver(replayAbilityLogger);
        pit2.addObserver(replayAbilityLogger);
        pit3.addObserver(replayAbilityLogger);
        pit4.addObserver(replayAbilityLogger);
        pit5.addObserver(replayAbilityLogger);
        pit6.addObserver(replayAbilityLogger);
        kalahaPitPlayer1.addObserver(replayAbilityLogger);
        pit7.addObserver(replayAbilityLogger);
        pit8.addObserver(replayAbilityLogger);
        pit9.addObserver(replayAbilityLogger);
        pit10.addObserver(replayAbilityLogger);
        pit11.addObserver(replayAbilityLogger);
        pit12.addObserver(replayAbilityLogger);
        kalahaPitPlayer2.addObserver(replayAbilityLogger);
    }

    public NormalPit getPit1() {
        return pit1;
    }

    public NormalPit getPit2() {
        return pit2;
    }

    public NormalPit getPit3() {
        return pit3;
    }

    public NormalPit getPit4() {
        return pit4;
    }

    public NormalPit getPit5() {
        return pit5;
    }

    public NormalPit getPit6() {
        return pit6;
    }

    public KalahaPit getKalahaPitPlayer1() {
        return kalahaPitPlayer1;
    }

    public NormalPit getPit7() {
        return pit7;
    }

    public NormalPit getPit8() {
        return pit8;
    }

    public NormalPit getPit9() {
        return pit9;
    }

    public NormalPit getPit10() {
        return pit10;
    }

    public NormalPit getPit11() {
        return pit11;
    }

    public NormalPit getPit12() {
        return pit12;
    }

    public KalahaPit getKalahaPitPlayer2() {
        return kalahaPitPlayer2;
    }

    public Referee getReferee() {
        return referee;
    }
}
