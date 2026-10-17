package controller;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.GameState;

import java.io.IOException;

public class GameOverController {

    @FXML private Label resultTitle;
    @FXML private Label subTitle;
    @FXML private Label infoLabel;
    @FXML private VBox mainContainer;
    @FXML private Circle glowCircle;

    public void setResultData(boolean victory, int finalGold, int finalDay) {
        // Set text
        if (victory) {
            resultTitle.setText("🎉 VICTORY!");
            MusicManager.stopBGM();
            MusicManager.playWin();
            resultTitle.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 50px;");
            subTitle.setText("🌟 Magnificent! You've restored the auction house to glory!");
            // Golden light: yellow radial gradient
            RadialGradient goldGrad = new RadialGradient(
                    0, 0, 0.5, 0.5, 0.5, true,
                    null,
                    new Stop(0, Color.rgb(255, 215, 0, 0.7)),
                    new Stop(1, Color.rgb(255, 215, 0, 0.0))
            );
            glowCircle.setFill(goldGrad);
            // Add golden glow effect to title
            DropShadow goldGlow = new DropShadow();
            goldGlow.setColor(Color.GOLD);
            goldGlow.setRadius(25);
            resultTitle.setEffect(goldGlow);
            // Button style adjustment (optional)
        } else {
            resultTitle.setText("💀 Failure...");
            MusicManager.stopBGM();
            MusicManager.playFail();
            resultTitle.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 50px;");
            subTitle.setText("😢 Don't give up! Every master was once a beginner.");
            // Dim halo
            RadialGradient grayGrad = new RadialGradient(
                    0, 0, 0.5, 0.5, 0.5, true,
                    null,
                    new Stop(0, Color.rgb(100, 100, 100, 0.6)),
                    new Stop(1, Color.rgb(100, 100, 100, 0.0))
            );
            glowCircle.setFill(grayGrad);
            // Gray shadow
            DropShadow grayShadow = new DropShadow();
            grayShadow.setColor(Color.GRAY);
            grayShadow.setRadius(20);
            resultTitle.setEffect(grayShadow);
        }
        infoLabel.setText("💰 Final Gold: " + String.format("%,d", finalGold) + "  |  📅 Days: " + (finalDay-1));

        // Start animation
        startAnimation();
    }

    private void startAnimation() {
        // Main container fades in and scales up from small
        mainContainer.setScaleX(0.3);
        mainContainer.setScaleY(0.3);
        mainContainer.setOpacity(0.0);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.6), mainContainer);
        scale.setFromX(0.3);
        scale.setFromY(0.3);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), mainContainer);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        ParallelTransition enterAnim = new ParallelTransition(scale, fade);
        enterAnim.play();

        // Halo pulse animation (only for victory with golden glow)
        if (resultTitle.getText().contains("VICTORY")) {
            Timeline pulse = new Timeline(
                    new javafx.animation.KeyFrame(Duration.ZERO,
                            e -> glowCircle.setRadius(150)),
                    new javafx.animation.KeyFrame(Duration.millis(400),
                            e -> glowCircle.setRadius(180)),
                    new javafx.animation.KeyFrame(Duration.millis(800),
                            e -> glowCircle.setRadius(150))
            );
            pulse.setCycleCount(Timeline.INDEFINITE);
            pulse.play();
        }
    }

    // Function of the restart button
    @FXML
    private void handleRestart() {
        GameState.setInstance(null);
        try {
            java.io.File saveFile = new java.io.File("save.dat");
            if (saveFile.exists()) saveFile.delete();
        } catch (Exception ignored) {}
        try {
            MusicManager.playBGM();
            Stage stage = (Stage) resultTitle.getScene().getWindow();
            Parent startView = FXMLLoader.load(getClass().getResource("/view/StartView.fxml"));
            stage.setScene(new Scene(startView, 810, 608));
            stage.setTitle("The Auctioneer");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function of the exit button
    @FXML
    private void handleExit() {
        System.exit(0);
    }
}