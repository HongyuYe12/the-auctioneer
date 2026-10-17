package util;

import model.GameState;
import model.Player;

/**
 * Pool unlock service
 */
public class PoolUnlockService
{

    public static final String POOL_1=
            "OLD_CELLAR";

    public static final String POOL_2=
            "BLACK_MARKET_SHIP";

    public static final String POOL_3=
            "ATLANTIS_RUINS";


    public static void updateUnlock(
            Player player)
    {

        GameState state =
                GameState.getInstance();

        // Default: open
        state.getUnlockedPools()
                .add(POOL_1);


        // Level 2 opens
        if(player.getAuctionLevel()>=2)
        {
            state.getUnlockedPools()
                    .add(POOL_2);
        }


        // Level 3 + Water-Repelling Pearl opens
        if(player.getAuctionLevel()>=3
                &&
           player.hasWaterBead())
        {
            state.getUnlockedPools()
                    .add(POOL_3);
        }

    }


    public static boolean isUnlocked(
            String poolName)
    {

        return GameState
                .getInstance()
                .getUnlockedPools()
                .contains(poolName);
    }

}