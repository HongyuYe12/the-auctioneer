package util;

/**
 * Game Constants Configuration Class
 * Centralizes global game parameters to avoid hard-coding and facilitate balance adjustments.
 */
public final class Constants {

    private Constants() {} // Private constructor to prevent instantiation (pure constant utility class)

    // Game progress control
    public static final int MAX_DAY = 30; // Maximum number of game days

    // Initial resource settings
    public static final int START_GOLD = 5000;   // Player starting gold
    public static final int START_ENERGY = 100;  // Player starting energy

    // Win condition
    public static final int WIN_GOLD = 10_000_000; // Total gold required to achieve victory

    // Shop item price configuration
    public static final int MILK_PRICE = 3000;        // Milk price
    public static final int STEAK_PRICE = 6000;       // Steak price
    public static final int WATER_BEAD_PRICE = 30000; // Water-Repelling Pearl price

    // Consumable recovery effect configuration
    public static final int MILK_RECOVER = 30;  // Energy restored by milk
    public static final int STEAK_RECOVER = 50; // Energy restored by steak

    // Appraisal system cost configuration
    public static final int LOW_APPRAISE_COST = 20;   // Cost to appraise a low-rarity artifact
    public static final int MID_APPRAISE_COST = 40;   // Cost to appraise a mid-rarity artifact
    public static final int MYTH_APPRAISE_COST = 70;  // Cost to appraise a mythic-rarity artifact
}