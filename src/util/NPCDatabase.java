package util;

import java.util.ArrayList;
import java.util.List;

import model.NPC;

/**
 * NPC Database Class
 * Used to centrally manage all NPC data in the auction system.
 * Acts as an "in-memory database" that provides a unified NPC list.
 */
public class NPCDatabase
{

    /**
     * Gets all NPCs.
     * @return a List containing all NPC objects
     */
    public static List<NPC> getAllNPCs()
    {

        // Create an empty NPC list to store all NPC data
        List<NPC> npcs = new ArrayList<>();

        // Add first NPC: High-value collector
        npcs.add(
                new NPC(
                        "Rich Collector", // NPC name: wealthy collector
                        0.90));           // Bid inclination: 90% chance to participate (very active)

        // Add second NPC: Museum
        npcs.add(
                new NPC(
                        "National Museum", // NPC name: national museum
                        0.80));            // Bid inclination: 80% 

        // Add third NPC: Black market dealer
        npcs.add(
                new NPC(
                        "Black Market Dealer", // NPC name: black market dealer
                        0.60));                // Bid inclination: 60% 

        // Add fourth NPC: Casual buyer
        npcs.add(
                new NPC(
                        "Silent Bidder", // NPC name: Silent Bidder
                        0.60));        // Bid inclination: 60%
        npcs.add(
                new NPC(
                        "Heirloom Seeker", // NPC name: Heirloom Seeker
                        0.50));        // Bid inclination: 50% 
        npcs.add(
                new NPC(
                        "Hereditary Duchess", // NPC name: Hereditary Duchess
                        0.60));  			// Bid inclination: 60%
        npcs.add(
                new NPC(
                        "Ranger Captain", // NPC name: Ranger Captain
                        0.30));				// Bid inclination: 30%
        npcs.add(
                new NPC(
                        "Curio Broker", // NPC name: Curio Broker
                        0.80));			// Bid inclination: 80%
        npcs.add(
                new NPC(
                        "Anicient Alchemist", // NPC name: Anicient Alchemist
                        0.40));				// Bid inclination: 40%
        // Return the constructed NPC list
        return npcs;
    }
}