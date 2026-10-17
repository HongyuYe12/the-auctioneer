package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import controller.MusicManager;

// Program entry point
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
    	
    	MusicManager.playBGM();
        // Load the start view
        Parent root = FXMLLoader.load(getClass()
                                .getResource("/view/StartView.fxml"));//go to the start view

        // Get screen bounds and set window size to 80% of screen dimensions
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Set window size to 80% of screen, capped at preferred dimensions
        double width = Math.min(810, screenWidth * 0.8);
        double height = Math.min(608, screenHeight * 0.8); // Adjusted for MainView height

        Scene scene = new Scene(root, width, height);  // Set the interface size

        stage.setTitle("The Auctioneer");
        stage.setScene(scene);
        stage.setResizable(true); 
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}