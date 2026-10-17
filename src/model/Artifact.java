package model;

import java.io.Serializable;
import java.util.UUID; 
/**
 * Artifact model class
 * Represents an antique item that can be drawn, appraised, and auctioned.
 */
public class Artifact implements Serializable {

    private static final long serialVersionUID = 1L;

    // =========================
    // Rarity Enum
    // =========================
    public enum Rarity {
        LOW,
        MID,
        MYTH
    }

    // =========================
    // Basic Attributes
    // =========================
    private final String id;

    private final String name;

    private final String description;

    private final String pool;

    // Artifact category (added)
    private final String category;

    private final int drawCost;

    private final double drawProbability;

    private final int basePrice;

    private final double authenticRate;

    private final int authenticValue;

    private final int fakeValue;

    private final Rarity rarity;

    // =========================
    // State Attributes
    // =========================
    private boolean appraised = false;

    private boolean authentic = false;

    /**
     * Constructor
     */
    public Artifact(
            String id,
            String name,
            String description,
            String pool,
            String category,
            int drawCost,
            double drawProbability,
            int basePrice,
            double authenticRate,
            int authenticValue,
            int fakeValue,
            Rarity rarity)
    {
        this.id=id;
        this.name=name;
        this.description=description;
        this.pool=pool;
        this.category=category;
        this.drawCost=drawCost;
        this.drawProbability=drawProbability;
        this.basePrice=basePrice;
        this.authenticRate=authenticRate;
        this.authenticValue=authenticValue;
        this.fakeValue=fakeValue;
        this.rarity=rarity;
    }
   
    /**
     * Creates a copy of this artifact (independent instance).
     */
    public Artifact copy() {
        return new Artifact(
                UUID.randomUUID().toString(),  // 生成新 ID
                name,
                description,
                pool,
                category,
                drawCost,
                drawProbability,
                basePrice,
                authenticRate,
                authenticValue,
                fakeValue,
                rarity
        );
    }

    /**
     * Performs appraisal of this artifact.
     * Determines authenticity based on the authenticRate and sets appraised flag.
     */
    public void appraise()
    {
        authentic =
                Math.random()
                < authenticRate;

        appraised=true;
    }

    /**
     * Returns the current value of the artifact.
     * If not appraised, returns the base price.
     * Otherwise returns the authentic or fake value accordingly.
     */
    public int getCurrentValue()
    {
        if(!appraised)
        {
            return basePrice;
        }

        return authentic ?
                authenticValue :
                fakeValue;
    }

    /**
     * Returns display name for inventory lists.
     * For unidentified items: shows category and price range.
     * For identified items: shows name, authenticity, and value.
     */
    public String getDisplayName()
    {
        // Unidentified
        if(!appraised)
        {
            return category
                    +" | Price:"
                    +(basePrice/2)
                    +"~"
                    +(basePrice*2);
        }

        // Identified
        return name
                +" | "
                +(authentic ?
                "Authentic" :
                "Fake")
                +" | "
                +getCurrentValue();
    }

    /**
     * Returns a formatted rarity label (e.g., [Common], [Rare], [Mythic]).
     */
    public String getRarityLabel()
    {
        switch(rarity)
        {
            case LOW:
                return "[Common]";

            case MID:
                return "[Rare]";

            case MYTH:
                return "[Mythic]";

            default:
                return "[???]";
        }
    }

    // =========================
    // Getters
    // =========================

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getPool()
    {
        return pool;
    }

    public String getCategory()
    {
        return category;
    }

    public int getDrawCost()
    {
        return drawCost;
    }

    public double getDrawProbability()
    {
        return drawProbability;
    }

    public int getBasePrice()
    {
        return basePrice;
    }

    public double getAuthenticRate()
    {
        return authenticRate;
    }

    public int getAuthenticValue()
    {
        return authenticValue;
    }

    public int getFakeValue()
    {
        return fakeValue;
    }

    public Rarity getRarity()
    {
        return rarity;
    }

    public boolean isAppraised()
    {
        return appraised;
    }

    public boolean isAuthentic()
    {
        return authentic;
    }

    // =========================
    // equals / hashCode
    // =========================

    @Override
    public boolean equals(Object o)
    {
        if(this==o)
        {
            return true;
        }

        if(!(o instanceof Artifact))
        {
            return false;
        }

        return id.equals(
                ((Artifact)o).id
        );
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    /**
     * Debug output: returns display name.
     */
    @Override
    public String toString()
    {
        return getDisplayName();
    }
}