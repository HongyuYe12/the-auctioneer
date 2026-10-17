package model; // Defines the package for this class (data model classes)

import java.util.List; // Import List interface for storing item and NPC lists
import java.util.concurrent.CopyOnWriteArrayList; // Thread-safe List for multi-threaded auction logic

/**
 * Auction session state container
 * Stores all "dynamically changing data" during the auction process
 */
public class AuctionSession {

    // Current auction phase (state machine core variable)
    private AuctionPhase phase = AuctionPhase.IDLE; // Initial state: auction not started

    // Index of the item currently being auctioned
    private int currentIndex = 0; // Start processing from item 0

    // List of all items in the current auction (thread-safe, avoids NPC thread conflicts)
    private List<Artifact> items = new CopyOnWriteArrayList<>();

    // List of NPCs participating in the auction
    private List<NPC> npcs;

    // Current highest bid amount
    private int currentPrice = 0;

    // Current highest bidder's name (could be an NPC)
    private String currentWinner = "";

    // Hammer countdown (in seconds)
    private int hammerCountdown = 10;

    // Whether the auction is currently active
    private boolean active = false;

    // =========================
    // NEW core fields (control auction logic)
    // =========================

    /**
     * Whether there is a valid bid currently
     * true = someone has bid, false = no one has bid
     */
    private boolean hasBid = false;

    // ================= Getter / Setter =================

    // Get the current auction phase
    public AuctionPhase getPhase() {
        return phase;
    }

    // Set the current auction phase
    public void setPhase(AuctionPhase phase) {
        this.phase = phase;
    }

    // Get the current item index
    public int getCurrentIndex() {
        return currentIndex;
    }

    // Set the current item index
    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    // Get the item list
    public List<Artifact> getItems() {
        return items;
    }

    // Set the item list
    public void setItems(List<Artifact> items) {
        this.items = items;
    }

    // Get the NPC list
    public List<NPC> getNpcs() {
        return npcs;
    }

    // Set the NPC list
    public void setNpcs(List<NPC> npcs) {
        this.npcs = npcs;
    }

    // Get the current highest price
    public int getCurrentPrice() {
        return currentPrice;
    }

    // Set the current highest price
    public void setCurrentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    // Get the current highest bidder
    public String getCurrentWinner() {
        return currentWinner;
    }

    // Set the current highest bidder
    public void setCurrentWinner(String currentWinner) {
        this.currentWinner = currentWinner;
    }

    // Get the hammer countdown
    public int getHammerCountdown() {
        return hammerCountdown;
    }

    // Set the hammer countdown
    public void setHammerCountdown(int hammerCountdown) {
        this.hammerCountdown = hammerCountdown;
    }

    // Check if the auction is active
    public boolean isActive() {
        return active;
    }

    // Set whether the auction is active
    public void setActive(boolean active) {
        this.active = active;
    }

    // =========================
    // NEW fields Getter / Setter
    // =========================

    // Check if there is a bid
    public boolean isHasBid() {
        return hasBid;
    }

    // Set whether there is a bid
    public void setHasBid(boolean hasBid) {
        this.hasBid = hasBid;
    }
}