package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import model.Artifact;
import model.Player;
import model.AppraiseResult;
import model.GameState;
import util.Constants;
import util.ResourceUtil;


public class AppraiseController {

    // ========== UI components  ==========
    @FXML private ImageView artifactImage;
    @FXML private Label nameLabel;
    @FXML private Label infoLabel;
    @FXML private Label costLabel;
    @FXML private Button appraiseBtn;
    @FXML private Label resultLabel;
    @FXML private Button doneBtn;

    private Artifact currentArtifact;

    // ========== Importing antiques data ==========
    public void initData(Artifact artifact) {
        this.currentArtifact = artifact;
        updateUI();
    }

    // ========== refresh UI ==========
    private void updateUI() {
        if (currentArtifact == null) return;

        // load image
        ImageView imageView = artifactImage; // directly use FXML import ImageView
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
       
        Image img = ResourceUtil.loadImageOrPlaceholder(currentArtifact.getName() + ".png");
        imageView.setImage(img);

        // name,status,cost
        nameLabel.setText(currentArtifact.isAppraised() ? currentArtifact.getName() : "???");
        String status = currentArtifact.isAppraised() ?
                (currentArtifact.isAuthentic() ? "✅ Authentic" : "❌ Fake") + "  Value: " + currentArtifact.getCurrentValue() :
                "🔍 Unidentified  Category: " + currentArtifact.getCategory();
        infoLabel.setText(status);

        int cost = 0;
        switch (currentArtifact.getRarity()) {
            case LOW:  cost = Constants.LOW_APPRAISE_COST;  break;
            case MID:  cost = Constants.MID_APPRAISE_COST;  break;
            case MYTH: cost = Constants.MYTH_APPRAISE_COST; break;
        }
        costLabel.setText("Cost: " + cost + " energy");

        if (currentArtifact.isAppraised()) {
            appraiseBtn.setDisable(true);
            appraiseBtn.setText("Already done");
            doneBtn.setVisible(true);
            resultLabel.setVisible(false);
        } else {
            appraiseBtn.setDisable(false);
            appraiseBtn.setText("🔍 APPRAISE");
            doneBtn.setVisible(false);
            resultLabel.setVisible(false);
        }
    }
    //The function of the ‘Appraise’ button
    @FXML
    private void handleAppraise() {
        if (currentArtifact == null || currentArtifact.isAppraised()) return;
        Player player = GameState.getInstance().getPlayer();
        AppraiseResult result = appraise(player, currentArtifact);
        if (result == AppraiseResult.SUCCESS) {
            resultLabel.setText("✅ Appraisal successful! " + (currentArtifact.isAuthentic() ? "Authentic!" : "Fake!"));
            resultLabel.setStyle("-fx-text-fill: #2ecc71;");
            resultLabel.setVisible(true);
            updateUI();
            MainController.getInstance().updateStatusBar();
        } else if (result == AppraiseResult.NO_ENERGY) {
            resultLabel.setText("❌ Not enough energy!");
            resultLabel.setStyle("-fx-text-fill: #e74c3c;");
            resultLabel.setVisible(true);
        } else {
            resultLabel.setText("⚠️ Already appraised.");
            resultLabel.setVisible(true);
        }
    }
    //The function of the 'Back' button
    @FXML
    private void handleBack() {
        MainController.getInstance().loadInventoryScene();  
    }
    //The function of the 'Done' button
    @FXML
    private void handleDone() {
        MainController.getInstance().loadInventoryScene();
    }
   
    // ===================set the result of appraise================
    public AppraiseResult appraise(Player player, Artifact artifact) {
        if (artifact.isAppraised()) {
            return AppraiseResult.ALREADY_DONE;
        }
        int energyCost;
        switch (artifact.getRarity()) {
            case LOW:  energyCost = Constants.LOW_APPRAISE_COST;  break;
            case MID:  energyCost = Constants.MID_APPRAISE_COST;  break;
            case MYTH: energyCost = Constants.MYTH_APPRAISE_COST; break;
            default:   return AppraiseResult.ALREADY_DONE;
        }
        if (player.getEnergy() < energyCost) {
            return AppraiseResult.NO_ENERGY;
        }
        player.setEnergy(player.getEnergy() - energyCost);
        artifact.appraise();
        return AppraiseResult.SUCCESS;
    }
}
