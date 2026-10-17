package controller;

import javafx.fxml.FXML;

public class IntroduceController {

    @FXML
    private void handleBack() {
        MainController.getInstance().showDefaultView();
        MainController.getInstance().updateStatusBar();
    }
}