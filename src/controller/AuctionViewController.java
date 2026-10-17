package controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import model.Artifact;
import model.AuctionPhase;
import model.AuctionSession;
import model.NPC;
import model.GameState;
import model.Player;
import util.NPCDatabase;
import util.ResourceUtil;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the live auction view.
 * Updates the UI in real-time as the auction progresses.
 */
public class AuctionViewController {

    @FXML private ImageView itemImage;
    @FXML private Label itemName;
    @FXML private Label basePriceLabel;
    @FXML private Label currentPriceLabel;
    @FXML private Label currentWinnerLabel;
    @FXML private Label itemProgressLabel;
    @FXML private Label statusLabel;
    @FXML private HBox npcContainer;
    @FXML private ProgressBar hammerProgress;
    @FXML private Label countdownLabel;
    @FXML private Button hammerBtn;

    private AuctionController auctionController = AuctionController.getInstance();

    private List<Artifact> items;
    private List<NPC> npcs;
    private AnimationTimer timer;
    private int currentItemIndex = 0;

    // Stores NPC UI nodes for easy highlight updates
    private List<VBox> npcNodes = new ArrayList<>();

    @FXML
    public void initialize() {
        items = AuctionSessionHolder.getItems();
        if (items == null || items.isEmpty()) {
            statusLabel.setText("No items to auction!");
            return;
        }

        // Generate NPCs based on player level
        Player player = GameState.getInstance().getPlayer();
        int level = player.getAuctionLevel();
        int npcCount = level == 1 ? 5 : (level == 2 ? 7 : 9);
        npcs = generateNPCs(npcCount);

        // Display NPCs
        displayNPCs();

        // Start the auction state machine
        auctionController.auction(player, items, npcs);

        // Start timer for UI updates
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateUI();
            }
        };
        timer.start();
    }

    /**
     * Generates a list of NPCs based on the required count.
     */
    private List<NPC> generateNPCs(int count) {
        List<NPC> allNPCs = NPCDatabase.getAllNPCs();
        List<NPC> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(allNPCs.get(i % allNPCs.size()));
        }
        return result;
    }

    /**
     * Displays NPC avatars in the container.
     */
    private void displayNPCs() {
        npcContainer.getChildren().clear();
        npcNodes.clear();

        for (NPC npc : npcs) {
            VBox node = createNPCNode(npc);
            npcNodes.add(node);
            npcContainer.getChildren().add(node);
        }
    }

    /**
     * Creates a single NPC node containing avatar, name, and bid bubble.
     */
    private VBox createNPCNode(NPC npc) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(70);
        box.setStyle("-fx-padding: 4; -fx-background-radius: 8;");

        // Avatar image
        ImageView avatar = new ImageView();
        avatar.setFitWidth(50);
        avatar.setFitHeight(50);
        avatar.setPreserveRatio(true);

        Image img = ResourceUtil.loadImageOrPlaceholder(npc.getName() + ".png");

        avatar.setImage(img);

        // NPC name (wrapped if too long)
        Label nameLabel = new Label(npc.getName());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setMaxWidth(60);
        nameLabel.setPrefHeight(60);

        // Bid bubble
        Label bubble = new Label();
        bubble.setStyle("-fx-background-color: rgba(255,215,0,0.8); -fx-padding: 4 10; -fx-background-radius: 8; -fx-text-fill: black; -fx-font-size: 10px; -fx-font-weight: bold;");
        bubble.setWrapText(false);
        bubble.setMinWidth(70);
        bubble.setMaxWidth(Double.MAX_VALUE);
        bubble.setVisible(false);

        box.getChildren().addAll(avatar, nameLabel, bubble);
        return box;
    }

    /**
     * Updates the UI with the current auction session state.
     */
    private void updateUI() {
        AuctionSession session = auctionController.getSession();
        if (session == null) return;

        AuctionPhase phase = session.getPhase();

        if (phase == AuctionPhase.FINISHED) {
            timer.stop();
            MainController.getInstance().loadAuctionResultScene();
            return;
        }

        int index = session.getCurrentIndex();
        if (index < items.size()) {
            Artifact item = items.get(index);
            currentItemIndex = index;
            itemProgressLabel.setText("Item " + (index + 1) + " / " + items.size());
            itemName.setText(item.getName());
            
            // Display the starting bid (authentic value) - aligns with the state machine's base price
            basePriceLabel.setText("Starting bid: " + item.getAuthenticValue());

            itemImage.setImage(ResourceUtil.loadImageOrPlaceholder(item.getName() + ".png"));
        }

        currentPriceLabel.setText(String.valueOf(session.getCurrentPrice()));
        currentWinnerLabel.setText("Highest bidder: " + session.getCurrentWinner());

        int countdown = session.getHammerCountdown();
        countdownLabel.setText(countdown + "s");
        hammerProgress.setProgress(countdown / 10.0);

        hammerBtn.setDisable(phase != AuctionPhase.WAITING_HAMMER);

        updateNPCs(session);

        statusLabel.setText("Phase: " + phase);
    }
    
    /**
     * Updates NPC highlights and bid bubbles based on the current highest bidder.
     */
    private void updateNPCs(AuctionSession session) {
        String winner = session.getCurrentWinner();

        for (int i = 0; i < npcs.size() && i < npcNodes.size(); i++) {
            VBox box = npcNodes.get(i);
            NPC npc = npcs.get(i);

            boolean isWinner = npc.getName().equals(winner) && !winner.equals("SYSTEM");

            if (isWinner) {
                box.setStyle("-fx-padding: 4; -fx-background-color: rgba(255,215,0,0.3); -fx-background-radius: 8;");

                for (Node child : box.getChildren()) {
                    if (child instanceof Label && child.getStyle().contains("background-color: rgba(255,215,0")) {
                        Label bubble = (Label) child;
                        bubble.setText("💰 " + session.getCurrentPrice());
                        bubble.setVisible(true);
                    }
                }
            } else {
                box.setStyle("-fx-padding: 4; -fx-background-radius: 8;");

                for (Node child : box.getChildren()) {
                    if (child instanceof Label && child.getStyle().contains("background-color: rgba(255,215,0")) {
                        child.setVisible(false);
                    }
                }
            }
        }
    }

    /**
     * Handles the hammer button click.
     */
    @FXML
    private void handleHammer() {

        auctionController.playerHammer(); // Execute backend sale
        MusicManager.playGavel();
        statusLabel.setText("SOLD!");

        hammerBtn.setDisable(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));

        pause.setOnFinished(e -> {

            AuctionSession session = auctionController.getSession();

            if (session != null && session.getPhase() == AuctionPhase.FINISHED) {
                timer.stop();
                MainController.getInstance().loadAuctionResultScene();
            } else {
                hammerBtn.setDisable(false);
            }
        });

        pause.play();
    }
    
}
