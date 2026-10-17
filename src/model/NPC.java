package model;

/**
 * NPC Model Class
 * Represents a non-player character participating in the antique auction.
 */
public class NPC
{

    private String name; // NPC name

    // Bidding inclination (bidChance)
    // Used to indicate the probability or willingness of this NPC to participate in bidding.
    // Value range is typically 0.0 to 1.0.
    private double bidChance;

    /**
     * NPC Constructor
     * @param name      NPC name
     * @param bidChance Bidding willingness probability
     */
    public NPC(String name, double bidChance)
    {
        this.name = name;
        this.bidChance = bidChance;
    }

    /**
     * Gets the NPC name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the NPC's bidding inclination.
     * In AuctionController, this value affects the final bidding logic.
     */
    public double getBidChance()
    {
        return bidChance;
    }
}