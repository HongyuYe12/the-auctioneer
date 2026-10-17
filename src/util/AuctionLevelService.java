package util;

import model.Player;

/**
 * Auction Level Upgrade Service
 * Permanently retained after upgrade
 */
public class AuctionLevelService
{

    /**
     * Automatic update detection
     */
    public static void updateLevel(
            Player player)
    {

        //=====================
        // Lv1 -> Lv2
        // coins (8000)
        //=====================

        if(player.getAuctionLevel()==1
                &&
           player.getGold()>=8000)
        {
            player.setAuctionLevel(2);

            System.out.println(
                    "Auction upgraded to Lv2"
            );
        }


        //=====================
        // Lv2 -> Lv3
        // coins (50000)
        //=====================

        if(player.getAuctionLevel()==2
                &&
           player.getGold()>=50000)
        {
            player.setAuctionLevel(3);

            System.out.println(
                    "Auction upgraded to Lv3"
            );
        }

    }

}