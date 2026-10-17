package controller;

import java.util.Arrays;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.GameState;
import model.Player;
import util.Constants;
import util.PoolUnlockService;
/**
 * Shop Controller
 * Handles the purchase of consumables and special items in the store.
 */
public class ShopController {

    private GameState gameState = GameState.getInstance();
    private Player player = gameState.getPlayer();

    @FXML private Label milkRemain, steakRemain, pearlRemain;
    @FXML private Button milkBtn, steakBtn, pearlBtn;

    @FXML
    public void initialize() {
        refreshUI();
    }

    // ========== UI Refresh ==========
    private void refreshUI() {
        // Milk
        int milkLeft = 2 - player.getMilkBoughtToday();
        milkRemain.setText("Remaining: " + milkLeft + "/2");
        milkBtn.setDisable(player.getGold() < Constants.MILK_PRICE || milkLeft <= 0);
        milkBtn.setText(player.getGold() < Constants.MILK_PRICE ? "No coin" : (milkLeft <= 0 ? "Sold out" : "Buy"));

        // Steak
        int steakLeft = 1 - player.getSteakBoughtToday();
        steakRemain.setText("Remaining: " + steakLeft + "/1");
        steakBtn.setDisable(player.getGold() < Constants.STEAK_PRICE || steakLeft <= 0);
        steakBtn.setText(player.getGold() < Constants.STEAK_PRICE ? "No coin" : (steakLeft <= 0 ? "Sold out" : "Buy"));

        // Water-Repelling Pearl
        boolean hasPearl = player.hasWaterBead();
        pearlRemain.setText(hasPearl ? "Owned" : "Stock: 1");
        pearlBtn.setDisable(player.getGold() < Constants.WATER_BEAD_PRICE || hasPearl);
        pearlBtn.setText(player.getGold() < Constants.WATER_BEAD_PRICE ? "No coin" : (hasPearl ? "Owned" : "Buy"));
    }

    // ========== Button Event Handlers ==========
    @FXML
    private void handleBack() {
        MainController.getInstance().showDefaultView();
    }

    @FXML
    private void handleBuyMilk() {
        if (buyMilk(player)) {
            refreshUI();
            List<Button> navBtns = Arrays.asList(milkBtn);
            for (Button btn : navBtns) {
                btn.getStyleClass().add("nav-button");
            }
            MainController.getInstance().updateStatusBar();
            System.out.println("Milk purchased! +30 Energy");
        } else {
            System.out.println("Failed to buy milk");
        }
    }

    @FXML
    private void handleBuySteak() {
        if (buySteak(player)) {
            refreshUI();
            List<Button> navBtns = Arrays.asList(steakBtn);
            for (Button btn : navBtns) {
                btn.getStyleClass().add("nav-button");
            }
            MainController.getInstance().updateStatusBar();
            System.out.println("Steak purchased! +50 Energy");
        } else {
            System.out.println("Failed to buy steak");
        }
    }

    @FXML
    private void handleBuyPearl() {
    	  if (buyWaterBead(player)) {
              // Clear and recalculate the unlock pool so that Card Pool 3 takes effect immediately
              GameState.getInstance().getUnlockedPools().clear();
              PoolUnlockService.updateUnlock(player);
              refreshUI();
              MainController.getInstance().updateStatusBar();
              System.out.println("Water-Repelling Pearl purchased! Atlantis unlocked!");
          } else {
              System.out.println("Failed to buy Water-Repelling Pearl");
          }
    }

    // ========== Purchase Logic ==========
    public boolean buyMilk(Player p) {
        if (p.getMilkBoughtToday() >= 2) return false;
        if (!p.spendGold(Constants.MILK_PRICE)) return false;
        MusicManager.playCoin();
        p.setEnergy(Math.min(100, p.getEnergy() + Constants.MILK_RECOVER));
        p.addMilkBought();
        return true;
    }

    public boolean buySteak(Player p) {
        if (p.getSteakBoughtToday() >= 1) return false;
        if (!p.spendGold(Constants.STEAK_PRICE)) return false;
        MusicManager.playCoin();
        p.setEnergy(Math.min(100, p.getEnergy() + Constants.STEAK_RECOVER));
        p.addSteakBought();
        return true;
    }

    public boolean buyWaterBead(Player p) {
        if (p.hasWaterBead()) return false;
        if (!p.spendGold(Constants.WATER_BEAD_PRICE)) return false;
        MusicManager.playCoin();
        p.setWaterBead(true);
        return true;
    }
}