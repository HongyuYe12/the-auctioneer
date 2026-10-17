package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button; 
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Start Controller
 * Handles the initial game start screen.
 */
public class StartController {

    @FXML
    private Button startBtn;

    /**
     * Handles the start button click event.
     * Loads the main game view and switches the scene.
     */
    @FXML
    private void handleStart() {
        try {
            // Load the main view
            Parent mainView = FXMLLoader.load(
                getClass().getResource("/view/MainView.fxml")
            );

            // Get the current window (stage)
            Stage stage = (Stage) startBtn.getScene().getWindow();

            // Switch to the main scene
            stage.setScene(new Scene(mainView, 830, 480));
            stage.setTitle("The Auctioneer");
            stage.show();
            List<Button> navBtns = Arrays.asList(startBtn);
            for (Button btn : navBtns) {
                btn.getStyleClass().add("nav-button");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load main view!");
        }
    }
    
}