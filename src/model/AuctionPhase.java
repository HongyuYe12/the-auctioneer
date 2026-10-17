package model;

/**
 * Auction Phase State Machine
 * Represents the different stages of an auction session.
 */
public enum AuctionPhase {
    IDLE,           // Not yet in auction
    PREPARE,        // Preparation phase (player listing items)
    AUCTIONING,     // Auction in progress
    BIDDING,        // NPC bidding phase
    WAITING_HAMMER, // 10-second hammer window
    ITEM_END,       // Single item ended
    FINISHED        // Auction finished
}