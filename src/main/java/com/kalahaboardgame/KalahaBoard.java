package com.kalahaboardgame;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kalahaboardgame.event.EventType;
import com.kalahaboardgame.logger.ReplayableEventLogger;
import com.kalahaboardgame.pit.Pit;
import com.kalahaboardgame.pit.impl.KalahaPit;
import com.kalahaboardgame.pit.impl.NormalPit;
import com.kalahaboardgame.player.PlayerType;
import com.kalahaboardgame.referee.Referee;

/**
 * Kalaha Board.
 * This object configures Kalaha board and initialize pits and connect them together thru publisher/subscriber mechanism.
 * <p/>
 * <pre>
 *
 * The board configuration looks like the following:
 *
 *     -------------------------------------------------------------------------
 *     |             Pit12   Pit11   Pit10   Pit9    Pit8    Pit7              |
 *     |                                                                       |
 *     | KalahaPit2                                                 KalahaPit1 |
 *     |                                                                       |
 *     |             Pit1    Pit2    Pit3    Pit4    Pit5    Pit6              |
 *     -------------------------------------------------------------------------
 *
 *     Player 1 owns:  Pit 1 .. 6   +  KalahaPit1
 *     Player 2 owns:  Pit 7 .. 12  +  KalahaPit2
 *
 * </pre>
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
    private final ReplayableEventLogger replayableEventLogger;
    private final Set<Pit> pitsForPlayer1;
    private final Set<Pit> pitsForPlayer2;
    private final Set<Pit> allPits;


    public KalahaBoard(final int initialNumberOfSeeds) {
        if (initialNumberOfSeeds <= 0) {
            throw new IllegalArgumentException("initial number of seeds should be bigger than 0");
        }

        // set up pits for Player 1
        pit1 = new NormalPit(PlayerType.PLAYER_1, "Pit 1", initialNumberOfSeeds);
        pit2 = new NormalPit(PlayerType.PLAYER_1, "Pit 2", initialNumberOfSeeds);
        pit3 = new NormalPit(PlayerType.PLAYER_1, "Pit 3", initialNumberOfSeeds);
        pit4 = new NormalPit(PlayerType.PLAYER_1, "Pit 4", initialNumberOfSeeds);
        pit5 = new NormalPit(PlayerType.PLAYER_1, "Pit 5", initialNumberOfSeeds);
        pit6 = new NormalPit(PlayerType.PLAYER_1, "Pit 6", initialNumberOfSeeds);
        kalahaPitPlayer1 = new KalahaPit(PlayerType.PLAYER_1, "KalahaPit 1", 0);

        // set up pits for Player 2
        pit7 = new NormalPit(PlayerType.PLAYER_2, "Pit 7", initialNumberOfSeeds);
        pit8 = new NormalPit(PlayerType.PLAYER_2, "Pit 8", initialNumberOfSeeds);
        pit9 = new NormalPit(PlayerType.PLAYER_2, "Pit 9", initialNumberOfSeeds);
        pit10 = new NormalPit(PlayerType.PLAYER_2, "Pit 10", initialNumberOfSeeds);
        pit11 = new NormalPit(PlayerType.PLAYER_2, "Pit 11", initialNumberOfSeeds);
        pit12 = new NormalPit(PlayerType.PLAYER_2, "Pit 12", initialNumberOfSeeds);
        kalahaPitPlayer2 = new KalahaPit(PlayerType.PLAYER_2, "KalahaPit 2", 0);

        // assign pits for players
        pitsForPlayer1 = getPitsForPlayer1();
        pitsForPlayer2 = getPitsForPlayer2();
        allPits = new LinkedHashSet<>();
        allPits.addAll(pitsForPlayer1);
        allPits.addAll(pitsForPlayer2);

        // initialize observers
        referee = new Referee(getPitIdentifierForPlayer1(), getPitIdentifierForPlayer2());
        replayableEventLogger = new ReplayableEventLogger();
    }

    public void configureBoard() {
        // this is registered first to make sure that we can track events as they inserted.
        registerReplayableEventLogger();

        registerNeighbors();
        registerOpposites();
        registerReferee();

        // this is to tell observers that all pits are ready and filled with seeds (except the 2 Kalaha pits)
        publishNotEmptyEventForAllPits();
    }

    /**
     * Register neighbors.
     * <p/>
     * e.g.
     * - pit2 is observing events from pit1
     * - pit3 is observing events from pit4
     * - and so on, until we have a circular connection (a connected graph)
     */
    private void registerNeighbors() {
        final Set<EventType> eventTypes = new LinkedHashSet<>();
        eventTypes.add(EventType.INITIAL_MOVE);
        eventTypes.add(EventType.MOVE);
        eventTypes.add(EventType.LAST_MOVE);

        pit1.addObserver(eventTypes, pit2);
        pit2.addObserver(eventTypes, pit3);
        pit3.addObserver(eventTypes, pit4);
        pit4.addObserver(eventTypes, pit5);
        pit5.addObserver(eventTypes, pit6);
        pit6.addObserver(eventTypes, kalahaPitPlayer1);
        kalahaPitPlayer1.addObserver(eventTypes, pit7);
        pit7.addObserver(eventTypes, pit8);
        pit8.addObserver(eventTypes, pit9);
        pit9.addObserver(eventTypes, pit10);
        pit10.addObserver(eventTypes, pit11);
        pit11.addObserver(eventTypes, pit12);
        pit12.addObserver(eventTypes, kalahaPitPlayer2);
        kalahaPitPlayer2.addObserver(eventTypes, pit1);
    }

    /**
     * Register opposites.
     * See class documentation above for board schema.
     */
    private void registerOpposites() {
        pit1.addObserver(EventType.CAPTURE_SEEDS, pit12);
        pit12.addObserver(EventType.CAPTURE_SEEDS, pit1);
        pit2.addObserver(EventType.CAPTURE_SEEDS, pit11);
        pit11.addObserver(EventType.CAPTURE_SEEDS, pit2);
        pit3.addObserver(EventType.CAPTURE_SEEDS, pit10);
        pit10.addObserver(EventType.CAPTURE_SEEDS, pit3);
        pit4.addObserver(EventType.CAPTURE_SEEDS, pit9);
        pit9.addObserver(EventType.CAPTURE_SEEDS, pit4);
        pit5.addObserver(EventType.CAPTURE_SEEDS, pit8);
        pit8.addObserver(EventType.CAPTURE_SEEDS, pit5);
        pit6.addObserver(EventType.CAPTURE_SEEDS, pit7);
        pit7.addObserver(EventType.CAPTURE_SEEDS, pit6);

        pit1.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer2);
        pit12.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer1);
        pit2.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer2);
        pit11.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer1);
        pit3.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer2);
        pit10.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer1);
        pit4.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer2);
        pit9.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer1);
        pit5.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer2);
        pit8.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer1);
        pit6.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer2);
        pit7.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer1);
    }

    /**
     * Register referee to all pits.
     */
    private void registerReferee() {
        final Set<EventType> eventTypes = new LinkedHashSet<>();
        eventTypes.add(EventType.EMPTY);
        eventTypes.add(EventType.NOT_EMPTY);
        eventTypes.add(EventType.CHANGE_TURN);

        for (final Pit pit : allPits) {
            pit.addObserver(eventTypes, referee);
        }
    }

    /**
     * Register logger (for event replay-ability) to all pits.
     */
    private void registerReplayableEventLogger() {
        // interested in all event types
        final Set<EventType> eventTypes = new LinkedHashSet<>(Lists.newArrayList(EventType.values()));

        for (final Pit pit : allPits) {
            pit.addObserver(eventTypes, replayableEventLogger);
        }
    }

    // This is to let observers know that all pits has been filled with seeds
    private void publishNotEmptyEventForAllPits() {
        for (final Pit pit : allPits) {
            pit.publishNotEmptyEvent();
        }
    }

    // get all pits for player 1
    private Set<Pit> getPitsForPlayer1() {
        final Set<Pit> pitsForPlayer1 = new LinkedHashSet<>();
        pitsForPlayer1.add(pit1);
        pitsForPlayer1.add(pit2);
        pitsForPlayer1.add(pit3);
        pitsForPlayer1.add(pit4);
        pitsForPlayer1.add(pit5);
        pitsForPlayer1.add(pit6);
        pitsForPlayer1.add(kalahaPitPlayer1);

        return pitsForPlayer1;
    }

    // get all pits for player 2
    private Set<Pit> getPitsForPlayer2() {
        final Set<Pit> pitsForPlayer2 = new LinkedHashSet<>();
        pitsForPlayer2.add(pit7);
        pitsForPlayer2.add(pit8);
        pitsForPlayer2.add(pit9);
        pitsForPlayer2.add(pit10);
        pitsForPlayer2.add(pit11);
        pitsForPlayer2.add(pit12);
        pitsForPlayer2.add(kalahaPitPlayer2);

        return pitsForPlayer2;
    }

    // get all pits identifier for player 1
    private Set<String> getPitIdentifierForPlayer1() {
        return Sets.newHashSet(Collections2.transform(pitsForPlayer1, new Function<Pit, String>() {
            @Override
            public String apply(final Pit pit) {
                return pit.getPitIdentifier();
            }
        }));
    }

    // get all pits identifier for player 2
    private Set<String> getPitIdentifierForPlayer2() {
        return Sets.newHashSet(Collections2.transform(pitsForPlayer2, new Function<Pit, String>() {
            @Override
            public String apply(final Pit pit) {
                return pit.getPitIdentifier();
            }
        }));
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
