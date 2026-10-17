package util;

import model.Artifact;
import model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory Manager
 * Unified management of backpack operations (secure wrapper).
 */
public class InventoryManager {

    /**
     * Adds an artifact to the player's inventory.
     */
    public static void add(Player player, Artifact artifact) {
        player.getInventory().add(artifact);
    }

    /**
     * Removes the specified artifact (only removes the first matching occurrence).
     */
    public static boolean remove(Player player, Artifact artifact) {

        List<Artifact> inventory = player.getInventory();

        for (int i = 0; i < inventory.size(); i++) {

            if (inventory.get(i).equals(artifact)) {
                inventory.remove(i);
                return true;
            }
        }

        return false;
    }

    /**
     * Retrieves the inventory (returns a copy to prevent external modification of the original data).
     */
    public static List<Artifact> get(Player player) {
        return new ArrayList<>(player.getInventory());
    }

    /**
     * Gets the size of the inventory.
     */
    public static int size(Player player) {
        return player.getInventory().size();
    }
}