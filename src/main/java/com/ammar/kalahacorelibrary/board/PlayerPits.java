package com.ammar.kalahacorelibrary.board;

import com.ammar.kalahacorelibrary.player.PlayerType;
import com.ammar.kalahacorelibrary.pubsub.pit.Pit;
import com.ammar.kalahacorelibrary.pubsub.pit.impl.KalahaPit;
import com.ammar.kalahacorelibrary.pubsub.pit.impl.NormalPit;

import java.util.*;

/**
 * This class represents pits that owns by a player.
 * <p>
 * Created by ahamid on 3/13/16.
 */
public class PlayerPits {

    private final NormalPit pit1;
    private final NormalPit pit2;
    private final NormalPit pit3;
    private final NormalPit pit4;
    private final NormalPit pit5;
    private final NormalPit pit6;
    private final KalahaPit kalahaPit;
    private final Map<String, Pit> allPits;

    public PlayerPits(final PlayerType playerType, final int initialNumberOfSeeds) {
        final List<String> playerPitNames = getPlayerPitNames(playerType);
        pit1 = new NormalPit(playerType, playerPitNames.get(0), initialNumberOfSeeds);
        pit2 = new NormalPit(playerType, playerPitNames.get(1), initialNumberOfSeeds);
        pit3 = new NormalPit(playerType, playerPitNames.get(2), initialNumberOfSeeds);
        pit4 = new NormalPit(playerType, playerPitNames.get(3), initialNumberOfSeeds);
        pit5 = new NormalPit(playerType, playerPitNames.get(4), initialNumberOfSeeds);
        pit6 = new NormalPit(playerType, playerPitNames.get(5), initialNumberOfSeeds);
        kalahaPit = new KalahaPit(playerType, playerPitNames.get(6), 0);

        allPits = new LinkedHashMap<>();
        allPits.put(pit1.getPitIdentifier(), pit1);
        allPits.put(pit2.getPitIdentifier(), pit2);
        allPits.put(pit3.getPitIdentifier(), pit3);
        allPits.put(pit4.getPitIdentifier(), pit4);
        allPits.put(pit5.getPitIdentifier(), pit5);
        allPits.put(pit6.getPitIdentifier(), pit6);
        allPits.put(kalahaPit.getPitIdentifier(), kalahaPit);
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

    public KalahaPit getKalahaPit() {
        return kalahaPit;
    }

    public Map<String, Pit> getAllPits() {
        return Collections.unmodifiableMap(allPits);
    }

    private List<String> getPlayerPitNames(final PlayerType playerType) {
        final List<String> pitNames = new ArrayList<>(7);
        switch (playerType) {
            case PLAYER_1:
                pitNames.add("Pit 1");
                pitNames.add("Pit 2");
                pitNames.add("Pit 3");
                pitNames.add("Pit 4");
                pitNames.add("Pit 5");
                pitNames.add("Pit 6");
                pitNames.add("KalahaPit 1");
                break;
            case PLAYER_2:
                pitNames.add("Pit 7");
                pitNames.add("Pit 8");
                pitNames.add("Pit 9");
                pitNames.add("Pit 10");
                pitNames.add("Pit 11");
                pitNames.add("Pit 12");
                pitNames.add("KalahaPit 2");
                break;
            default:
                throw new IllegalArgumentException("Player type is not supported");
        }

        return pitNames;
    }

}
