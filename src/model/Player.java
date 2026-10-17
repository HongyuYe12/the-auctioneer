package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Player Class
 * Stores player personal data (only "personal attributes").
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    // ======================
    // Basic Attributes
    // ======================
    private int gold;
    private int energy;
    private int auctionLevel;

    // ======================
    // Data Systems
    // ======================
    private List<Artifact> inventory;
    private Set<String> encyclopedia;

    // ======================
    // Special Items
    // ======================
    private boolean hasWaterBead;

    // ======================
    // Daily Purchase Limits (per player)
    // ======================
    private int milkBoughtToday;
    private int steakBoughtToday;

    // ======================
    // Constructor
    // ======================
    public Player() {
        gold = 5000;
        energy = 100;
        auctionLevel = 1;

        inventory = new ArrayList<>();
        encyclopedia = new HashSet<>();
    }

    // ======================
    // Gold
    // ======================
    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public boolean spendGold(int amount) {
        if (gold < amount) return false;
        gold -= amount;
        return true;
    }

    // ======================
    // Energy
    // ======================
    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    // ======================
    // Level
    // ======================
    public int getAuctionLevel() {
        return auctionLevel;
    }

    public void setAuctionLevel(int auctionLevel) {
        this.auctionLevel = auctionLevel;
    }

    // ======================
    // Inventory (Backpack)
    // ======================
    public List<Artifact> getInventory() {
        return inventory;
    }

    // ======================
    // Encyclopedia
    // ======================
    public Set<String> getEncyclopedia() {
        return encyclopedia;
    }

    // ======================
    // Water-Repelling Pearl (permanent item)
    // ======================
    public boolean hasWaterBead() {
        return hasWaterBead;
    }

    public void setWaterBead(boolean hasWaterBead) {
        this.hasWaterBead = hasWaterBead;
    }

    // ======================
    // Purchase Limit System (per player)
    // ======================
    public int getMilkBoughtToday() {
        return milkBoughtToday;
    }

    public void addMilkBought() {
        milkBoughtToday++;
    }

    public int getSteakBoughtToday() {
        return steakBoughtToday;
    }

    public void addSteakBought() {
        steakBoughtToday++;
    }

    public void resetDailyLimit() {
        milkBoughtToday = 0;
        steakBoughtToday = 0;
    }
}