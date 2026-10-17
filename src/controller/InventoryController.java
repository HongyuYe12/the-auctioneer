package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Artifact;
import model.GameState;
import model.Player;
import util.InventoryManager;
import util.ResourceUtil;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.ButtonBar;

/**
 * Inventory Controller
 * Displays all artifacts owned by the player in a grid.
 * Unidentified items provide an entry point for appraisal.
 */
public class InventoryController {

    @FXML private GridPane inventoryGrid;

    @FXML
    public void initialize() {
        refreshInventory();
        // Ensure the status bar is up to date
        MainController.getInstance().updateStatusBar();
    }

    /**
     * Refresh the inventory grid (can be called externally, e.g., after returning from appraisal).
     */
    public void refreshInventory() {
        inventoryGrid.getChildren().clear();
        Player player = GameState.getInstance().getPlayer();
        // Use InventoryManager to get inventory (returns a safe copy)
        var inventory = InventoryManager.get(player);
        if (inventory.isEmpty()) {
            // Display empty inventory message
            Label emptyLabel = new Label("Your inventory is empty.");
            emptyLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px;");
            inventoryGrid.add(emptyLabel, 0, 0);
            return;
        }

        int col = 0, row = 0;
        for (Artifact artifact : inventory) {
            Node card = buildCard(artifact);
            inventoryGrid.add(card, col, row);
            col++;
            if (col >= 4) {  // 4 items per row
                col = 0;
                row++;
            }
        }
    }

    /**
     * Build a single artifact card.
     */
    private Node buildCard(Artifact artifact) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(8));
        card.setStyle("-fx-background-color: rgba(0,0,0,0.4); -fx-background-radius: 8; " +
                      "-fx-border-color: #b8860b; -fx-border-radius: 8;");
        card.setPrefWidth(160);

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        Image img = ResourceUtil.loadImageOrPlaceholder(artifact.getName() + ".png");
        if (img != null) {
            imageView.setImage(img);
        }

        // Name
        Label nameLabel = new Label(artifact.isAppraised() ? artifact.getName() : "???");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(140);

        // Value
        String valueText = artifact.isAppraised() ?
                String.valueOf(artifact.getCurrentValue()) :
                "~" + artifact.getBasePrice();
        Label valueLabel = new Label(valueText);
        valueLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 13px;");

        // Status label (appraised / unidentified)
        Label statusLabel = new Label();
        if (!artifact.isAppraised()) {
            statusLabel.setText("🔍 Unidentified");
            statusLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-size: 12px;");
        } else {
            statusLabel.setText(artifact.isAuthentic() ? "✅ Authentic" : "❌ Fake");
            statusLabel.setStyle("-fx-text-fill: " + (artifact.isAuthentic() ? "#2ecc71" : "#e74c3c") + "; -fx-font-size: 12px;");
        }

        card.getChildren().addAll(imageView, nameLabel, valueLabel, statusLabel);

        // If unidentified, add a magnifier icon (click to trigger appraisal)
        if (!artifact.isAppraised()) {
            StackPane stack = new StackPane(card);
            // Magnifier marker
            Label magLabel = new Label("🔍");
            magLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #f39c12;");
            StackPane.setAlignment(magLabel, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(magLabel, new Insets(0, 5, 5, 0));
            stack.getChildren().add(magLabel);
            // Click the entire card to trigger appraisal
            stack.setOnMouseClicked(e -> {
                MainController.getInstance().loadAppraiseScene(artifact);
            });
            stack.setStyle("-fx-cursor: hand;");
            return stack;
        } else if (!artifact.isAuthentic()) {
            // Fake item: click to sell
            card.setOnMouseClicked(e -> sellFakeItem(artifact));
            card.setStyle(card.getStyle() + "; -fx-cursor: hand;");
            return card;
        }else {
            // Identified items show details on click
            card.setOnMouseClicked(e -> showArtifactDetail(artifact));
            card.setStyle(card.getStyle() + "; -fx-cursor: hand;");
            return card;
        }
    }

    /**
     * Sell a fake item.
     */
    private void sellFakeItem(Artifact artifact) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Sell Fake Item");
        confirm.setHeaderText("Are you sure you want to sell this fake item?");
        confirm.setContentText(
            artifact.getName() + "\n" +
            "Sell price: " + artifact.getCurrentValue() + " coins"
        );
        ButtonType yesButton = new ButtonType("Sell");
        ButtonType noButton = new ButtonType("Cancel");
        confirm.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            Player player = GameState.getInstance().getPlayer();
            // Remove from inventory
            boolean removed = InventoryManager.remove(player, artifact);
            if (removed) {
                // Add gold (fakeValue is returned by getCurrentValue() for fake items)
                player.addGold(artifact.getCurrentValue());
                MusicManager.playCoin();
                MainController.getInstance().checkVictoryAndNotify();
                // Refresh the interface
                refreshInventory();
                MainController.getInstance().updateStatusBar();
                System.out.println("Sold fake item: " + artifact.getName() + " for " + artifact.getCurrentValue());
            } else {
                System.out.println("Failed to remove item from inventory.");
            }
        }
    }

    @FXML
    private void handleSellAllFakes() {
        Player player = GameState.getInstance().getPlayer();
        List<Artifact> inventory = InventoryManager.get(player);
        
        // Collect all fake items
        List<Artifact> fakes = inventory.stream()
                .filter(a -> a.isAppraised() && !a.isAuthentic())
                .collect(Collectors.toList());
        
        if (fakes.isEmpty()) {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("No Fakes");
            info.setHeaderText(null);
            info.setContentText("You have no fake items to sell.");
            ButtonType okButton = new ButtonType("OK");
            info.getButtonTypes().setAll(okButton);
            info.showAndWait();
            return;
        }
        
        // Calculate total price
        int total = fakes.stream().mapToInt(Artifact::getCurrentValue).sum();
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Sell All Fakes");
        confirm.setHeaderText("Are you sure you want to sell all " + fakes.size() + " fake items?");
        confirm.setContentText("Total value: " + String.format("%,d", total) + " coins");
        ButtonType yesButton = new ButtonType("Sell All", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(yesButton, noButton);
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            // Remove one by one and add gold
            for (Artifact fake : fakes) {
                InventoryManager.remove(player, fake);
                player.addGold(fake.getCurrentValue());
                MusicManager.playCoin();
            }
            MainController.getInstance().checkVictoryAndNotify();
            refreshInventory();
            MainController.getInstance().updateStatusBar();
            
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Sold!");
            success.setHeaderText(null);
            success.setContentText("Sold " + fakes.size() + " fakes for " + String.format("%,d", total) + " coins.");
            ButtonType okButton = new ButtonType("OK");
            success.getButtonTypes().setAll(okButton);
            success.showAndWait();
        }
    }

    /**
     * Show brief details of an identified artifact in a pop-up.
     */
    private void showArtifactDetail(Artifact artifact) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Artifact Detail");
        alert.setHeaderText(artifact.getName());
        String content = "Rarity: " + artifact.getRarityLabel() + "\n" +
                         "Category: " + artifact.getCategory() + "\n" +
                         "Value: " + artifact.getCurrentValue() + "\n" +
                         (artifact.isAuthentic() ? "✅ Authentic" : "❌ Fake") + "\n" +
                         "Description: " + artifact.getDescription();
        alert.setContentText(content);
        ButtonType okButton = new ButtonType("OK");
        alert.getButtonTypes().setAll(okButton);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        MainController.getInstance().showDefaultView();
        MainController.getInstance().updateStatusBar();
    }
}
