import static org.hamcrest.CoreMatchers.is;

import java.util.Map;
import java.util.Set;

import com.kalahaboardgame.KalahaBoard;
import com.kalahaboardgame.pit.impl.NormalPit;
import com.kalahaboardgame.player.PlayerType;
import com.kalahaboardgame.referee.Referee;
import org.junit.Assert;
import org.junit.Test;

/**
 * Kalaha Board Game Acceptance Tests
 * <p/>
 * Created by amhamid on 7/23/15.
 */
public class KalahaBoardGameAcceptanceTest {

    /**
     * Scenario 1:
     * Player 1: Moving 6 seeds from Pit 1
     * <p/>
     * Expectation:
     * - Pit 1 has 0 seeds after initial move
     * - Pit 2, 3, 4, 5, 6, should have 7 seeds
     * - Kalaha Pit Player 1 should have 1 seed
     * - Referee should note that Pit 1 is empty
     * - Referee should note that the rest of the Pits (2..12) excluding Kalaha, are not empty
     *
     * TODO: player 1 should be playing again (because last seed ends in his Kalaha pit)
     */
    @Test
    public void moving6SeedsFromAPit() {
        final KalahaBoard kalahaBoard = new KalahaBoard(6);
        kalahaBoard.configureBoard();

        final NormalPit pit1 = kalahaBoard.getPit1();
        pit1.initialMove();

        // assert number of seeds
        Assert.assertThat("Pit 1 should have 0 seeds", pit1.getNumberOfSeeds(), is(0));
        Assert.assertThat("Pit 2 should have 7 seeds", kalahaBoard.getPit2().getNumberOfSeeds(), is(7));
        Assert.assertThat("Pit 3 should have 7 seeds", kalahaBoard.getPit3().getNumberOfSeeds(), is(7));
        Assert.assertThat("Pit 4 should have 7 seeds", kalahaBoard.getPit4().getNumberOfSeeds(), is(7));
        Assert.assertThat("Pit 5 should have 7 seeds", kalahaBoard.getPit5().getNumberOfSeeds(), is(7));
        Assert.assertThat("Pit 6 should have 7 seeds", kalahaBoard.getPit6().getNumberOfSeeds(), is(7));
        Assert.assertThat("Kalaha Pit Player 1 should have 1 seed", kalahaBoard.getKalahaPitPlayer1().getNumberOfSeeds(), is(1));

        final Referee referee = kalahaBoard.getReferee();
        final Map<PlayerType, Set<String>> emptyPits = referee.getEmptyPits();
        final Map<PlayerType, Set<String>> notEmptyPits = referee.getNotEmptyPits();

        // assert empty pit for player 1
        final Set<String> emptyPitIdentifiers = emptyPits.get(PlayerType.PLAYER_1);
        Assert.assertThat("There should be one empty pit for player 1", emptyPitIdentifiers.size(), is(1));
        Assert.assertThat("Pit 1 should be empty pit for player 1", emptyPitIdentifiers.contains("Pit 1"), is(true));

        // assert not empty pit for player 1
        final Set<String> notEmptyPitIdentifiers = notEmptyPits.get(PlayerType.PLAYER_1);
        Assert.assertThat("There should be five not empty pits for player 1 (excluding Kalaha for player 1)", notEmptyPitIdentifiers.size(), is(5));
        Assert.assertThat("Pit 2 should be not empty pit for player 1", notEmptyPitIdentifiers.contains("Pit 2"), is(true));
        Assert.assertThat("Pit 3 should be not empty pit for player 1", notEmptyPitIdentifiers.contains("Pit 3"), is(true));
        Assert.assertThat("Pit 4 should be not empty pit for player 1", notEmptyPitIdentifiers.contains("Pit 4"), is(true));
        Assert.assertThat("Pit 5 should be not empty pit for player 1", notEmptyPitIdentifiers.contains("Pit 5"), is(true));
        Assert.assertThat("Pit 6 should be not empty pit for player 1", notEmptyPitIdentifiers.contains("Pit 6"), is(true));

        // assert empty pit for player 2
        Assert.assertThat("There should be no empty pit for player 2", emptyPits.get(PlayerType.PLAYER_2).size(), is(0));

        // assert not empty pit for player 1
        final Set<String> notEmptyPitIdentifiersForPlayer2 = notEmptyPits.get(PlayerType.PLAYER_2);
        Assert.assertThat("There should be six not empty pits for player 2 (excluding Kalaha for player 2)", notEmptyPitIdentifiersForPlayer2.size(), is(6));
        Assert.assertThat("Pit 7 should be not empty pit for player 2", notEmptyPitIdentifiersForPlayer2.contains("Pit 7"), is(true));
        Assert.assertThat("Pit 8 should be not empty pit for player 2", notEmptyPitIdentifiersForPlayer2.contains("Pit 8"), is(true));
        Assert.assertThat("Pit 9 should be not empty pit for player 2", notEmptyPitIdentifiersForPlayer2.contains("Pit 9"), is(true));
        Assert.assertThat("Pit 10 should be not empty pit for player 2", notEmptyPitIdentifiersForPlayer2.contains("Pit 10"), is(true));
        Assert.assertThat("Pit 11 should be not empty pit for player 2", notEmptyPitIdentifiersForPlayer2.contains("Pit 11"), is(true));
        Assert.assertThat("Pit 12 should be not empty pit for player 2", notEmptyPitIdentifiersForPlayer2.contains("Pit 12"), is(true));
    }

    // Scenario 2:
    // do scenario 1 from the perspective of player 2


    // Scenario 3:
    // Player 1: Moving 10 seeds from pit 6 should add 1 to its Kalaha pit and another 6 to pits opponent and skip Kalaha opponent and add 4 to his own pit (end in pit 4)
    // switch to player 2, since last seed ends not in player 1's Kalaha pit

    // Scenario 4:
    // do scenario 3 from player 2 perspective


    // Scenario 5:
    // Player 1: all of his pits is empty, except pit 6 that has 1 seed, it is his turn then he move his last seed in pit 6 to his kalaha pit, then since all pits is empty, player 1 should win
    // win should override possibility to play again, since it is over

    // Scenario 6:
    // do scenario 5 from player 2 perspective


    // Scenario 7:
    // Player 1: Pit 3 has 1 seed, pit 4 is empty, player 1 moves his 1 seed from pit 3 to pit 4 then player 1 capture his 1 seed in pit 4 and its opponent pit (who happen to have 5 pits) and
    // store these seeds (6 seeds in his kalaha pit) --> his kalaha pit has originally 10 pits, so total after this event, should be 16 pits
    // switch to player 2 for the turn

    // Scenario 8:
    // do scenario 7 from player 2 perspective

}
