package model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import util.AuctionLevelService;
import util.PoolUnlockService;

/**
 * Global game state (Singleton)
 * Manages: days / scenes / player / pools / levels / game over
 */
public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private static GameState instance;

    // ======================
    // Core state
    // ======================
    private int currentDay = 1;
    private String currentLocation = "Auction House";

    private Player player = new Player();

    // ======================
    // Pools (ultimately determined by the level system)
    // ======================
    private Set<String> unlockedPools = new HashSet<>();

    // ======================
    // Game status
    // ======================
    private boolean gameOver = false;
    private boolean victory = false;
    // ======================
    // Auction completed flag
    // ======================
    private boolean auctionCompletedToday = false;
    public boolean isAuctionCompletedToday() {
    	return auctionCompletedToday;
    }

    public void setAuctionCompletedToday(boolean completed) {
     this.auctionCompletedToday = completed;
    }
    // ======================
    // Singleton
    // ======================
    private GameState() {
        unlockedPools.add("OLD_CELLAR");
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public static void setInstance(GameState state) {
        instance = state;
    }

    // ======================
    // Player
    // ======================
    public Player getPlayer() {
        return player;
    }

    // ======================
    // Day
    // ======================
    public int getCurrentDay() {
        return currentDay;
    }

    // ======================
    // Scene
    // ======================
    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    // ======================
    // Pools
    // ======================
    public Set<String> getUnlockedPools() {
        return unlockedPools;
    }

    // ======================
    // Game Over
    // ======================
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setVictory(boolean victory) {
        this.victory = victory;
    }
    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isVictory() {
        return victory;
    }
    
 // ======================
 // Auction skip flag
 // ======================
 private boolean auctionSkipped = false;

 public boolean isAuctionSkipped() {
     return auctionSkipped;
 }

 public void setAuctionSkipped(boolean auctionSkipped) {
     this.auctionSkipped = auctionSkipped;
 }

    // ======================
    // Next Day Flow (core fix point)
    // ======================
    public void nextDay() {

        if (gameOver) return;
        auctionSkipped = false;
        auctionCompletedToday = false;
        // Day +1
        currentDay++;

        // Restore energy
        player.setEnergy(100);

        // Reset daily purchase limits
        player.resetDailyLimit();

        // ======================
        // ⭐Level system (must update level first)
        // ======================
        AuctionLevelService.updateLevel(player);

        // ======================
        // ⭐Pool system (depends on level)
        // ======================
        unlockedPools.clear();
        PoolUnlockService.updateUnlock(player);

        // ======================
        // Auction day
        // ======================
        if (currentDay == 7 ||
            currentDay == 14 ||
            currentDay == 21 ||
            currentDay == 28) {

            currentLocation = "Auction";
            System.out.println("Auction started!");
        }
        
        

        // ======================
        // Win condition
        // ======================
        if (player.getGold() >= 10000000) {
            gameOver = true;
            victory = true;
            System.out.println("YOU WIN!");
            return;
        }

        // ======================
        // Lose condition
        // ======================
        if (currentDay > 30 && player.getGold() < 10000000) {
            gameOver = true;
            victory = false;
            System.out.println("GAME OVER");
        }

        System.out.println("Day " + currentDay + " started");
    }
}