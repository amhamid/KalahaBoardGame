package com.ammar.kalahacorelibrary.board;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.ammar.kalahacorelibrary.event.EventType;
import com.ammar.kalahacorelibrary.player.PlayerType;
import com.ammar.kalahacorelibrary.pubsub.logger.ReplayableEventLogger;
import com.ammar.kalahacorelibrary.pubsub.pit.Pit;
import com.ammar.kalahacorelibrary.pubsub.pit.impl.KalahaPit;
import com.ammar.kalahacorelibrary.pubsub.pit.impl.NormalPit;
import com.ammar.kalahacorelibrary.pubsub.referee.Referee;

/**
 * Kalaha Board.
 * This object configures Kalaha board and initialize pits and connect them together thru publisher/subscriber mechanism.
 * <p>
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
 * <p>
 * Created by amhamid on 7/23/15.
 */
public class KalahaBoard {

    private final NormalPit pit1;
    private final NormalPit pit2;
    private final NormalPit pit3;
    private final NormalPit pit4;
    private final NormalPit pit5;
    private final NormalPit pit6;
    private final NormalPit pit7;
    private final NormalPit pit8;
    private final NormalPit pit9;
    private final NormalPit pit10;
    private final NormalPit pit11;
    private final NormalPit pit12;
    private final KalahaPit kalahaPitPlayer1;
    private final KalahaPit kalahaPitPlayer2;

    private final Referee referee;
    private final ReplayableEventLogger replayableEventLogger;
    private final Map<String, Pit> allPits;


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
        final Map<String, Pit> pitsForPlayer1 = getPitsForPlayer1();
        final Map<String, Pit> pitsForPlayer2 = getPitsForPlayer2();
        allPits = new LinkedHashMap<>();
        allPits.putAll(pitsForPlayer1);
        allPits.putAll(pitsForPlayer2);

        // initialize observers
        referee = new Referee(pitsForPlayer1, pitsForPlayer2);
        replayableEventLogger = new ReplayableEventLogger();

        // configure board
        this.configureBoard();
    }

    private void configureBoard() {
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
     * <p>
     * <pre>
     *
     *     -------------------------------------------------------------------------
     *     |           Pit12 <-- Pit11 <-- Pit10 <-- Pit9  <-- Pit8  <--  Pit7     |
     *     |      |                                                          ^     |
     *     |      |                                                          \     |
     *     | KalahaPit2                                                 KalahaPit1 |
     *     |      \                                                          ^     |
     *     |      |                                                          |     |
     *     |           Pit1 --> Pit2 --> Pit3 --> Pit4 --> Pit5 -->  Pit6          |
     *     -------------------------------------------------------------------------
     *
     * Note:
     * - '-->' means publish events
     * - pit1 is publishing events and pit2 is observing events from pit1
     * - pit2 is publishing events and pit3 is observing events from pit4
     * - and so on, until we have a circular connection (a connected graph)
     * </pre>
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
     * <p>
     * <pre>
     *
     * Example of one registration:
     *
     *     --------------------------------------------------------------------------
     *     |             Pit12   Pit11   Pit10   Pit9    Pit8    Pit7               |
     *     |                      ^  |                                              |
     *     | KalahaPit2           |  |---------------------------------> KalahaPit1 |
     *     |                      |                                                 |
     *     |             Pit1    Pit2    Pit3    Pit4    Pit5    Pit6               |
     *     --------------------------------------------------------------------------
     *
     * Note:
     * - '-->' means publish events
     * - pit 2 is publishing events and pit 11 is observing events from pit1
     * - pit 11 is publishing events and KalahaPit 1 is observing events from pit 11
     * - this also applies to the other direction (from pit 11 to pit 1 and pit 1 to KalahaPit 2)
     * </pre>
     */
    private void registerOpposites() {
        pit1.addObserver(EventType.CAPTURE_SEEDS, pit12);
        pit12.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer1);
        pit12.addObserver(EventType.CAPTURE_SEEDS, pit1);
        pit1.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer2);

        pit2.addObserver(EventType.CAPTURE_SEEDS, pit11);
        pit11.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer1);
        pit11.addObserver(EventType.CAPTURE_SEEDS, pit2);
        pit2.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer2);

        pit3.addObserver(EventType.CAPTURE_SEEDS, pit10);
        pit10.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer1);
        pit10.addObserver(EventType.CAPTURE_SEEDS, pit3);
        pit3.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer2);

        pit4.addObserver(EventType.CAPTURE_SEEDS, pit9);
        pit9.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer1);
        pit9.addObserver(EventType.CAPTURE_SEEDS, pit4);
        pit4.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer2);

        pit5.addObserver(EventType.CAPTURE_SEEDS, pit8);
        pit8.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer1);
        pit8.addObserver(EventType.CAPTURE_SEEDS, pit5);
        pit5.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer2);

        pit6.addObserver(EventType.CAPTURE_SEEDS, pit7);
        pit7.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer1);
        pit7.addObserver(EventType.CAPTURE_SEEDS, pit6);
        pit6.addObserver(EventType.STORE_SEEDS, kalahaPitPlayer2);
    }

    /**
     * Register referee to all pits.
     * Referee decides which player turn and who win the game.
     */
    private void registerReferee() {
        final Set<EventType> eventTypes = new LinkedHashSet<>();
        eventTypes.add(EventType.EMPTY);
        eventTypes.add(EventType.NOT_EMPTY);
        eventTypes.add(EventType.CHANGE_TURN);

        allPits.values().stream().forEach(
                pit -> pit.addObserver(eventTypes, referee)
        );
    }

    /**
     * Register logger (for event replay-ability) to all pits.
     * This is to make it possible to recreate the whole game situation from events.
     */
    private void registerReplayableEventLogger() {
        // interested in all event types
        final Set<EventType> eventTypes = new LinkedHashSet<>(Arrays.asList(EventType.values()));
        allPits.values().stream().forEach(
                pit -> pit.addObserver(eventTypes, replayableEventLogger)
        );
        referee.addObserver(eventTypes, replayableEventLogger);
    }

    // This is to let observers know that all pits has been filled with seeds
    private void publishNotEmptyEventForAllPits() {
        allPits.values().forEach(Pit::publishNotEmptyEvent);
    }

    // get all pits for player 1
    private Map<String, Pit> getPitsForPlayer1() {
        final Map<String, Pit> pits = new LinkedHashMap<>();
        pits.put(pit1.getPitIdentifier(), pit1);
        pits.put(pit2.getPitIdentifier(), pit2);
        pits.put(pit3.getPitIdentifier(), pit3);
        pits.put(pit4.getPitIdentifier(), pit4);
        pits.put(pit5.getPitIdentifier(), pit5);
        pits.put(pit6.getPitIdentifier(), pit6);
        pits.put(kalahaPitPlayer1.getPitIdentifier(), kalahaPitPlayer1);

        return pits;
    }

    // get all pits for player 2
    private Map<String, Pit> getPitsForPlayer2() {
        final Map<String, Pit> pits = new LinkedHashMap<>();
        pits.put(pit7.getPitIdentifier(), pit7);
        pits.put(pit8.getPitIdentifier(), pit8);
        pits.put(pit9.getPitIdentifier(), pit9);
        pits.put(pit10.getPitIdentifier(), pit10);
        pits.put(pit11.getPitIdentifier(), pit11);
        pits.put(pit12.getPitIdentifier(), pit12);
        pits.put(kalahaPitPlayer2.getPitIdentifier(), kalahaPitPlayer2);

        return pits;
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

    public Map<String, Pit> getAllPits() {
        return Collections.unmodifiableMap(allPits);
    }
}
