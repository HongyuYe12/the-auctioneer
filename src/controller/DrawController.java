package controller;

import java.util.List;

import model.Artifact;
import model.Player;
import util.ArtifactDatabase;
import util.InventoryManager;

/**
 * Draw Controller
 * Handles the "draw/gacha" logic (one of the core gameplay mechanics).
 */
public class DrawController
{

    /**
     * Draw an artifact from the specified pool.
     * @param player    the player performing the draw
     * @param poolName  the pool name (e.g., OLD_CELLAR)
     * @return the drawn artifact object, or null if failed
     */
    public Artifact draw(Player player, String poolName)
    {

        // Retrieve all artifacts for the given pool from the database
        List<Artifact> pool = ArtifactDatabase.getPool(poolName);

        // If the pool is empty, return null
        if (pool.isEmpty())
        {
            return null;
        }

        // Draw cost
        int drawCost = pool.get(0).getDrawCost();

        // Insufficient coins → failure
        if (player.getGold() < drawCost)
        {
            return null;
        }

        // Deduct coins
        player.spendGold(drawCost);
        MusicManager.playCoin();

        // =========================
        // Weighted random draw logic
        // =========================

        double totalWeight = 0;

        for (Artifact artifact : pool)
        {
            totalWeight += artifact.getDrawProbability();
        }

        double random = Math.random() * totalWeight;

        double cumulative = 0;

        for (Artifact artifact : pool)
        {
            cumulative += artifact.getDrawProbability();

            if (random <= cumulative)
            {
                // Create an independent instance (to avoid polluting the template)
                Artifact newArtifact = artifact.copy();

                // Add to inventory using the inventory manager
                InventoryManager.add(player, newArtifact);

                // Add to encyclopedia (kept)
                player.getEncyclopedia().add(newArtifact.getId());

                // =========================
                // Black Market special bonus
                // =========================
                if (poolName.equals("BLACK_MARKET_SHIP"))
                {
                    if (!player.hasWaterBead())
                    {
                        if (Math.random() < 0.01)
                        {
                            player.setWaterBead(true);
                            System.out.println("Bonus: Water Bead obtained!");
                        }
                    }
                }

                return newArtifact;
            }
        }

        return null;
    }
}