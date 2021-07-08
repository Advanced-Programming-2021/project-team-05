package view;

import com.sanityinc.jargs.CmdLineParser;
import control.DataManager;
import control.controller.DuelMenuController;
import control.controller.MainMenuController;
import control.controller.Phase;
import control.message.DuelMenuMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.User;
import model.board.*;
import model.board.cell.MonsterCell;
import model.board.cell.SpellTrapCell;
import model.card.Card;
import model.card.Monster;
import model.template.CardTemplate;
import utils.CoinSide;
import utils.ViewUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DuelMenuView implements CheatRunner {

    private final DuelMenuController controller;
    private Scene scene;
    private boolean endDuel;

    private ImageView selectedCardImage;


    public DuelMenuView(DuelMenuController controller) {
        this.controller = controller;
        this.setEndDuel(false);
        controller.setView(this);
    }


    public void setEndDuel(boolean endDuel) {
        this.endDuel = endDuel;
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
        Button testButton = (Button) scene.lookup("#test");

        Button nextPhaseButton = (Button) scene.lookup("#next-phase-btn");
        nextPhaseButton.setOnMouseClicked(e -> controller.goToNextPhase(true));

        Button PauseButton = (Button) scene.lookup("#pause-btn");
        PauseButton.setOnMouseClicked(e -> pauseGame());
    }


    public void updateBoard(Board board) {
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
        if (address.isForOpponent() && cardState.isDown()) {
            cardContainer.getStyleClass().remove("selectable-card-image");
        } else if (address.getZone() == CardAddressZone.GRAVEYARD) {
            cardImage.setOnMouseClicked(e -> showGraveyard(address.isForOpponent()));
            if (!cardContainer.getStyleClass().contains("selectable-card-image"))
                cardContainer.getStyleClass().add("selectable-card-image");
        } else {
            cardImage.setOnMouseClicked(e -> {
                showCardDetails(card);
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
            showCardDetails(card);
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
        if (selectedCardImage == cardImage) {
            controller.deselect(true);
            return;
        }
        if (selectedCardImage != null) selectedCardImage.getStyleClass().remove("selected-card");
        cardImage.getStyleClass().add("selected-card");
        selectedCardImage = cardImage;

        if (address.getZone() == CardAddressZone.HAND) controller.selectCardFromHand(card);
        else controller.selectCard(address);
    }

    public void deselectCard() {
        if (selectedCardImage != null) {
            selectedCardImage.getStyleClass().remove("selected-card");
            clearCardDetails();
            selectedCardImage = null;
        }
    }


    public void showPhase(Phase phase) {
        boolean isOpp = false;
        VBox[] phases = new VBox[6];
        phases[0] = (VBox) scene.lookup("#draw-phase-label");
        phases[1] = (VBox) scene.lookup("#standby-phase-label");
        phases[2] = (VBox) scene.lookup("#main-1-phase-label");
        phases[3] = (VBox) scene.lookup("#battle-phase-label");
        phases[4] = (VBox) scene.lookup("#main-2-phase-label");
        phases[5] = (VBox) scene.lookup("#end-phase-label");

//        if (isOpp) {
//            for (VBox phaseBox : phases) {
//                if (!phaseBox.getStyleClass().contains("phase-label-opp")) {
//                    phaseBox.getStyleClass().add("phase-label-opp");
//                }
//            }
//            Button nextPhaseButton = (Button) scene.lookup("#next-phase-btn");
//            nextPhaseButton.setDisable(true);
//        } else for (VBox phaseBox : phases) phaseBox.getStyleClass().remove("phase-label-opp");

        for (VBox phaseBox : phases) {
            phaseBox.getStyleClass().remove("phase-label-active");
//            phaseBox.getStyleClass().remove("phase-label-active-opp");
        }
        phases[phase.getNumber()].getStyleClass().add("phase-label-active" + (isOpp ? "-opp" : ""));
    }


    public void updateLPs(int playerLP, int opponentLP) {
        ProgressBar lpBar = (ProgressBar) scene.lookup("#lp-bar");
        lpBar.setProgress((double) playerLP / 8000);

        Label lpLabel = (Label) scene.lookup("#lp-label");
        lpLabel.setText(String.valueOf(playerLP));

        ProgressBar oppLPBar = (ProgressBar) scene.lookup("#opp-lp-bar");
        oppLPBar.setProgress((double) opponentLP / 8000);

        Label oppLBLabel = (Label) scene.lookup("#opp-lp-label");
        oppLBLabel.setText(String.valueOf(opponentLP));
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
        Label player1Username = (Label) scene.lookup("#username-label");
        player1Username.setText(player1.getUsername());

        Label player1Nickname = (Label) scene.lookup("#nickname-label");
        player1Nickname.setText(player1.getNickname());

        ImageView player1Pic = (ImageView) scene.lookup("#profile-pic");
        player1Pic.setImage(new Image(ViewUtility.class.getResource("/images/profile-pics/" + player1.getProfilePictureName()).toExternalForm()));

        Label player2Username = (Label) scene.lookup("#opp-username-label");
        player2Username.setText(player2.getUsername());

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


    public ArrayList<Integer> getNumbers(int numbersCount, String message) {
        ArrayList<Integer> numbers = new ArrayList<>();
        System.out.println(message);
        for (int i = 1; i <= numbersCount; i++) {
//            try {
//                String input = Utility.getNextLine();
//                if ("cancel".equals(input)) return null;
//                int number = Integer.parseInt(input);
//                numbers.add(number);
//            } catch (NumberFormatException e) {
//                System.out.println("please enter a number");
//                i--;
//            }
        }
        return numbers;
    }


    public CardAddress getAddress(String[] command) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option<Integer> monsterOption = parser.addIntegerOption('m', "monster");
        CmdLineParser.Option<Integer> spellOption = parser.addIntegerOption('s', "spell");
        CmdLineParser.Option<Integer> handOption = parser.addIntegerOption('h', "hand");
        CmdLineParser.Option<Integer> graveyardOption = parser.addIntegerOption('g', "graveyard");
        CmdLineParser.Option<Boolean> fieldOption = parser.addBooleanOption('f', "field");
        CmdLineParser.Option<Boolean> opponentOption = parser.addBooleanOption('o', "opponent");
        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            return null;
        }

        HashMap<CardAddressZone, Integer> positions = new HashMap<>();
        positions.put(CardAddressZone.MONSTER, parser.getOptionValue(monsterOption));
        positions.put(CardAddressZone.SPELL, parser.getOptionValue(spellOption));
        positions.put(CardAddressZone.HAND, parser.getOptionValue(handOption));
        positions.put(CardAddressZone.GRAVEYARD, parser.getOptionValue(graveyardOption));
        positions.put(CardAddressZone.FIELD, parser.getOptionValue(fieldOption) == null ? null : 0);

        positions.entrySet().removeIf(key -> key.getValue() == null);
        if (positions.size() == 1) {
            Map.Entry<CardAddressZone, Integer> entry = positions.entrySet().iterator().next();
            CardAddressZone zone = entry.getKey();
            int position = entry.getValue();
            boolean isOpponent = parser.getOptionValue(opponentOption, false);

            return new CardAddress(zone, position, isOpponent);
        }
        return null;
    }


    public String getOneOfValues(String firstValue, String secondValue, String message, String invalidMessage) {
        System.out.println(message);
//        while (true) {
//            String state = Utility.getNextLine();
//            if ("cancel".equals(state)) return null;
//            if (state.equals(firstValue) || state.equals(secondValue)) return state;
//            System.out.println(invalidMessage);
//        }
        return "";
    }


    private void select(String[] command) {
        CardAddress cardAddress = getAddress(command);
        if (cardAddress == null || CardAddressZone.GRAVEYARD.equals(cardAddress.getZone())) {
            System.out.println("invalid selection");
            return;
        }

        controller.selectCard(cardAddress);
    }

    public void printSelectMessage(DuelMenuMessage message) {
        switch (message) {
            case INVALID_SELECTION:
                System.out.println("invalid selection");
                break;
            case NO_CARD_FOUND:
                System.out.println("no card found in the given position");
                break;
            case CARD_SELECTED:
                System.out.println("card selected!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void printDeselectMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CARD_DESELECTED:
                System.out.println("card deselected!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void summon() {
        controller.checkSummon(false);
    }

    public void printSummonMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CANT_SUMMON:
                System.out.println("you can’t summon this card");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case MONSTER_ZONE_IS_FULL:
                System.out.println("monster card zone is full");
                break;
            case ALREADY_SUMMONED_SET:
                System.out.println("you already summoned/set on this turn");
                break;
            case NOT_ENOUGH_TRIBUTE:
                System.out.println("there are not enough cards for tribute");
                break;
            case CANT_PLAY_THIS_KIND_OF_MOVES:
                System.out.println("it’s not your turn to play this kind of moves");
                break;
            case SUMMON_SUCCESSFUL:
                System.out.println("summoned successfully!");
                break;
            case SPECIAL_SUMMON_RIGHT_NOW:
                System.out.println("you should special summon right now");
                break;
            default:
                System.out.println("unexpected error");
        }
    }

    public void printTributeSummonMessage(DuelMenuMessage message) {
        switch (message) {
            case INVALID_POSITION:
                System.out.println("invalid position");
                break;
            case NO_MONSTER_ON_ADDRESS:
                System.out.println("there is no monsters in one of addresses");
                break;
            case SUMMON_SUCCESSFUL:
                System.out.println("summoned successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void set() {
        controller.set();
    }

    public void printSetMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CANT_SET:
                System.out.println("you can’t set this card");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case MONSTER_ZONE_IS_FULL:
                System.out.println("monster card zone is full");
                break;
            case SPELL_ZONE_FULL:
                System.out.println("spell card zone is full");
                break;
            case ALREADY_SUMMONED_SET:
                System.out.println("you already summoned/set on this turn");
                break;
            case CANT_PLAY_THIS_KIND_OF_MOVES:
                System.out.println("it’s not your turn to play this kind of moves");
                break;
            case SET_SUCCESSFUL:
                System.out.println("set successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void changePosition(String[] command) {
        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option<String> positionOption = parser.addStringOption('p', "position");

        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            System.out.println("invalid command");
            return;
        }

        String position = parser.getOptionValue(positionOption);
        if (position == null) {
            System.out.println("invalid command");
            return;
        }

        controller.changePosition(position);
    }

    public void printChangePositionMessage(DuelMenuMessage message) {
        switch (message) {
            case INVALID_COMMAND:
                System.out.println("invalid command");
                break;
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CANT_CHANGE_POSITION:
                System.out.println("you can’t change this card position");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case ALREADY_IN_WANTED_POSITION:
                System.out.println("this card is already in the wanted position");
                break;
            case ALREADY_CHANGED_POSITION:
                System.out.println("you already changed this card position in this turn");
                break;
            case POSITION_CHANGED:
                System.out.println("monster card position changed successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void flipSummon() {
        controller.checkFlipSummon();
    }

    public void printFlipSummonMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CANT_CHANGE_POSITION:
                System.out.println("you can’t change this card position");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case CANT_FLIP_SUMMON:
                System.out.println("you can’t flip summon this card");
                break;
            case FLIP_SUMMON_SUCCESSFUL:
                System.out.println("flip summoned successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void directAttack() {
        controller.directAttack();
    }

    public void printDirectAttackMessage(DuelMenuMessage message, int damage) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CANT_ATTACK:
                System.out.println("you can’t attack with this card");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case ALREADY_ATTACKED:
                System.out.println("this card already attacked");
                break;
            case CANT_ATTACK_DIRECTLY:
                System.out.println("you can’t attack the opponent directly");
                break;
            case ATTACK_PREVENTED:
                System.out.println("attack prevented");
                return;
            case DIRECT_ATTACK_SUCCESSFUL:
                System.out.println("you opponent receives " + damage + " battle damage");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void attack(String[] command) {
        int position = Integer.parseInt(command[1]);
        controller.attack(position);
    }

    public void printAttackMessage(DuelMenuMessage message, int damage, String hiddenCardName) {
        if (hiddenCardName != null) System.out.print("opponent’s monster card was " + hiddenCardName + " and ");
        switch (message) {
            case INVALID_POSITION:
                System.out.println("invalid position");
                break;
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case CANT_ATTACK:
                System.out.println("you can’t attack with this card");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case ALREADY_ATTACKED:
                System.out.println("this card already attacked");
                break;
            case NO_CARD_TO_ATTACK:
                System.out.println("there is no card to attack here");
                break;
            case ATTACK_PREVENTED:
                System.out.println("attack prevented");
                break;
            case OPPONENT_ATTACK_POSITION_MONSTER_DESTROYED:
                System.out.println("your opponent’s monster is destroyed and your opponent receives " + damage + " battle damage");
                break;
            case BOTH_ATTACK_POSITION_MONSTERS_DESTROYED:
                System.out.println("both you and your opponent monster cards are destroyed and no one receives damage");
                break;
            case YOUR_ATTACK_POSITION_MONSTER_DESTROYED:
                System.out.println("Your monster card is destroyed and you received " + damage + " battle damage");
                break;
            case OPPONENT_DEFENSE_POSITION_MONSTER_DESTROYED:
                System.out.println("the defense position monster is destroyed");
                break;
            case NO_CARD_DESTROYED_AND_NO_DAMAGE:
                System.out.println("no card is destroyed");
                break;
            case NO_CARD_DESTROYED_WITH_DAMAGE:
                System.out.println("no card is destroyed and you received " + damage + " battle damage");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    private void activateEffect() {
        controller.checkActivateEffect();
    }

    public void printActivateEffectMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_CARD_IS_SELECTED:
                System.out.println("no card is selected yet");
                break;
            case ONLY_FOR_SPELLS:
                System.out.println("activate effect is only for spell cards");
                break;
            case CANT_ACTIVATE_EFFECT:
                System.out.println("you can’t activate an effect on this turn");
                break;
            case ACTION_NOT_ALLOWED:
                System.out.println("you can’t do this action in this phase");
                break;
            case CARD_ALREADY_ACTIVATED:
                System.out.println("you have already activated this card");
                break;
            case SPELL_ZONE_FULL:
                System.out.println("spell card zone is full");
                break;
            case PREPARATIONS_NOT_DONE_YET:
                System.out.println("preparations of this spell are not done yet");
                break;
            case SPELL_TRAP_ACTIVATED:
                System.out.println("spell/trap activated!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void printRitualSummonMessage(DuelMenuMessage message) {
        switch (message) {
            case NO_WAY_TO_RITUAL_SUMMON:
                System.out.println("there is no way you could ritual summon a monster");
                break;
            case RITUAL_SUMMON_RIGHT_NOW:
                System.out.println("you should ritual summon right now");
                break;
            case DONT_MATCH_WITH_RITUAL_MONSTER:
                System.out.println("selected monsters levels don’t match with ritual monster");
                break;
            case SUMMON_SUCCESSFUL:
                System.out.println("summoned successfully!");
                break;
            default:
                System.out.println("unexpected error");
                break;
        }
    }


    public void printCancelMessage(DuelMenuMessage message) {
        switch (message) {
            case ACTION_CANCELED:
                System.out.println("action canceled");
                break;
            case NOTHING_TO_CANCEL:
                System.out.println("there is nothing to cancel");
                break;
            default:
                System.out.println("unexpected error");
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


    private void showSelectedCard() {
        System.out.println(controller.getSelectedCardString());
    }


    private void showCard(String[] command) {
        String cardName;
        cardName = command[2];

        DataManager dataManager = DataManager.getInstance();
        CardTemplate template = dataManager.getCardTemplateByName(cardName);
        if (template == null) System.out.println("invalid card name");
        else System.out.println(template.detailedToString());
    }


    public void showCards(ArrayList<Card> cards, String title) {
        System.out.println(title);
        for (int i = 0, cardsSize = cards.size(); i < cardsSize; i++) {
            Card card = cards.get(i);
            System.out.println((i + 1) + ". " + card);
        }
    }


    public void showFlipCoinResult(String starterNickname, CoinSide coinSide) {
        System.out.println("coin side was " + coinSide.getName() + " and " + starterNickname + " starts duel");
    }


    public void showTurn(String playerNickname) {
        // TODO: 2021-07-08 change
        System.out.println("its " + playerNickname + "'s turn");

        Circle turnCircle = (Circle) scene.lookup("#turn-circle");
        turnCircle.getStyleClass().clear();
        turnCircle.getStyleClass().add("turn-active");

        Circle oppTurnCircle = (Circle) scene.lookup("#opp-turn-circle");
        oppTurnCircle.getStyleClass().clear();
        oppTurnCircle.getStyleClass().add("turn-inactive");
    }

    public void showQuickTurn(String playerNickname) {
        System.out.println("now it will be " + playerNickname + "'s turn");
    }


    public void showHand(ArrayList<Card> hand) {
        showCards(hand, "Hand");
    }


    public void showDrawMessage(Card card) {
        // TODO: 2021-07-08 change
        System.out.println("you drew \"" + card.getName() + "\" from your deck");
    }


    public void printWinnerMessage(boolean isWholeMatch, String winnerUsername, int score1, int score2) {
        // TODO: 2021-07-08 change
        if (isWholeMatch)
            System.out.println(winnerUsername + " won the whole match with score: " + score1 + "-" + score2);
        else System.out.println(winnerUsername + " won the game with score: " + score1 + "-" + score2);
    }


    public void printActionCanceled() {
        // TODO: 2021-07-08 change
        System.out.println("action canceled");
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

    public void showSetWinnerMessage(DuelMenuMessage message) {
        // TODO: 2021-07-08 remove after complete
        switch (message) {
            case INVALID_NICKNAME:
                System.out.println("invalid nickname");
                break;
            case WINNER_SET:
                System.out.println("winner set successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }
}
