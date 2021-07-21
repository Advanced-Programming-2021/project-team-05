package view;

import control.DataManager;
import control.Phase;
import control.controller.DuelMenuController;
import control.controller.MainMenuController;
import control.message.DuelMenuMessage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import model.board.*;
import model.board.cell.MonsterCell;
import model.board.cell.SpellTrapCell;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import utils.CoinSide;
import utils.DuelBackgroundType;
import utils.ViewUtility;
import view.animation.FlipCoinAnimation;
import view.animation.ThrowCoinAnimation;

import java.io.IOException;
import java.util.ArrayList;

public class DuelMenuView implements CheatRunner {

    private final DuelMenuController controller;
    private Scene scene;

    private ImageView selectedCardImage;
    private boolean attackMode;

    {
        attackMode = false;
    }


    public DuelMenuView(DuelMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }


    public void setDuelScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/duel.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeDuelSceneButtons();
            scene.setOnKeyPressed(keyEvent -> handleConsoleKeyEvent(keyEvent, this));
        } catch (IOException e) {
            System.out.println("Failed to load duel scene");
        }
    }

    private void initializeDuelSceneButtons() {
        Button nextPhaseButton = (Button) scene.lookup("#next-phase-btn");
        nextPhaseButton.setOnMouseClicked(e -> nextPhase());

        Button PauseButton = (Button) scene.lookup("#pause-btn");
        PauseButton.setOnMouseClicked(e -> pauseGame());

        Button summonButton = (Button) scene.lookup("#summon-btn");
        summonButton.setOnMouseClicked(e -> summon());

        Button setButton = (Button) scene.lookup("#set-btn");
        setButton.setOnMouseClicked(e -> set());

        Button activateEffectButton = (Button) scene.lookup("#activate-effect-btn");
        activateEffectButton.setOnMouseClicked(e -> activateEffect());

        Button flipSummonButton = (Button) scene.lookup("#flip-summon-btn");
        flipSummonButton.setOnMouseClicked(e -> flipSummon());

        Button changeToAttackPositionButton = (Button) scene.lookup("#change-to-attack-position-btn");
        changeToAttackPositionButton.setOnMouseClicked(e -> changePosition("Attack"));

        Button changeToDefensePositionButton = (Button) scene.lookup("#change-to-defense-position-btn");
        changeToDefensePositionButton.setOnMouseClicked(e -> changePosition("Defense"));

        Button directAttackButton = (Button) scene.lookup("#direct-attack-btn");
        directAttackButton.setOnMouseClicked(e -> directAttack());

        Button attackButton = (Button) scene.lookup("#attack-btn");
        attackButton.setOnMouseClicked(e -> turnOnAttackMode());

        Button cancelAttackButton = (Button) scene.lookup("#cancel-attack-btn");
        cancelAttackButton.setOnMouseClicked(e -> turnOffAttackMode());

        resetButtons();
    }

    private void resetButtons() {
        resetButton((Button) scene.lookup("#summon-btn"));
        resetButton((Button) scene.lookup("#set-btn"));
        resetButton((Button) scene.lookup("#activate-effect-btn"));
        resetButton((Button) scene.lookup("#flip-summon-btn"));
        resetButton((Button) scene.lookup("#change-to-attack-position-btn"));
        resetButton((Button) scene.lookup("#change-to-defense-position-btn"));
        resetButton((Button) scene.lookup("#direct-attack-btn"));
        resetButton((Button) scene.lookup("#attack-btn"));
    }

    private void resetButton(Button button) {
        button.setLayoutX(680);
        button.setLayoutY(100);
        button.setDisable(true);
        button.setOpacity(0);
    }


    public void updateBoard(Board board) {
        updateLPs(board);
        updatePlayersInfo(board.getPlayerTable().getOwner(), board.getOpponentTable().getOwner());
        for (int j = 0; j < 2; j++) {
            Table targetTable = j == 0 ? board.getPlayerTable() : board.getOpponentTable();
            for (int i = 1; i <= 5; i++) {
                MonsterCell monsterCell = targetTable.getMonsterCell(i);
                CardAddress address = new CardAddress(CardAddressZone.MONSTER, i, j == 1);
                if (monsterCell.getCard() == null) {
                    removeCardFromBoard(address);
                    removeMonsterLabel(address);
                } else {
                    Monster monster = (Monster) monsterCell.getCard();
                    addCardToBoard(monster, address, monsterCell.getState());
                    updateMonsterLabel(monster, address, monsterCell.getState(), board.isMonsterSpelled(monster));
                }
            }

            for (int i = 1; i <= 5; i++) {
                SpellTrapCell spellTrapCell = targetTable.getSpellOrTrapCell(i);
                CardAddress address = new CardAddress(CardAddressZone.SPELL, i, j == 1);
                if (spellTrapCell.getCard() == null) removeCardFromBoard(address);
                else addCardToBoard(spellTrapCell.getCard(), address, spellTrapCell.getState());
            }

            SpellTrapCell fieldSpellCell = targetTable.getFieldSpellCell();
            CardAddress fieldSpellAddress = new CardAddress(CardAddressZone.FIELD, 0, j == 1);
            if (fieldSpellCell.getCard() == null) removeCardFromBoard(fieldSpellAddress);
            else addCardToBoard(fieldSpellCell.getCard(), fieldSpellAddress, fieldSpellCell.getState());

            ArrayList<Card> graveyard = targetTable.getGraveyard();
            CardAddress graveyardAddress = new CardAddress(CardAddressZone.GRAVEYARD, 0, j == 1);
            Label graveyardLabel = (Label) scene.lookup("#" + (j == 1 ? "opp-" : "") + "graveyard-label");
            if (graveyard.size() == 0) {
                removeCardFromBoard(graveyardAddress);
                graveyardLabel.setText("");
            } else {
                addCardToBoard(targetTable.getGraveyard().get(graveyard.size() - 1), graveyardAddress, CardState.VERTICAL_UP);
                graveyardLabel.setText(String.valueOf(graveyard.size()));
            }

            int deckSize = targetTable.getDeck().getMainDeckSize();
            HBox deckContainer = (HBox) scene.lookup("#" + ((j == 1) ? "opp-" : "") + "deck");
            deckContainer.getChildren().clear();
            Label deckLabel = (Label) scene.lookup("#" + ((j == 1) ? "opp-" : "") + "deck-label");
            if (deckSize == 0) {
                deckLabel.setText("");
            } else {
                ImageView deckImage = ViewUtility.getCardImageView("Unknown");
                deckImage.setFitWidth(95);
                deckImage.setFitHeight(95);
                deckImage.setPreserveRatio(true);
                deckContainer.getChildren().add(deckImage);
                deckLabel.setText(String.valueOf(deckSize));
            }

            clearHand(j == 1);
            ArrayList<Card> hand = targetTable.getHand();
            for (Card card : hand) {
                addCardToHand(card, j == 1);
            }
        }
    }

    private void addCardToBoard(Card card, CardAddress address, CardState cardState) {
        if (cardState == null) cardState = CardState.VERTICAL_UP;
        String cardName = cardState.isDown() ? "Unknown" : card.getName();
        ImageView cardImage = ViewUtility.getCardImageView(cardName);
        cardImage.setFitWidth(95);
        cardImage.setFitHeight(95);
        cardImage.setPreserveRatio(true);

        int rotateValue = (address.isForOpponent() ? 180 : 0) + (cardState.isHorizontal() ? 90 : 0);
        cardImage.setRotate(rotateValue);

        String selector = getSelector(address);

        HBox cardContainer = (HBox) scene.lookup(selector);
        cardContainer.getChildren().clear();
        cardContainer.getChildren().add(cardImage);
        if (attackMode && address.isForOpponent() && address.getZone() == CardAddressZone.MONSTER) {
            cardImage.setOnMouseClicked(e -> {
                if (!attackMode) showCardDetails(card);
                selectCard(card, address, cardImage);
            });
            if (!cardContainer.getStyleClass().contains("selectable-card-image"))
                cardContainer.getStyleClass().add("selectable-card-image");
        } else if (address.isForOpponent() && cardState.isDown()) {
            cardContainer.getStyleClass().remove("selectable-card-image");
        } else if (address.getZone() == CardAddressZone.GRAVEYARD) {
            cardImage.setOnMouseClicked(e -> showGraveyard(address.isForOpponent()));
            if (!cardContainer.getStyleClass().contains("selectable-card-image"))
                cardContainer.getStyleClass().add("selectable-card-image");
        } else {
            cardImage.setOnMouseClicked(e -> {
                if (!attackMode) showCardDetails(card);
                selectCard(card, address, cardImage);
            });
            if (!cardContainer.getStyleClass().contains("selectable-card-image"))
                cardContainer.getStyleClass().add("selectable-card-image");
        }
    }

    private void removeCardFromBoard(CardAddress address) {
        String selector = getSelector(address);
        HBox cardContainer = (HBox) scene.lookup(selector);
        cardContainer.getChildren().clear();
        cardContainer.getStyleClass().remove("selectable-card-image");
    }

    private String getSelector(CardAddress address) {
        switch (address.getZone()) {
            case MONSTER:
                return "#" + (address.isForOpponent() ? "opp-" : "") + "monster-" + address.getPosition();
            case SPELL:
                return "#" + (address.isForOpponent() ? "opp-" : "") + "spell-" + address.getPosition();
            case FIELD:
                return "#" + (address.isForOpponent() ? "opp-" : "") + "field-spell";
            case GRAVEYARD:
                return "#" + (address.isForOpponent() ? "opp-" : "") + "graveyard";
            default:
                return "#";
        }
    }

    private void updateMonsterLabel(Monster monster, CardAddress address, CardState cardState, boolean isEffected) {
        if (address.getZone() == CardAddressZone.MONSTER) {
            String selector = "#" + (address.isForOpponent() ? "opp-" : "") + "monster-" + address.getPosition() + "-label";
            HBox monsterLabels = (HBox) scene.lookup(selector);

            Label attackLabel = (Label) monsterLabels.getChildren().get(0);
            Label slashLabel = (Label) monsterLabels.getChildren().get(1);
            Label defenseLabel = (Label) monsterLabels.getChildren().get(2);
            if (address.isForOpponent() && cardState.isDown()) {
                attackLabel.setText("");
                slashLabel.setText("");
                defenseLabel.setText("");
            } else {
                attackLabel.setText(String.valueOf(monster.getAttack()));
                attackLabel.getStyleClass().clear();
                attackLabel.getStyleClass().add("number-label");
                slashLabel.setText("/");
                defenseLabel.setText(String.valueOf(monster.getDefence()));
                defenseLabel.getStyleClass().clear();
                defenseLabel.getStyleClass().add("number-label");

                if (cardState.isHorizontal()) {
                    attackLabel.getStyleClass().add("monster-label-inactive" + (isEffected ? "-effected" : ""));
                    defenseLabel.getStyleClass().add("monster-label-active" + (isEffected ? "-effected" : ""));
                } else {
                    attackLabel.getStyleClass().add("monster-label-active" + (isEffected ? "-effected" : ""));
                    defenseLabel.getStyleClass().add("monster-label-inactive" + (isEffected ? "-effected" : ""));
                }
            }
        }
    }

    private void removeMonsterLabel(CardAddress address) {
        if (address.getZone() == CardAddressZone.MONSTER) {
            String selector = "#" + (address.isForOpponent() ? "opp-" : "") + "monster-" + address.getPosition() + "-label";
            HBox monsterLabels = (HBox) scene.lookup(selector);

            ((Label) monsterLabels.getChildren().get(0)).setText("");
            ((Label) monsterLabels.getChildren().get(1)).setText("");
            ((Label) monsterLabels.getChildren().get(2)).setText("");
        }
    }

    private void addCardToHand(Card card, boolean isOpp) {
        String cardName = isOpp ? "Unknown" : card.getName();
        ImageView cardImage = ViewUtility.getCardImageView(cardName);
        cardImage.setFitWidth(100);
        cardImage.setFitHeight(95);
        cardImage.setRotate(isOpp ? 180 : 0);
        cardImage.setPreserveRatio(true);
        FlowPane hand = (FlowPane) scene.lookup("#" + (isOpp ? "opp-" : "") + "hand");
        if (!isOpp) cardImage.setOnMouseClicked(e -> {
            if (!attackMode) showCardDetails(card);
            selectCard(card, new CardAddress(CardAddressZone.HAND, 0, false), cardImage);
        });

        Image oldImage = cardImage.getImage();
        PixelReader reader = oldImage.getPixelReader();
        int newImageWidth = (int) Math.round(oldImage.getWidth());
        int newImageHeight = (int) (95 * oldImage.getWidth() / 100);
        WritableImage newImage = new WritableImage(reader, 0, 0, newImageWidth, newImageHeight);
        cardImage.setImage(newImage);
        hand.getChildren().add(cardImage);
    }

    private void clearHand(boolean isOpp) {
        FlowPane hand = (FlowPane) scene.lookup("#" + (isOpp ? "opp-" : "") + "hand");
        hand.getChildren().clear();
    }


    private void selectCard(Card card, CardAddress address, ImageView cardImage) {
        if (attackMode) {
            if (address.isForOpponent() && address.getZone() == CardAddressZone.MONSTER)
                controller.attack(address.getPosition());
            else ViewUtility.showInformationAlert("Select", "", "You should attack now");
            return;
        }
        if (selectedCardImage == cardImage) {
            controller.deselect();
            return;
        }
        resetButtons();
        if (selectedCardImage != null) selectedCardImage.getStyleClass().remove("selected-card");
        cardImage.getStyleClass().add("selected-card");
        selectedCardImage = cardImage;

        if (address.getZone() == CardAddressZone.HAND) controller.selectCardFromHand(card);
        else controller.selectCard(address);

        showRequiredButtons(card, address, cardImage);
    }

    private void showRequiredButtons(Card card, CardAddress address, ImageView cardImage) {
        if (address.isForOpponent()) return;
        Phase phase = controller.getPhase();
        if (address.getZone() == CardAddressZone.HAND) {
            double layoutX = cardImage.getLayoutX();
            if (!(phase == Phase.MAIN_1 || phase == Phase.MAIN_2)) return;
            if (card instanceof Monster) {
                Button summonButton = (Button) scene.lookup("#summon-btn");
                summonButton.setLayoutX(layoutX + 21);
                summonButton.setLayoutY(720);
                summonButton.setDisable(false);
                summonButton.setOpacity(1);

                Button setButton = (Button) scene.lookup("#set-btn");
                setButton.setLayoutX(layoutX + 73);
                setButton.setLayoutY(720);
                setButton.setDisable(false);
                setButton.setOpacity(1);
            } else if (card instanceof Spell) {
                Button activateEffect = (Button) scene.lookup("#activate-effect-btn");
                activateEffect.setLayoutX(layoutX + 21);
                activateEffect.setLayoutY(720);
                activateEffect.setDisable(false);
                activateEffect.setOpacity(1);

                Button setButton = (Button) scene.lookup("#set-btn");
                setButton.setLayoutX(layoutX + 73);
                setButton.setLayoutY(720);
                setButton.setDisable(false);
                setButton.setOpacity(1);
            } else {
                Button setButton = (Button) scene.lookup("#set-btn");
                setButton.setLayoutX(layoutX + 40);
                setButton.setLayoutY(720);
                setButton.setDisable(false);
                setButton.setOpacity(1);
            }
        } else if (address.getZone() == CardAddressZone.MONSTER) {
            double layoutX = cardImage.getParent().getLayoutX();
            if (phase == Phase.MAIN_1 || phase == Phase.MAIN_2) {
                Button flipSummonButton = (Button) scene.lookup("#flip-summon-btn");
                flipSummonButton.setLayoutX(layoutX + 13);
                flipSummonButton.setLayoutY(417);
                flipSummonButton.setDisable(false);
                flipSummonButton.setOpacity(1);

                Button changeToAttackButton = (Button) scene.lookup("#change-to-attack-position-btn");
                changeToAttackButton.setLayoutX(layoutX - 15);
                changeToAttackButton.setLayoutY(480);
                changeToAttackButton.setDisable(false);
                changeToAttackButton.setOpacity(1);

                Button changeToDefenseButton = (Button) scene.lookup("#change-to-defense-position-btn");
                changeToDefenseButton.setLayoutX(layoutX + 43);
                changeToDefenseButton.setLayoutY(480);
                changeToDefenseButton.setDisable(false);
                changeToDefenseButton.setOpacity(1);
            } else if (phase == Phase.BATTLE) {
                Button changeToAttackButton = (Button) scene.lookup("#direct-attack-btn");
                changeToAttackButton.setLayoutX(layoutX - 15);
                changeToAttackButton.setLayoutY(440);
                changeToAttackButton.setDisable(false);
                changeToAttackButton.setOpacity(1);

                Button changeToDefenseButton = (Button) scene.lookup("#attack-btn");
                changeToDefenseButton.setLayoutX(layoutX + 43);
                changeToDefenseButton.setLayoutY(440);
                changeToDefenseButton.setDisable(false);
                changeToDefenseButton.setOpacity(1);
            }
        } else if (address.getZone() == CardAddressZone.SPELL) {
            if (card instanceof Spell) {
                double layoutX = cardImage.getParent().getLayoutX();
                Button activateEffectButton = (Button) scene.lookup("#activate-effect-btn");
                activateEffectButton.setLayoutX(layoutX + 13);
                activateEffectButton.setLayoutY(580);
                activateEffectButton.setDisable(false);
                activateEffectButton.setOpacity(1);
            }
        } else if (address.getZone() == CardAddressZone.FIELD) {
            double layoutX = cardImage.getParent().getLayoutX();
            Button activateEffectButton = (Button) scene.lookup("#activate-effect-btn");
            activateEffectButton.setLayoutX(layoutX + 13);
            activateEffectButton.setLayoutY(435);
            activateEffectButton.setDisable(false);
            activateEffectButton.setOpacity(1);
        }
    }

    public void deselectCard() {
        resetButtons();
        if (selectedCardImage != null) {
            selectedCardImage.getStyleClass().remove("selected-card");
            clearCardDetails();
            selectedCardImage = null;
        }
    }


    public void showPhase(Phase phase) {
        VBox[] phases = new VBox[6];
        phases[0] = (VBox) scene.lookup("#draw-phase-label");
        phases[1] = (VBox) scene.lookup("#standby-phase-label");
        phases[2] = (VBox) scene.lookup("#main-1-phase-label");
        phases[3] = (VBox) scene.lookup("#battle-phase-label");
        phases[4] = (VBox) scene.lookup("#main-2-phase-label");
        phases[5] = (VBox) scene.lookup("#end-phase-label");

        for (VBox phaseBox : phases) {
            phaseBox.getStyleClass().remove("phase-label-active");
        }
        phases[phase.getNumber()].getStyleClass().add("phase-label-active");
    }


    public void updateLPs(int playerLP, int opponentLP) {
        ProgressBar lpBar = (ProgressBar) scene.lookup("#lp-bar");
        double oldPlayerLP = lpBar.getProgress() * 8000;

        Label lpLabel = (Label) scene.lookup("#lp-label");
        lpLabel.setText(String.valueOf(playerLP));

        ProgressBar oppLPBar = (ProgressBar) scene.lookup("#opp-lp-bar");
        double oldOpponentLP = oppLPBar.getProgress() * 8000;

        Label oppLBLabel = (Label) scene.lookup("#opp-lp-label");
        oppLBLabel.setText(String.valueOf(opponentLP));

        double playerTime = Math.abs(oldPlayerLP - playerLP) / 5 + 100;
        double oppTime = Math.abs(oldOpponentLP - opponentLP) / 5 + 100;
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(lpBar.progressProperty(), oldPlayerLP / 8000)),
                new KeyFrame(Duration.ZERO, new KeyValue(oppLPBar.progressProperty(), oldOpponentLP / 8000)),
                new KeyFrame(Duration.millis(playerTime), new KeyValue(lpBar.progressProperty(), (double) playerLP / 8000)),
                new KeyFrame(Duration.millis(oppTime), new KeyValue(oppLPBar.progressProperty(), (double) opponentLP / 8000))
        );
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void updateLPs(Board board) {
        updateLPs(board.getPlayerTable().getLifePoint(), board.getOpponentTable().getLifePoint());
    }


    public void showCardDetails(Card card) {
        String address = "/images/cards/" + card.getName().replace(" ", "_") + ".jpg";
        ImageView cardImage = new ImageView(new Image(ViewUtility.class.getResource(address).toExternalForm()));
        cardImage.setFitWidth(219);
        cardImage.setFitHeight(329);

        String description = DataManager.getInstance().getCardTemplateByName(card.getName()).detailedToString();
        TextArea descriptionArea = new TextArea(description);
        descriptionArea.getStyleClass().add("description-box");
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setMinHeight(200);
        descriptionArea.setMaxHeight(200);

        VBox container = (VBox) scene.lookup("#card-details-box");
        container.getChildren().clear();
        container.getChildren().addAll(cardImage, descriptionArea);
    }

    public void clearCardDetails() {
        VBox container = (VBox) scene.lookup("#card-details-box");
        container.getChildren().clear();
    }

    public void updatePlayersInfo(User player1, User player2) {
        Label player1Score = (Label) scene.lookup("#score-label");
        player1Score.setText("Score: " + controller.getScore(player1));

        Label player1Nickname = (Label) scene.lookup("#nickname-label");
        player1Nickname.setText(player1.getNickname());

        ImageView player1Pic = (ImageView) scene.lookup("#profile-pic");
        player1Pic.setImage(new Image(ViewUtility.class.getResource("/images/profile-pics/" + player1.getProfilePictureName()).toExternalForm()));

        Label player2Score = (Label) scene.lookup("#opp-score-label");
        player2Score.setText("Score: " + controller.getScore(player2));

        Label player2Nickname = (Label) scene.lookup("#opp-nickname-label");
        player2Nickname.setText(player2.getNickname());

        ImageView player2Pic = (ImageView) scene.lookup("#opp-profile-pic");
        player2Pic.setImage(new Image(ViewUtility.class.getResource("/images/profile-pics/" + player2.getProfilePictureName()).toExternalForm()));
    }


    public void pauseGame() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Pause");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/pause.fxml"));
            Scene duelSettingScene = new Scene(root);
            stage.setScene(duelSettingScene);
            stage.show();

            Button backButton = (Button) duelSettingScene.lookup("#back-btn");
            backButton.setOnAction(e -> stage.close());
            backButton.setOnMouseClicked(e -> stage.close());

            Button surrenderButton = (Button) duelSettingScene.lookup("#surrender-btn");
            surrenderButton.setOnAction(e -> {
                stage.close();
                controller.surrender();
            });
            surrenderButton.setOnMouseClicked(e -> {
                stage.close();
                controller.surrender();
            });

            Button startDuelButton = (Button) duelSettingScene.lookup("#exit-btn");
            startDuelButton.setOnMouseClicked(e -> {
                controller.exit();
                stage.close();
                MainMenuController mainMenuController = new MainMenuController(controller.getPlayerOne());
                MainMenuView mainMenuView = new MainMenuView(mainMenuController);
                mainMenuView.setMainMenuScene();
            });
            startDuelButton.setOnAction(e -> {
                controller.exit();
                stage.close();
                MainMenuController mainMenuController = new MainMenuController(controller.getPlayerOne());
                MainMenuView mainMenuView = new MainMenuView(mainMenuController);
                mainMenuView.setMainMenuScene();
            });
        } catch (IOException e) {
            System.out.println("Failed to load pause scene");
        }
    }


    private void nextPhase() {
        if (attackMode)
            ViewUtility.showInformationAlert("Next Phase", "", "You can't change phase while in attack mode");
        else controller.goToNextPhase(true);
    }


    public ArrayList<Integer> getCardsPosition(ArrayList<Card> cards, int positionsCount, String message) {
        ArrayList<Integer> positions = new ArrayList<>();

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Select Cards");
        stage.initModality(Modality.APPLICATION_MODAL);

        Label title = new Label("Select Cards");
        title.getStyleClass().add("title-label");

        HBox titleContainer = new HBox(title);
        titleContainer.setAlignment(Pos.CENTER);

        Label messageLabel = new Label(message);
        messageLabel.setId("message-label");
        messageLabel.getStyleClass().add("default-label");

        Button finishButton = new Button("Finish");
        finishButton.setId("finish-btn");
        finishButton.getStyleClass().addAll("default-button", "button-small");
        finishButton.setDisable(true);
        finishButton.setOnMouseClicked(e -> stage.close());
        finishButton.setOnAction(e -> stage.close());

        Button cancelButton = new Button("Cancel");
        cancelButton.setId("cancel-btn");
        cancelButton.getStyleClass().addAll("default-button", "button-small");
        cancelButton.setOnMouseClicked(e -> {
            positions.clear();
            stage.close();
        });
        cancelButton.setOnAction(e -> {
            positions.clear();
            stage.close();
        });

        FlowPane cardsContainer = new FlowPane();
        cardsContainer.setPrefWidth(780);
        cardsContainer.setId("cards-container");
        cardsContainer.setAlignment(Pos.CENTER);
        for (int i = 0, cardsSize = cards.size(); i < cardsSize; i++) {
            int index = i;
            Card card = cards.get(i);
            ImageView cardImage = ViewUtility.getCardImageView(card.getName());
            cardImage.getStyleClass().add("card-image");
            cardImage.setFitWidth(150);
            cardImage.setFitHeight(250);
            cardImage.setOnMouseClicked(e -> {
                if (cardImage.getStyleClass().contains("selected-image")) {
                    positions.remove(Integer.valueOf(index));
                    cardImage.getStyleClass().remove("selected-image");
                } else {
                    positions.add(index);
                    cardImage.getStyleClass().add("selected-image");
                }
                finishButton.setDisable(positions.size() != positionsCount);
            });

            Button showButton = new Button("Show");
            showButton.getStyleClass().addAll("default-button", "show-button");
            showButton.setOnMouseClicked(e -> ViewUtility.showCard(card.getName()));
            showButton.setOnAction(e -> ViewUtility.showCard(card.getName()));

            VBox container = new VBox(2, cardImage, showButton);
            container.setPrefWidth(150);
            container.setPrefHeight(282);
            cardsContainer.getChildren().add(container);
        }

        ScrollPane scrollPane = new ScrollPane(cardsContainer);
        scrollPane.setId("scroll-pane");

        GridPane root = new GridPane();
        root.setId("root");
        root.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource("/css/select-cards.css").toExternalForm());
        root.setPrefWidth(800);
        root.setPrefHeight(700);

        ColumnConstraints columnConstraints1 = new ColumnConstraints(150);
        columnConstraints1.setHgrow(Priority.NEVER);
        columnConstraints1.setHalignment(HPos.CENTER);
        ColumnConstraints columnConstraints2 = new ColumnConstraints(250);
        columnConstraints2.setHgrow(Priority.NEVER);
        columnConstraints2.setHalignment(HPos.CENTER);
        ColumnConstraints columnConstraints3 = new ColumnConstraints(250);
        columnConstraints3.setHgrow(Priority.NEVER);
        columnConstraints3.setHalignment(HPos.CENTER);
        ColumnConstraints columnConstraints4 = new ColumnConstraints(150);
        columnConstraints4.setHgrow(Priority.NEVER);
        columnConstraints4.setHalignment(HPos.CENTER);
        root.getColumnConstraints().clear();
        root.getColumnConstraints().addAll(columnConstraints1, columnConstraints2, columnConstraints3, columnConstraints4);

        RowConstraints rowConstraints1 = new RowConstraints(40);
        rowConstraints1.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints2 = new RowConstraints(100);
        rowConstraints2.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints3 = new RowConstraints(100);
        rowConstraints3.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints4 = new RowConstraints(120);
        rowConstraints3.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints5 = new RowConstraints(100);
        rowConstraints3.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints6 = new RowConstraints(100);
        rowConstraints3.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints7 = new RowConstraints(120);
        rowConstraints3.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints8 = new RowConstraints(20);
        rowConstraints3.setVgrow(Priority.NEVER);
        root.getRowConstraints().clear();
        root.getRowConstraints().addAll(rowConstraints1, rowConstraints2, rowConstraints3, rowConstraints4, rowConstraints5, rowConstraints6, rowConstraints7, rowConstraints8);

        root.add(titleContainer, 1, 1, 2, 1);
        root.add(messageLabel, 0, 2, 4, 1);
        root.add(scrollPane, 0, 3, 4, 3);
        root.add(finishButton, 1, 6, 1, 1);
        root.add(cancelButton, 2, 6, 1, 1);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();

        return positions;
    }

    public ArrayList<Integer> getCardsPosition(ArrayList<Card> cards, ArrayList<Boolean> showCards, int positionsCount, String message) {
        ArrayList<Integer> positions = new ArrayList<>();

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Select Cards");
        stage.initModality(Modality.APPLICATION_MODAL);

        Label title = new Label("Select Cards");
        title.getStyleClass().add("title-label");

        HBox titleContainer = new HBox(title);
        titleContainer.setAlignment(Pos.CENTER);

        Label messageLabel = new Label(message);
        messageLabel.setId("message-label");
        messageLabel.getStyleClass().add("default-label");

        Button finishButton = new Button("Finish");
        finishButton.setId("finish-btn");
        finishButton.getStyleClass().addAll("default-button", "button-small");
        finishButton.setDisable(true);
        finishButton.setOnMouseClicked(e -> stage.close());
        finishButton.setOnAction(e -> stage.close());

        Button cancelButton = new Button("Cancel");
        cancelButton.setId("cancel-btn");
        cancelButton.getStyleClass().addAll("default-button", "button-small");
        cancelButton.setOnMouseClicked(e -> {
            positions.clear();
            stage.close();
        });
        cancelButton.setOnAction(e -> {
            positions.clear();
            stage.close();
        });

        FlowPane cardsContainer = new FlowPane();
        cardsContainer.setPrefWidth(780);
        cardsContainer.setId("cards-container");
        cardsContainer.setAlignment(Pos.CENTER);
        for (int i = 0, cardsSize = cards.size(); i < cardsSize; i++) {
            int index = i;
            Card card = cards.get(i);
            boolean showCard = showCards.get(i);
            ImageView cardImage = ViewUtility.getCardImageView(showCard ? card.getName() : "Unknown");
            cardImage.getStyleClass().add("card-image");
            cardImage.setFitWidth(150);
            cardImage.setFitHeight(250);
            cardImage.setOnMouseClicked(e -> {
                if (cardImage.getStyleClass().contains("selected-image")) {
                    positions.remove(Integer.valueOf(index));
                    cardImage.getStyleClass().remove("selected-image");
                } else {
                    positions.add(index);
                    cardImage.getStyleClass().add("selected-image");
                }
                finishButton.setDisable(positions.size() != positionsCount);
            });

            Button showButton = new Button("Show");
            showButton.getStyleClass().addAll("default-button", "show-button");
            showButton.setDisable(!showCard);
            if (showCard) {
                showButton.setOnMouseClicked(e -> ViewUtility.showCard(card.getName()));
                showButton.setOnAction(e -> ViewUtility.showCard(card.getName()));
            }

            VBox container = new VBox(2, cardImage, showButton);
            container.setPrefWidth(150);
            container.setPrefHeight(282);
            cardsContainer.getChildren().add(container);
        }

        ScrollPane scrollPane = new ScrollPane(cardsContainer);
        scrollPane.setId("scroll-pane");

        GridPane root = new GridPane();
        root.setId("root");
        root.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        root.getStylesheets().add(getClass().getResource("/css/select-cards.css").toExternalForm());
        root.setPrefWidth(800);
        root.setPrefHeight(700);

        ColumnConstraints columnConstraints1 = new ColumnConstraints(150);
        columnConstraints1.setHgrow(Priority.NEVER);
        columnConstraints1.setHalignment(HPos.CENTER);
        ColumnConstraints columnConstraints2 = new ColumnConstraints(250);
        columnConstraints2.setHgrow(Priority.NEVER);
        columnConstraints2.setHalignment(HPos.CENTER);
        ColumnConstraints columnConstraints3 = new ColumnConstraints(250);
        columnConstraints3.setHgrow(Priority.NEVER);
        columnConstraints3.setHalignment(HPos.CENTER);
        ColumnConstraints columnConstraints4 = new ColumnConstraints(150);
        columnConstraints4.setHgrow(Priority.NEVER);
        columnConstraints4.setHalignment(HPos.CENTER);
        root.getColumnConstraints().clear();
        root.getColumnConstraints().addAll(columnConstraints1, columnConstraints2, columnConstraints3, columnConstraints4);

        RowConstraints rowConstraints1 = new RowConstraints(40);
        rowConstraints1.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints2 = new RowConstraints(100);
        rowConstraints2.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints3 = new RowConstraints(100);
        rowConstraints3.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints4 = new RowConstraints(120);
        rowConstraints3.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints5 = new RowConstraints(100);
        rowConstraints3.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints6 = new RowConstraints(100);
        rowConstraints3.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints7 = new RowConstraints(120);
        rowConstraints3.setVgrow(Priority.NEVER);
        RowConstraints rowConstraints8 = new RowConstraints(20);
        rowConstraints3.setVgrow(Priority.NEVER);
        root.getRowConstraints().clear();
        root.getRowConstraints().addAll(rowConstraints1, rowConstraints2, rowConstraints3, rowConstraints4, rowConstraints5, rowConstraints6, rowConstraints7, rowConstraints8);

        root.add(titleContainer, 1, 1, 2, 1);
        root.add(messageLabel, 0, 2, 4, 1);
        root.add(scrollPane, 0, 3, 4, 3);
        root.add(finishButton, 1, 6, 1, 1);
        root.add(cancelButton, 2, 6, 1, 1);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();

        return positions;
    }


    private void summon() {
        controller.checkSummon(false);
    }

    public void showSummonMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                ViewUtility.showInformationAlert("Summon", "", "No card is selected yet");
                break;
            case CANT_SUMMON:
                ViewUtility.showInformationAlert("Summon", "", "You can’t summon this card");
                break;
            case ACTION_NOT_ALLOWED:
                ViewUtility.showInformationAlert("Summon", "", "You can’t do this action in this phase");
                break;
            case MONSTER_ZONE_IS_FULL:
                ViewUtility.showInformationAlert("Summon", "", "Monster card zone is full");
                break;
            case ALREADY_SUMMONED_SET:
                ViewUtility.showInformationAlert("Summon", "", "You already summoned/set on this turn");
                break;
            case NOT_ENOUGH_TRIBUTE:
                ViewUtility.showInformationAlert("Summon", "", "There are not enough cards for tribute");
                break;
            case CANT_PLAY_THIS_KIND_OF_MOVES:
                ViewUtility.showInformationAlert("Summon", "", "It’s not your turn to play this kind of moves");
                break;
            case SPECIAL_SUMMON_RIGHT_NOW:
                ViewUtility.showInformationAlert("Summon", "", "You should special summon right now");
                break;
            case SUMMON_SUCCESSFUL:
                break;
            default:
                ViewUtility.showInformationAlert("Summon", "", "Unexpected error");
        }
    }

    public void showTributeSummonMessage(DuelMenuMessage message) {
        switch (message) {
            case INVALID_POSITION:
                ViewUtility.showInformationAlert("Tribute Summon", "", "Invalid position");
                break;
            case NO_MONSTER_ON_ADDRESS:
                ViewUtility.showInformationAlert("Tribute Summon", "", "There is no monsters in one of addresses");
                break;
            case SUMMON_SUCCESSFUL:
                break;
            default:
                ViewUtility.showInformationAlert("Tribute Summon", "", "Unexpected error");
        }
    }


    private void set() {
        controller.set();
    }

    public void showSetMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                ViewUtility.showInformationAlert("Set", "", "No card is selected yet");
                break;
            case CANT_SET:
                ViewUtility.showInformationAlert("Set", "", "You can’t set this card");
                break;
            case ACTION_NOT_ALLOWED:
                ViewUtility.showInformationAlert("Set", "", "You can’t do this action in this phase");
                break;
            case MONSTER_ZONE_IS_FULL:
                ViewUtility.showInformationAlert("Set", "", "Monster card zone is full");
                break;
            case SPELL_ZONE_FULL:
                ViewUtility.showInformationAlert("Set", "", "Spell card zone is full");
                break;
            case ALREADY_SUMMONED_SET:
                ViewUtility.showInformationAlert("Set", "", "You already summoned/set on this turn");
                break;
            case CANT_PLAY_THIS_KIND_OF_MOVES:
                ViewUtility.showInformationAlert("Set", "", "It’s not your turn to play this kind of moves");
                break;
            case SET_SUCCESSFUL:
                break;
            default:
                ViewUtility.showInformationAlert("Set", "", "Unexpected error");
        }
    }


    private void changePosition(String position) {
        controller.changePosition(position);
    }

    public void showChangePositionMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                ViewUtility.showInformationAlert("Change Position", "", "No card is selected yet");
                break;
            case CANT_CHANGE_POSITION:
                ViewUtility.showInformationAlert("Change Position", "", "You can’t change this card position");
                break;
            case ACTION_NOT_ALLOWED:
                ViewUtility.showInformationAlert("Change Position", "", "You can’t do this action in this phase");
                break;
            case ALREADY_IN_WANTED_POSITION:
                ViewUtility.showInformationAlert("Change Position", "", "This card is already in the wanted position");
                break;
            case ALREADY_CHANGED_POSITION:
                ViewUtility.showInformationAlert("Change Position", "", "You already changed this card position in this turn");
                break;
            case POSITION_CHANGED:
                break;
            default:
                ViewUtility.showInformationAlert("Change Position", "", "Unexpected error");
        }
    }


    private void flipSummon() {
        controller.checkFlipSummon();
    }

    public void showFlipSummonMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                ViewUtility.showInformationAlert("Flip Summon", "", "No card is selected yet");
                break;
            case CANT_CHANGE_POSITION:
                ViewUtility.showInformationAlert("Flip Summon", "", "You can’t change this card position");
                break;
            case ACTION_NOT_ALLOWED:
                ViewUtility.showInformationAlert("Flip Summon", "", "You can’t do this action in this phase");
                break;
            case CANT_FLIP_SUMMON:
                ViewUtility.showInformationAlert("Flip Summon", "", "You can’t flip summon this card");
                break;
            case FLIP_SUMMON_SUCCESSFUL:
                break;
            default:
                ViewUtility.showInformationAlert("Flip Summon", "", "Unexpected error");
        }
    }


    private void directAttack() {
        controller.directAttack();
    }

    public void showDirectAttackMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                ViewUtility.showInformationAlert("Direct Attack", "", "No card is selected yet");
                break;
            case CANT_ATTACK:
                ViewUtility.showInformationAlert("Direct Attack", "", "You can’t attack with this card");
                break;
            case ACTION_NOT_ALLOWED:
                ViewUtility.showInformationAlert("Direct Attack", "", "You can’t do this action in this phase");
                break;
            case ALREADY_ATTACKED:
                ViewUtility.showInformationAlert("Direct Attack", "", "This card already attacked");
                break;
            case CANT_ATTACK_DIRECTLY:
                ViewUtility.showInformationAlert("Direct Attack", "", "You can’t attack the opponent directly");
                break;
            case ATTACK_PREVENTED:
                ViewUtility.showInformationAlert("Direct Attack", "", "Attack prevented");
                return;
            case DIRECT_ATTACK_SUCCESSFUL:
                updateLPs(controller.getBoard());
                break;
            default:
                ViewUtility.showInformationAlert("Direct Attack", "", "Unexpected error");
        }
    }


    private void turnOnAttackMode() {
        if (attackMode) return;
        if (controller.canSelectedCardAttack()) {
            attackMode = true;
            updateBoard(controller.getBoard());

            Table opponentTable = controller.getBoard().getOpponentTable();
            for (int i = 1; i <= 5; i++) {
                Monster monster = opponentTable.getMonster(i);
                if (monster != null) {
                    HBox cardContainer = (HBox) scene.lookup("#opp-monster-" + i);
                    cardContainer.getChildren().get(0).getStyleClass().add("attackable-card");
                }
            }
            Button cancelAttackButton = (Button) scene.lookup("#cancel-attack-btn");
            cancelAttackButton.setOpacity(1);
            cancelAttackButton.setDisable(false);

        } else ViewUtility.showInformationAlert("Attack", "", "You can't attack with this card");
    }

    public void turnOffAttackMode() {
        attackMode = false;
        updateBoard(controller.getBoard());

        Table opponentTable = controller.getBoard().getOpponentTable();
        for (int i = 1; i <= 5; i++) {
            Monster monster = opponentTable.getMonster(i);
            if (monster != null) {
                HBox cardContainer = (HBox) scene.lookup("#opp-monster-" + i);
                cardContainer.getChildren().get(0).getStyleClass().remove("attackable-card");
            }
        }

        Button cancelAttackButton = (Button) scene.lookup("#cancel-attack-btn");
        cancelAttackButton.setOpacity(0);
        cancelAttackButton.setDisable(true);
    }


    private void activateEffect() {
        controller.checkActivateEffect();
    }

    public void showActivateEffectMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                ViewUtility.showInformationAlert("Activate Effect", "", "No card is selected yet");
                break;
            case ONLY_FOR_SPELLS:
                ViewUtility.showInformationAlert("Activate Effect", "", "Activate effect is only for spell cards");
                break;
            case CANT_ACTIVATE_EFFECT:
                ViewUtility.showInformationAlert("Activate Effect", "", "You can’t activate an effect on this turn");
                break;
            case ACTION_NOT_ALLOWED:
                ViewUtility.showInformationAlert("Activate Effect", "", "You can’t do this action in this phase");
                break;
            case CARD_ALREADY_ACTIVATED:
                ViewUtility.showInformationAlert("Activate Effect", "", "You have already activated this card");
                break;
            case SPELL_ZONE_FULL:
                ViewUtility.showInformationAlert("Activate Effect", "", "Spell card zone is full");
                break;
            case PREPARATIONS_NOT_DONE_YET:
                ViewUtility.showInformationAlert("Activate Effect", "", "Preparations of this spell are not done yet");
                break;
            case SPELL_TRAP_ACTIVATED:
                break;
            default:
                ViewUtility.showInformationAlert("Activate Effect", "", "Unexpected error");
        }
    }


    public void showRitualSummonMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_WAY_TO_RITUAL_SUMMON:
                ViewUtility.showInformationAlert("Ritual Summon", "", "There is no way you could ritual summon a monster");
                break;
            case RITUAL_SUMMON_RIGHT_NOW:
                ViewUtility.showInformationAlert("Ritual Summon", "", "You should ritual summon right now");
                break;
            case DONT_MATCH_WITH_RITUAL_MONSTER:
                ViewUtility.showInformationAlert("Ritual Summon", "", "Selected monsters levels don’t match with ritual monster");
                break;
            case SUMMON_SUCCESSFUL:
                break;
            default:
                ViewUtility.showInformationAlert("Ritual Summon", "", "Unexpected error");
        }
    }


    private void showGraveyard(boolean isOpp) {
        try {
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Graveyard");
            stage.initModality(Modality.APPLICATION_MODAL);

            Parent root = FXMLLoader.load(ViewUtility.class.getResource("/fxml/show-cards.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            FlowPane cardsContainer = (FlowPane) scene.lookup("#cards-container");
            Button backButton = (Button) scene.lookup("#back-btn");
            backButton.setOnMouseClicked(e -> stage.close());

            Label title = (Label) scene.lookup("#title");
            title.setText("Graveyard");

            Table targetTable = isOpp ? controller.getBoard().getOpponentTable() : controller.getBoard().getPlayerTable();
            for (Card card : targetTable.getGraveyard()) {
                ImageView cardImage = ViewUtility.getCardImageView(card.getName());
                cardImage.getStyleClass().add("card-image");
                cardImage.setFitWidth(184);
                cardImage.setFitHeight(300);
                cardImage.setOnMouseClicked(e -> ViewUtility.showCard(card.getName()));

                VBox container = new VBox(2, cardImage);
                container.setPrefWidth(184);
                container.setPrefHeight(300);
                cardsContainer.getChildren().add(container);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showFlipCoinResult(User player, User opponent, CoinSide coinSide) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/flip-coin.fxml"));
            Scene scene = new Scene(root);
            MainView.stage.setScene(scene);

            Label resultLabel = (Label) scene.lookup("#result-label");

            String resultMessage = player.getNickname() + " starts duel!";
            Timeline showStarterTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(2), e -> {
                        this.setDuelScene();
                        this.updatePlayersInfo(player, opponent);
                        controller.startNextRound(player, opponent);
                    })
            );
            showStarterTimeline.setCycleCount(1);

            ImageView coinImage = (ImageView) scene.lookup("#coin-image");
            FlipCoinAnimation flipCoinAnimation = new FlipCoinAnimation(coinImage);
            flipCoinAnimation.setCycleCount(coinSide == CoinSide.HEADS ? 10 : 11);
            flipCoinAnimation.setOnFinished(e -> {
                resultLabel.setText(resultMessage);
                showStarterTimeline.play();
            });

            double duration = coinSide == CoinSide.HEADS ? 2000 : 2100;
            ThrowCoinAnimation throwCoinAnimation = new ThrowCoinAnimation(coinImage, duration);

            Timeline waiter = new Timeline(
                    new KeyFrame(Duration.seconds(1), e -> {
                        throwCoinAnimation.play();
                        flipCoinAnimation.play();
                    })
            );
            waiter.play();
        } catch (IOException e) {
            System.out.println("Failed to load flip coin scene");
        }
    }


    public void showTurn(String playerNickname, boolean isQuick) {
        String message;
        if (isQuick) {
            message = "Now it's " + playerNickname + "'s turn!";
            deselectCard();
        } else message = "It's " + playerNickname + "'s turn!";

        HBox messageLabelContainer = (HBox) scene.lookup("#message-label-container");
        messageLabelContainer.getChildren().clear();
        messageLabelContainer.getChildren().add(new Label(message));
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), e -> messageLabelContainer.getChildren().clear());
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(1);
        timeline.play();

        Circle turnCircle = (Circle) scene.lookup("#turn-circle");
        turnCircle.getStyleClass().clear();
        turnCircle.getStyleClass().add("turn-active");

        Circle oppTurnCircle = (Circle) scene.lookup("#opp-turn-circle");
        oppTurnCircle.getStyleClass().clear();
        oppTurnCircle.getStyleClass().add("turn-inactive");
    }


    public void showRoundWinner(String winnerNickname, User player, User opponent) {
        HBox messageLabelContainer = (HBox) scene.lookup("#message-label-container");
        messageLabelContainer.getChildren().clear();
        messageLabelContainer.getChildren().add(new Label(winnerNickname + " won the round!"));
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), e -> {
            messageLabelContainer.getChildren().clear();
            controller.reset();
            controller.startNextRound(player, opponent);
        });
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(1);
        timeline.play();
    }

    public void showDuelWinner(int score1, int score2, User player1, User player2) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Duel Result");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/duel-result.fxml"));
            Scene duelResultScene = new Scene(root);
            stage.setScene(duelResultScene);
            stage.show();

            ImageView player1Image = (ImageView) duelResultScene.lookup("#player-1-image");
            String player1ImagePath = "/images/profile-pics/" + player1.getProfilePictureName();
            player1Image.setImage(new Image(getClass().getResource(player1ImagePath).toExternalForm()));

            Label player1Label = (Label) duelResultScene.lookup("#player-1-nickname");
            player1Label.setText(player1.getNickname());

            ImageView player2Image = (ImageView) duelResultScene.lookup("#player-2-image");
            String player2ImagePath = "/images/profile-pics/" + player2.getProfilePictureName();
            player2Image.setImage(new Image(getClass().getResource(player2ImagePath).toExternalForm()));

            Label player2Label = (Label) duelResultScene.lookup("#player-2-nickname");
            player2Label.setText(player2.getNickname());

            Label resultLabel = (Label) duelResultScene.lookup("#result-label");
            resultLabel.setText(score1 + " - " + score2);

            Button backButton = (Button) duelResultScene.lookup("#back-btn");
            backButton.setOnMouseClicked(e -> {
                stage.close();
                MainMenuController mainMenuController = new MainMenuController(controller.getPlayerOne());
                MainMenuView mainMenuView = new MainMenuView(mainMenuController);
                mainMenuView.setMainMenuScene();
            });
            backButton.setOnAction(e -> {
                stage.close();
                MainMenuController mainMenuController = new MainMenuController(controller.getPlayerOne());
                MainMenuView mainMenuView = new MainMenuView(mainMenuController);
                mainMenuView.setMainMenuScene();
            });
        } catch (IOException e) {
            System.out.println("Failed to load duel result scene");
        }
    }


    public void setBackground(DuelBackgroundType backgroundType) {
        AnchorPane board = (AnchorPane) scene.lookup("#board");
        board.getStyleClass().clear();
        switch (backgroundType) {
            case DEFAULT:
                board.getStyleClass().add("default-board");
                break;
            case FOREST:
                board.getStyleClass().add("forest-board");
                break;
            case YAMI:
                board.getStyleClass().add("yami-board");
                break;
            case UMI:
                board.getStyleClass().add("umi-board");
                break;
        }
    }


    @Override
    public void runCheat(String command) {
        if (command.matches("^increase --LP \\d+$"))
            increaseLP(command.split("\\s"));
        else if (command.matches("^duel set-winner \\S+$"))
            setWinner(command.split("\\s"));
    }


    private void increaseLP(String[] command) {
        int amount;
        try {
            amount = Integer.parseInt(command[2]);
        } catch (NumberFormatException e) {
            return;
        }
        controller.increaseLP(amount);
    }


    private void setWinner(String[] command) {
        String nickname = command[2];
        controller.setWinner(nickname);
    }
}
