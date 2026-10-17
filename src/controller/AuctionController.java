package controller;

import java.util.List;

import model.Artifact;
import model.GameState;
import model.NPC;
import model.Player;

/**
 * Auction Controller
 * Responsible only for entry control, not complex logic.
 */
public class AuctionController
{
	 private static AuctionController instance;

	    public AuctionController() {
	        instance = this;
	    }

	    public static AuctionController getInstance() {
	        return instance;
	    }
    // Auction state machine instance
    private AuctionStateMachine machine;

    /**
     * Gets the maximum number of items that can be listed.
     */
    public int getMaxAuctionSlot(Player player)
    {
        switch(player.getAuctionLevel())
        {
            case 1: return 3;
            case 2: return 6;
            case 3: return 9;
            default: return 3;
        }
    }

    /**
     * Checks if today is an auction day.
     */
    public boolean isAuctionDay()
    {
        int day = GameState.getInstance().getCurrentDay();

        return day == 7
                || day == 14
                || day == 21
                || day == 28;
    }

    /**
     * Starts the auction.
     */
    public int auction(
            Player player,
            List<Artifact> artifacts,
            List<NPC> npcs)
    {
        if (!isAuctionDay())
            return 0;

        if (artifacts == null || artifacts.isEmpty())
            return 0;

        if (artifacts.size() > getMaxAuctionSlot(player))
            return 0;

        List<Artifact> validArtifacts =
                artifacts.stream()
                        .filter(Artifact::isAppraised)
                        .toList();

        if (validArtifacts.isEmpty())
            return 0;

        machine = new AuctionStateMachine(player);
        machine.startAuction(validArtifacts, npcs);

        return 1;
    }

    /**
     * Player hammers down.
     */
    public void playerHammer()
    {
        if (machine != null)
        {
            machine.playerHammer();
        }
    }

    /**
     * Gets the current auction session (for UI).
     */
    public model.AuctionSession getSession()
    {
        return machine == null ? null : machine.getSession();
    }

    /**
     * Retrieves auction results (critical fix).
     */
    public List<AuctionStateMachine.SaleResult> getSaleResults()
    {
        return machine == null ? null : machine.getSaleResults();
    }
}