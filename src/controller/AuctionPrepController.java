package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Artifact;
import model.GameState;
import model.Player;
import util.InventoryManager;

import java.util.List;
import java.util.Optional;

public class AuctionPrepController {

    @FXML private ListView<Artifact> availableList;
    @FXML private ListView<Artifact> selectedList;
    @FXML private Label countLabel;
    @FXML private Button startBtn;
    @FXML private Button backBtn;
    @FXML private Button skipBtn;

    private Player player;
    private AuctionController auctionController = new AuctionController();
    private int maxSlots;

    // Data model (observable list for real-time updates)
    private ObservableList<Artifact> availableItems = FXCollections.observableArrayList();
    private ObservableList<Artifact> selectedItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        player = GameState.getInstance().getPlayer();
        maxSlots = auctionController.getMaxAuctionSlot(player);

        // Retrieve identified items from the player's inventory
        List<Artifact> inventory = InventoryManager.get(player);
        for (Artifact a : inventory) {
            if (a.isAppraised() && a.isAuthentic()) {
                availableItems.add(a);
            }
        }

        availableList.setItems(availableItems);
        selectedList.setItems(selectedItems);

        // Set selection mode (multiple)
        availableList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        for (Artifact a : inventory) {
            System.out.println(a.getName() + " id=" + a.getId());
        }
        updateCount();
    }

    @FXML
    private void addSelected() {

        // Copy selection (prevent UI auto-changes)
        List<Artifact> selected = List.copyOf(
                availableList.getSelectionModel().getSelectedItems()
        );

        if (selected.isEmpty()) return;

        int canAdd = maxSlots - selectedItems.size();
        if (canAdd <= 0) {
            showAlert("Slots Full", "You can only select up to " + maxSlots + " items.");
            return;
        }

        List<Artifact> toAdd = selected;
        if (selected.size() > canAdd) {
            toAdd = selected.subList(0, canAdd);
        }

        // Add first, then remove (fixed order)
        selectedItems.addAll(toAdd);
        availableItems.removeAll(toAdd);

        // Critical fix
        availableList.getSelectionModel().clearSelection();
        selectedList.getSelectionModel().clearSelection();

        updateCount();
    }
    
    @FXML
    private void removeSelected() {

        // Copy selection
        List<Artifact> selected = List.copyOf(
                selectedList.getSelectionModel().getSelectedItems()
        );

        if (selected.isEmpty()) return;

        // Move data
        selectedItems.removeAll(selected);
        availableItems.addAll(selected);

        // Critical fix (prevents auto-selection and count errors)
        selectedList.getSelectionModel().clearSelection();
        availableList.getSelectionModel().clearSelection();

        updateCount();
    }

    private void updateCount() {
        int count = selectedItems.size();
        countLabel.setText("Selected " + count + " / " + maxSlots);
        startBtn.setDisable(count == 0);
    }

    @FXML
    private void handleStartAuction() {
        if (selectedItems.isEmpty()) {
            showAlert("No Items", "Please select at least one item to auction.");
            return;
        }
        // Pass the selected items to the auction scene
        AuctionSessionHolder.setItems(selectedItems);
        MainController.getInstance().loadAuctionScene();
    }
    // ===== return button =====
    @FXML
    private void handleBack() {
        MainController.getInstance().showDefaultView();
    }

    // ===== skip button =====
    @FXML
    private void handleSkip() {
        // Set the ‘Skip’ flag; access to the preparation screen will no longer be permitted on this auction day.
    	Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Skip Auction");
        confirm.setHeaderText("Ready to skip auction this time?");
        confirm.setContentText("\nOnce you have skipped this auction, \nyou will not be permitted to re-enter it.");

        ButtonType skipButton = new ButtonType("Skip", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(skipButton, cancelButton);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == skipButton) {
        GameState.getInstance().setAuctionSkipped(true);
        MainController.getInstance().showDefaultView();
        }
    }
    private void showAlert(String title, String msg) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}