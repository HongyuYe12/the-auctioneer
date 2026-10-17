package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.GameState;
import model.Player;
import util.PoolUnlockService;
import util.ResourceUtil;
import java.util.List;

/**
 * Controller for the auction results view.
 * Displays the list of sold/unsold items and total income.
 */
public class AuctionResultController {

    @FXML private ListView<HBox> resultList;
    @FXML private Label totalIncomeLabel;

    private GameState gameState = GameState.getInstance();
    private Player player = gameState.getPlayer();
    private AuctionController auctionController = AuctionController.getInstance();

    @FXML
    public void initialize() {

        // Retrieve results from the controller
        List<AuctionStateMachine.SaleResult> results =
                auctionController.getSaleResults();

        ObservableList<HBox> items = FXCollections.observableArrayList();

        int totalIncome = 0;

        if (results == null || results.isEmpty()) {
            HBox empty = new HBox();
            empty.getChildren().add(new Label("No items were auctioned."));
            items.add(empty);
        } else {
            for (AuctionStateMachine.SaleResult sr : results) {
                HBox row = buildResultRow(sr);
                items.add(row);

                if (sr.sold) {
                    totalIncome += sr.soldPrice;
                }
            }
        }

        resultList.setItems(items);

        resultList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : item);
            }
        });

        int currentGold = GameState.getInstance().getPlayer().getGold();

        totalIncomeLabel.setText(
                "Total Income: " + String.format("%,d", totalIncome) +
                "  |  Total Gold: " + String.format("%,d", currentGold)
        );
    }

    /**
     * Builds a single row in the auction results list.
     * Each row displays the item image, name, and sale price.
     */
    private HBox buildResultRow(AuctionStateMachine.SaleResult sr) {

        HBox row = new HBox(15);
        row.setStyle("-fx-padding: 8; -fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 8;");
        row.setPrefHeight(60);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Item image
        ImageView imgView = new ImageView();
        imgView.setFitWidth(50);
        imgView.setFitHeight(50);
        imgView.setPreserveRatio(true);

        imgView.setImage(ResourceUtil.loadImageOrPlaceholder(sr.item.getName() + ".png"));

        // Item name
        Label nameLabel = new Label(sr.item.getName());
        nameLabel.setPrefWidth(200);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Sale price
        Label priceLabel = new Label();

        if (sr.sold) {
            priceLabel.setText("Sold for: " + String.format("%,d", sr.soldPrice));
            priceLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 16px;");
        } else {
            priceLabel.setText("Unsold");
            priceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 16px; -fx-font-weight: bold;");
        }

        row.getChildren().addAll(imgView, nameLabel, priceLabel);
        
        return row;
    }

    /**
     * Handles the "Finish Auction" button click.
     * Plays a coin sound, updates the status bar, navigates to the main view,
     * and refreshes the pool unlock status.
     */
    @FXML
    private void handleFinish() {
        GameState.getInstance().setAuctionCompletedToday(true);
    	MusicManager.playCoin();
    	//check if victory
    	if (player.getGold() >= 10000000) {
            GameState state = GameState.getInstance();
            state.setGameOver(true);
            state.setVictory(true);
            MainController.getInstance().loadGameOverScene(true, player.getGold(), state.getCurrentDay());
            return;
        }
        // no victory->enter next day
        MainController.getInstance().updateStatusBar();
        MainController.getInstance().showDefaultView();
        PoolUnlockService.updateUnlock(player);
    }
}
