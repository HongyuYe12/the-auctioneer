package controller;

import model.Artifact;
import java.util.List;

/**
 * Auction Session Holder
 * A simple static holder for passing auction item lists between scenes.
 * Used to temporarily store the selected items for auction during scene transitions.
 */
public class AuctionSessionHolder {
    
    /** The list of artifacts to be auctioned in the current session */
    private static List<Artifact> items;
    
    /**
     * Sets the auction item list.
     * @param i the list of artifacts to auction
     */
    public static void setItems(List<Artifact> i) { 
        items = i; 
    }
    
    /**
     * Retrieves the current auction item list.
     * @return the list of artifacts, or null if not set
     */
    public static List<Artifact> getItems() { 
        return items; 
    }
}