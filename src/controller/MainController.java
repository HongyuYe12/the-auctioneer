package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import model.Artifact;
import model.GameState;
import model.Player;
import java.io.IOException;
import javafx.application.Platform;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MainController {

    // ===== FXML Injection =====
    @FXML private Label dayLabel;
    @FXML private Label energyLabel;
    @FXML private Label goldLabel;
    @FXML private Label levelLabel;
    @FXML private Label locationLabel;
    @FXML private StackPane contentArea;
    @FXML private Label unlockLabel;
    @FXML private Label overlayLabel;
    @FXML private Circle todayCircle;
    @FXML private Label auctionCountdownLabel;
    @FXML private Button introduceBtn, mapBtn, shopBtn, inventoryBtn, endDayBtn;
    @FXML private StackPane bottomNavBar;

    // ===== Other Members =====
    private Timeline overlayTimeline;

    private static MainController instance;
    private final Map<Integer, Point2D> calendarPos = new HashMap<>();
    private final int[] auctionDays = {7,14,21,28};

    // ===== Singleton =====
    public static MainController getInstance() {
        return instance;
    }

    // ===== Initialization =====
    @FXML
    public void initialize() {
        instance = this;

        initCalendar(); 
        updateStatusBar();
        updateCalendar();
        // Add style classes to bottom buttons
        List<Button> navBtns = Arrays.asList(introduceBtn, mapBtn, shopBtn, inventoryBtn, endDayBtn);
        for (Button btn : navBtns) {
            btn.getStyleClass().add("nav-button");
        }

        loadStylesheet();

        // Show Day 1 on startup
        Platform.runLater(() -> {
            int day = GameState.getInstance().getCurrentDay();
            showOverlay("Day " + day, 1);
        });
    }

    // ===== Stylesheet Loading =====
    private void loadStylesheet() {
        Scene scene = contentArea.getScene();
        if (scene != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } else {
            contentArea.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                }
            });
        }
    }

    // ===== Bottom Navigation Bar Control =====
    public void setBottomNavVisible(boolean visible) {
        if (bottomNavBar != null) {
            bottomNavBar.setVisible(visible);
            bottomNavBar.setManaged(visible);
        }
    }

    // ===== Default Main View =====
    public void showDefaultView() {
        try {
            contentArea.getChildren().clear();
            setLocation("Auction");
            updateStatusBar();
            setBottomNavVisible(true);
            
         // Check whether it is the auction day and has not been skipped and today's auction has not been done
            GameState state = GameState.getInstance();
            int day = state.getCurrentDay();
            if (day % 7 == 0 && day >= 7 && !state.isAuctionSkipped() && !state.isAuctionCompletedToday()) {
                // return button
                Button backToPrep = new Button("📋 Back to Auction Prep");
                backToPrep.setStyle("-fx-font-size: 20px; -fx-background-color: #f1c40f; -fx-font-weight: bold; -fx-background-radius: 12; -fx-padding: 10 20;");
                backToPrep.setOnAction(e -> loadAuctionPrepScene());
                
                StackPane.setAlignment(backToPrep, javafx.geometry.Pos.CENTER);
                contentArea.getChildren().add(backToPrep);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Status Bar Update =====
    public void updateStatusBar() {
        GameState state = GameState.getInstance();
        if (state == null) return;
        Player player = state.getPlayer();
        if (player == null) return;
        String newGold = "Coin: " + String.format("%,d", player.getGold());
        String newEnergy = "Energy: " + player.getEnergy() + "/100";
        animateValueChange(goldLabel, newGold);
        animateValueChange(energyLabel, newEnergy);
        dayLabel.setText("Day " + state.getCurrentDay());
        levelLabel.setText("Lv." + player.getAuctionLevel());
    }

    // ===== Navigation Methods =====
    @FXML public void goToMap() {
        loadScene("/view/MapView.fxml");
        setLocation("Map");
        setBottomNavVisible(true);
    }

    @FXML public void goToShop() {
        loadScene("/view/ShopView.fxml");
        setLocation("Shop");
        setBottomNavVisible(true);
    }

    @FXML public void goToInventory() {
        loadScene("/view/InventoryView.fxml");
        setLocation("Inventory");
        setBottomNavVisible(true);
    }

    @FXML public void goToIntroduce() {
        loadIntroduceScene();
        setLocation("Introduction");
        setBottomNavVisible(true);
    }

    // ===== Scene Loading (Generic) =====
    public void loadScene(String fxmlPath) {
        try {
            Parent newScene = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(newScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed: " + fxmlPath);
        }
    }

    // ===== Specific Scene Loading =====
    public void loadInventoryScene() {
        loadScene("/view/InventoryView.fxml");
        setLocation("Inventory");
        setBottomNavVisible(true);
        updateStatusBar();
    }

    public void loadIntroduceScene() {
        loadScene("/view/IntroduceView.fxml");
        setBottomNavVisible(true);
    }

    public void loadAppraiseScene(Artifact artifact) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AppraiseView.fxml"));
            Parent root = loader.load();
            AppraiseController controller = loader.getController();
            controller.initData(artifact);
            contentArea.getChildren().setAll(root);
            setLocation("Appraisal Room");
            setBottomNavVisible(false);
            updateStatusBar();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load appraisal view");
            alert.setContentText("Please check if AppraiseView.fxml exists.");
            alert.showAndWait();
        }
    }

    public void loadAuctionPrepScene() {
        GameState.getInstance().setAuctionCompletedToday(false);
        loadScene("/view/AuctionPrepView.fxml");
        setLocation("Auction House");
        setBottomNavVisible(false);
    }

    public void loadAuctionScene() {
        loadScene("/view/AuctionView.fxml");
        setLocation("Auction House");
        setBottomNavVisible(false);
    }

    public void loadAuctionResultScene() {
        loadScene("/view/AuctionResultView.fxml");
        setLocation("Auction House");
        setBottomNavVisible(false);
    }

    public void loadGameOverScene(boolean victory, int finalGold, int finalDay) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GameOverView.fxml"));
            Parent root = loader.load();
            GameOverController controller = loader.getController();
            controller.setResultData(victory, finalGold, finalDay);
            contentArea.getChildren().setAll(root);
            setLocation("Game Over");
            setBottomNavVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== Location Label =====
    public void setLocation(String text) {
        locationLabel.setText("📍 " + text);
    }

    // ===== Core: Advance Day (Shared Logic) =====
    private void advanceDayInternal() {
        GameState.getInstance().nextDay();
        GameState state = GameState.getInstance();

        // Check game over
        if (state.isGameOver()) {
            Player player = state.getPlayer();
            loadGameOverScene(state.isVictory(), player.getGold(), state.getCurrentDay());
            return;
        }

        // Normal progression
        int day = state.getCurrentDay();
        String overlayText = "Day " + day;
        if (day % 7 == 0 && day >= 7) {
            overlayText = "\nAUCTION DAY!\n\t" + overlayText;
        }
        showOverlay(overlayText, 1);

        if (day % 7 == 0 && day >= 7 && !state.isAuctionSkipped()) {
            loadAuctionPrepScene();
        } else {
            showDefaultView();
        }
        updateStatusBar();
        updateCalendar();
    }

    // ===== End Day (with confirmation dialog) =====
    @FXML
    public void handleEndDay() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("End Day");
        confirm.setHeaderText("Ready to end today?");
        confirm.setContentText("Your energy will be reset to 100\nProgress will carry over to tomorrow.");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirm.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == yesButton) {
            System.out.println("User confirmed, proceeding to next day.");
            advanceDayInternal();
        } else {
            System.out.println("User cancelled or closed dialog.");
        }
    }

    // ===== Silent Advance (called after auction ends) =====
    public void advanceDayWithoutConfirmation() {
        System.out.println("Advancing day silently from auction result.");
        advanceDayInternal();
    }
    /**
     * Check in real time whether the victory conditions have been met; if so, display the victory screen immediately.
     */
    public void checkVictoryAndNotify() {
        GameState state = GameState.getInstance();
        if (state.isGameOver()) return; // skip when already end

        Player player = state.getPlayer();
        if (player.getGold() >= 10000000) {
            state.setGameOver(true);
            state.setVictory(true);
            // directly load GameOverView
            loadGameOverScene(true, player.getGold(), state.getCurrentDay());
        }
    }
    // ===== Overlay Caption =====
    public void showOverlay(String text, int durationSeconds) {
        if (overlayLabel == null) return;
        overlayLabel.setText(text);
        overlayLabel.setVisible(true);
        // Cancel previous timer task
        if (overlayTimeline != null) {
            overlayTimeline.stop();
        }
        overlayTimeline = new Timeline(new KeyFrame(Duration.seconds(durationSeconds), e -> {
            overlayLabel.setVisible(false);
        }));
        overlayTimeline.play();
    }

    // ===== Helper: Value Change Animation =====
    private void animateValueChange(Label label, String newText) {
        if (label.getText().equals(newText)) return;
        label.setText(newText);

        ScaleTransition scale = new ScaleTransition(Duration.millis(200), label);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);

        Timeline colorTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, e -> label.setTextFill(Color.GOLD)),
            new KeyFrame(Duration.millis(100), e -> label.setTextFill(Color.web("#f1c40f"))),
            new KeyFrame(Duration.millis(200), e -> label.setTextFill(Color.WHITE))
        );

        scale.play();
        colorTimeline.play();
    }

    private void initCalendar(){

        calendarPos.put(1,new Point2D(540,150));
        calendarPos.put(2,new Point2D(560,150));
        calendarPos.put(3,new Point2D(580,150));
        calendarPos.put(4,new Point2D(600,150));
        calendarPos.put(5,new Point2D(620,150));
        calendarPos.put(6,new Point2D(640,150));

        calendarPos.put(7,new Point2D(520,166));
        calendarPos.put(8,new Point2D(540,166));
        calendarPos.put(9,new Point2D(560,166));
        calendarPos.put(10,new Point2D(580,166));
        calendarPos.put(11,new Point2D(600,166));
        calendarPos.put(12,new Point2D(620,166));
        calendarPos.put(13,new Point2D(640,166));

        calendarPos.put(14,new Point2D(520,182));
        calendarPos.put(15,new Point2D(540,182));
        calendarPos.put(16,new Point2D(560,182));
        calendarPos.put(17,new Point2D(580,182));
        calendarPos.put(18,new Point2D(600,182));
        calendarPos.put(19,new Point2D(620,182));
        calendarPos.put(20,new Point2D(640,182));

        calendarPos.put(21,new Point2D(520,198));
        calendarPos.put(22,new Point2D(540,198));
        calendarPos.put(23,new Point2D(560,198));
        calendarPos.put(24,new Point2D(580,198));
        calendarPos.put(25,new Point2D(600,198));
        calendarPos.put(26,new Point2D(620,198));
        calendarPos.put(27,new Point2D(640,198));

        calendarPos.put(28,new Point2D(520,214));
        calendarPos.put(29,new Point2D(540,214));
        calendarPos.put(30,new Point2D(560,214));
    }

    private void updateCalendar(){

        int day = GameState.getInstance().getCurrentDay();

        Point2D p = calendarPos.get(day);

        if(p != null){

            todayCircle.setVisible(true);

            todayCircle.setCenterX(p.getX());

            todayCircle.setCenterY(p.getY());

        }

        int remain = -1;

        for(int d : auctionDays){

            if(day <= d){

                remain = d - day;

                break;

            }

        }

        if(remain == 0 && day<28){

            auctionCountdownLabel.setText(" 7");

        }else if(day>=28){
        	auctionCountdownLabel.setText(" 0");
        }
        else{

            auctionCountdownLabel.setText(
                     " "+remain);

        }

    }
}