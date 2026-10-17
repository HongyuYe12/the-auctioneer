package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.animation.Interpolator;
import javafx.scene.Node;
import model.Artifact;
import model.GameState;
import model.Player;
import java.util.Arrays;
import java.util.List;
import util.PoolUnlockService;
import util.ResourceUtil;

public class MapController {

    private GameState gameState = GameState.getInstance();
    private DrawController drawController = new DrawController();

    @FXML private Label pool1Status, pool2Status, pool3Status;
    @FXML private Button pool1Btn, pool2Btn, pool3Btn;

    @FXML
    private void handleBack() {
        MainController.getInstance().showDefaultView();
    }

    @FXML
    public void initialize() {
        refreshPoolStatus();
        MainController.getInstance().updateStatusBar();
    }

    // ========== Draw buttons for the three pools ==========
    @FXML
    private void goToPool1() {
        performDraw(PoolUnlockService.POOL_1);
    }

    @FXML
    private void goToPool2() {
        if (GameState.getInstance().getUnlockedPools().contains(PoolUnlockService.POOL_2)) {
            performDraw(PoolUnlockService.POOL_2);
        } else {
            showSimpleAlert("Pool Locked", "You need Lv.2 and 8000 coins to unlock Black Market.");
        }
    }

    @FXML
    private void goToPool3() {
        if (GameState.getInstance().getUnlockedPools().contains(PoolUnlockService.POOL_3)) {
            performDraw(PoolUnlockService.POOL_3);
        } else {
            showSimpleAlert("Pool Locked", "You need Lv.3 and the Water-Repelling Pearl to unlock Atlantis.");
        }
    }

    // ========== Core draw method ==========
    private void performDraw(String pool) {
        Player player = gameState.getPlayer();
        Artifact result = drawController.draw(player, pool);
        if (result != null) {
            boolean gotPearl = pool.equals(PoolUnlockService.POOL_2) && player.hasWaterBead();
            MainController.getInstance().updateStatusBar();
            showDrawResultDialog(result, gotPearl);
            List<Button> navBtns = Arrays.asList(pool1Btn,pool2Btn,pool3Btn);
            for (Button btn : navBtns) {
                btn.getStyleClass().add("nav-button");
            }
            refreshPoolStatus();
        } else {
            showSimpleAlert("Draw Failed", "Not enough coins!\nCost: " + getPoolCost(pool));
        }
    }

    // ========== Custom dialog: show draw result (with image) ==========
    private void showDrawResultDialog(Artifact artifact, boolean gotPearl) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Draw Result");
        dialog.setHeaderText(null);

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        content.setMinWidth(300);
        dialog.getDialogPane().setContent(content);
        // 1. Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        Image img = ResourceUtil.loadImageOrPlaceholder(artifact.getName() + ".png");

        if (img != null) {
            imageView.setImage(img);
        } else {
            // No image available, set null
            imageView.setImage(null);
        }

        // 2. Rarity label
        Label rarityLabel = new Label(artifact.getRarityLabel());
        rarityLabel.setFont(Font.font("Century", 20));
        rarityLabel.setTextFill(Color.web(getRarityColor(artifact.getRarity())));

        // 3. Name
        Label nameLabel = new Label(artifact.getName());
        nameLabel.setFont(Font.font("Century", 24));
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);

        // 4. Description
        Label descLabel = new Label(artifact.getDescription());
        descLabel.setFont(Font.font(14));
        descLabel.setStyle("-fx-text-fill: white;");
        descLabel.setWrapText(true);
        descLabel.setTextAlignment(TextAlignment.CENTER);
        descLabel.setMaxWidth(300);

        // 5. Value info
        Label valueLabel = new Label(
            "Base: " + artifact.getBasePrice() +
            "  |  Authentic: " + artifact.getAuthenticValue() +
            "  |  Fake: " + artifact.getFakeValue()
        );
        valueLabel.setFont(Font.font(14));
        valueLabel.setStyle("-fx-text-fill: white;");

        // 6. Water-Repelling Pearl bonus
        Label bonusLabel = new Label();
        if (gotPearl) {
            bonusLabel.setText("🎉 Bonus: Water-Repelling Pearl obtained!");
            bonusLabel.setFont(Font.font(16));
            descLabel.setStyle("-fx-text-fill: white;");
            bonusLabel.setTextFill(Color.CYAN);
        }

        content.getChildren().addAll(imageView, rarityLabel, nameLabel, descLabel, valueLabel);
        if (gotPearl) content.getChildren().add(bonusLabel);
        ButtonType confirmButton = new ButtonType("OK!", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(confirmButton);
        dialog.getDialogPane().setStyle("-fx-background-color: #2c3e50; -fx-padding: 10;");

        Button okBtn = (Button) dialog.getDialogPane().lookupButton(confirmButton);
        okBtn.setStyle("-fx-background-color: #f1c40f; -fx-font-weight: bold; -fx-font-size: 14px;");
        dialog.setOnShown(e -> {
            Node pane = dialog.getDialogPane();
            pane.setScaleX(0.5);
            pane.setScaleY(0.5);
            ScaleTransition st = new ScaleTransition(Duration.millis(300), pane);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
        });
        
        dialog.showAndWait();
    }

    // ========== Helper: simple alert ==========
    private void showSimpleAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ========== Get pool cost ==========
    private int getPoolCost(String pool) {
        switch (pool) {
            case PoolUnlockService.POOL_1: return 200;
            case PoolUnlockService.POOL_2: return 1500;
            case PoolUnlockService.POOL_3: return 10000;
            default: return 0;
        }
    }

    // ========== Rarity color ==========
    private String getRarityColor(Artifact.Rarity rarity) {
        switch (rarity) {
            case LOW:  return "#2ecc71";
            case MID:  return "#3498db";
            case MYTH: return "#9b59b6";
            default:   return "white";
        }
    }

    // ========== Refresh map pool status ==========
    private void refreshPoolStatus() {
        Player player = gameState.getPlayer();

        // Pool 1 always unlocked
        pool1Status.setText("✅ unlocked");
        pool1Status.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        pool1Btn.setDisable(false);
        pool1Btn.setStyle("-fx-background-color: #f1c40f; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6;");

        // Pool 2
        if (gameState.getUnlockedPools().contains(PoolUnlockService.POOL_2)) {
            pool2Status.setText("✅ unlocked");
            pool2Status.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            pool2Btn.setDisable(false);
            pool2Btn.setStyle("-fx-background-color: #f1c40f; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6;");
        } else {
            pool2Status.setText("🔒 need Lv.2(8000 coins)");
            pool2Status.setStyle("-fx-text-fill: #9d1506; -fx-font-weight: bold;");
            pool2Btn.setDisable(true);
            pool2Btn.setStyle("-fx-background-color: #7f8c8d; -fx-font-size: 14px; -fx-background-radius: 6;");
        }

        // Pool 3
        if (gameState.getUnlockedPools().contains(PoolUnlockService.POOL_3)) {
            pool3Status.setText("✅ unlocked");
            pool3Status.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            pool3Btn.setDisable(false);
            pool3Btn.setStyle("-fx-background-color: #f1c40f; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6;");
        } else {
            pool3Status.setText("🔒 need Lv.3(50000 coins)\n+ water-repelling pearl");
            pool3Status.setStyle("-fx-text-fill: #9d1506; -fx-font-weight: bold;");
            pool3Btn.setDisable(true);
            pool3Btn.setStyle("-fx-background-color: #7f8c8d; -fx-font-size: 14px; -fx-background-radius: 6;");
        }
    }
}
