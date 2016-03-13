package com.ammar.kalahacorelibrary.board;

import com.ammar.kalahacorelibrary.event.EventType;
import com.ammar.kalahacorelibrary.player.PlayerType;
import com.ammar.kalahacorelibrary.pubsub.audit.ReplayableEventPublisher;
import com.ammar.kalahacorelibrary.pubsub.pit.Pit;
import com.ammar.kalahacorelibrary.pubsub.referee.Referee;

import java.util.*;

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
    private final PlayerPits player1;
    private final PlayerPits player2;
    private final Referee referee;
    private final ReplayableEventPublisher replayableEventPublisher;
    private final Map<String, Pit> allPits;

    public KalahaBoard(final int initialNumberOfSeeds) {
        if (initialNumberOfSeeds <= 0) {
            throw new IllegalArgumentException("initial number of seeds should be bigger than 0");
        }

        // setup pits for Players
        player1 = new PlayerPits(PlayerType.PLAYER_1, initialNumberOfSeeds);
        player2 = new PlayerPits(PlayerType.PLAYER_2, initialNumberOfSeeds);

        // assign pits for players
        final Map<String, Pit> pitsForPlayer1 = player1.getAllPits();
        final Map<String, Pit> pitsForPlayer2 = player2.getAllPits();
        allPits = new LinkedHashMap<>();
        allPits.putAll(pitsForPlayer1);
        allPits.putAll(pitsForPlayer2);

        // initialize observers
        referee = new Referee(pitsForPlayer1, pitsForPlayer2);
        replayableEventPublisher = new ReplayableEventPublisher();

        // configure board
        this.configureBoard();
    }

    /**
     * Configure board:
     * - register 'replayable event publisher'
     * - register pit neighbors
     * - register pit opposites
     * - register referee
     * - register publisher for not empty pit
     */
    private void configureBoard() {
        // this is registered first to make sure that we can track events as they inserted.
        registerReplayableEventPublisher();

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

        // player 1's pits
        player1.getPit1().addObserver(eventTypes, player1.getPit2());
        player1.getPit2().addObserver(eventTypes, player1.getPit3());
        player1.getPit3().addObserver(eventTypes, player1.getPit4());
        player1.getPit4().addObserver(eventTypes, player1.getPit5());
        player1.getPit5().addObserver(eventTypes, player1.getPit6());
        player1.getPit6().addObserver(eventTypes, player1.getKalahaPit());
        player1.getKalahaPit().addObserver(eventTypes, player2.getPit1());

        // player 2's pits
        player2.getPit1().addObserver(eventTypes, player2.getPit2());
        player2.getPit2().addObserver(eventTypes, player2.getPit3());
        player2.getPit3().addObserver(eventTypes, player2.getPit4());
        player2.getPit4().addObserver(eventTypes, player2.getPit5());
        player2.getPit5().addObserver(eventTypes, player2.getPit6());
        player2.getPit6().addObserver(eventTypes, player2.getKalahaPit());
        player2.getKalahaPit().addObserver(eventTypes, player1.getPit1());
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
        player1.getPit1().addObserver(EventType.CAPTURE_SEEDS, player2.getPit6());
        player2.getPit6().addObserver(EventType.STORE_SEEDS, player1.getKalahaPit());
        player2.getPit6().addObserver(EventType.CAPTURE_SEEDS, player1.getPit1());
        player1.getPit1().addObserver(EventType.STORE_SEEDS, player2.getKalahaPit());

        player1.getPit2().addObserver(EventType.CAPTURE_SEEDS, player2.getPit5());
        player2.getPit5().addObserver(EventType.STORE_SEEDS, player1.getKalahaPit());
        player2.getPit5().addObserver(EventType.CAPTURE_SEEDS, player1.getPit2());
        player1.getPit2().addObserver(EventType.STORE_SEEDS, player2.getKalahaPit());

        player1.getPit3().addObserver(EventType.CAPTURE_SEEDS, player2.getPit4());
        player2.getPit4().addObserver(EventType.STORE_SEEDS, player1.getKalahaPit());
        player2.getPit4().addObserver(EventType.CAPTURE_SEEDS, player1.getPit3());
        player1.getPit3().addObserver(EventType.STORE_SEEDS, player2.getKalahaPit());

        player1.getPit4().addObserver(EventType.CAPTURE_SEEDS, player2.getPit3());
        player2.getPit3().addObserver(EventType.STORE_SEEDS, player1.getKalahaPit());
        player2.getPit3().addObserver(EventType.CAPTURE_SEEDS, player1.getPit4());
        player1.getPit4().addObserver(EventType.STORE_SEEDS, player2.getKalahaPit());

        player1.getPit5().addObserver(EventType.CAPTURE_SEEDS, player2.getPit2());
        player2.getPit2().addObserver(EventType.STORE_SEEDS, player1.getKalahaPit());
        player2.getPit2().addObserver(EventType.CAPTURE_SEEDS, player1.getPit5());
        player1.getPit5().addObserver(EventType.STORE_SEEDS, player2.getKalahaPit());

        player1.getPit6().addObserver(EventType.CAPTURE_SEEDS, player2.getPit1());
        player2.getPit1().addObserver(EventType.STORE_SEEDS, player1.getKalahaPit());
        player2.getPit1().addObserver(EventType.CAPTURE_SEEDS, player1.getPit6());
        player1.getPit6().addObserver(EventType.STORE_SEEDS, player2.getKalahaPit());
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
     * Register replayable event publisher (for event replay-ability) to all pits and referee.
     * This is to make it possible to recreate the whole game situation from events.
     */
    private void registerReplayableEventPublisher() {
        // interested in all event types
        final Set<EventType> eventTypes = new LinkedHashSet<>(Arrays.asList(EventType.values()));
        allPits.values().stream().forEach(
                pit -> pit.addObserver(eventTypes, replayableEventPublisher)
        );
        referee.addObserver(eventTypes, replayableEventPublisher);
    }

    // This is to let observers know that all pits has been filled with seeds
    private void publishNotEmptyEventForAllPits() {
        allPits.values().forEach(Pit::publishNotEmptyEvent);
    }

    public PlayerPits getPlayer1() {
        return player1;
    }

    public PlayerPits getPlayer2() {
        return player2;
    }

    public Referee getReferee() {
        return referee;
    }

    public Map<String, Pit> getAllPits() {
        return Collections.unmodifiableMap(allPits);
    }

    public ReplayableEventPublisher getReplayableEventPublisher() {
        return replayableEventPublisher;
    }

}
