import control.DataManager;
import control.controller.Phase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Deck;
import model.User;
import model.board.*;
import model.board.cell.MonsterCell;
import model.board.cell.SpellTrapCell;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import utils.ViewUtility;

import java.util.ArrayList;
import java.util.Random;

public class RunTest extends Application {

    public static Scene scene;
    private static ImageView selectedCardImage;

    private static Phase phase;
    private static boolean isOpp = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        initializeFonts();
        DataManager.getInstance().loadData();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/duel.fxml"));
        Scene scene = new Scene(root);
        RunTest.scene = scene;
        stage.setScene(scene);
        stage.setTitle("Yo-Gi-Oh!");
        stage.setResizable(false);
        stage.show();

        User user1 = getUser();
        User user2 = getUser();

        Board board = getBoard(user1, user2);
        updateBoard(board);
        addCardsToBoard();

        showPhase(Phase.DRAW, false);
        phase = Phase.DRAW;

        updatePlayersInfo(user1, user2);
        Button nextPhaseButton = (Button) scene.lookup("#next-phase-btn");
        nextPhaseButton.setOnMouseClicked(e -> nextPhase());
    }

    private Board getBoard(User user1, User user2) {
        Board board = new Board(user1, user2);
        Table playerTable = board.getPlayerTable();
        Table opponentTable = board.getOpponentTable();

        playerTable.initializeHand();
        opponentTable.initializeHand();

        return board;
    }

    private void addCardsToBoard() {
        DataManager dataManager = DataManager.getInstance();
        Random random = new Random();

        for (int j = 0; j < 2; j++) {
            for (int i = 1; i <= 5; i++) {
                CardTemplate cardTemplate;
                do {
                    cardTemplate = dataManager.getCardTemplates().get(random.nextInt(dataManager.getCardTemplates().size()));
                } while (!(cardTemplate instanceof MonsterTemplate));
                MonsterTemplate monsterTemplate = (MonsterTemplate) cardTemplate;
                Monster card = new Monster(monsterTemplate);

                CardAddress cardAddress = new CardAddress(CardAddressZone.MONSTER, i, j != 0);
                CardState cardState;

                if (i <= 2) cardState = CardState.VERTICAL_UP;
                else if (i <= 4) cardState = CardState.HORIZONTAL_DOWN;
                else cardState = CardState.HORIZONTAL_UP;

                addCardToBoard(card, cardAddress, cardState);
                updateMonsterLabel(card, cardAddress, cardState, (i + j) % 2 == 0);
            }
        }

        for (int j = 0; j < 2; j++) {
            for (int i = 1; i <= 5; i++) {
                CardTemplate cardTemplate;
                do {
                    cardTemplate = dataManager.getCardTemplates().get(random.nextInt(dataManager.getCardTemplates().size()));
                } while ((cardTemplate instanceof MonsterTemplate));
                Card card;
                if (cardTemplate instanceof SpellTemplate) card = new Spell((SpellTemplate) cardTemplate);
                else card = new Trap((TrapTemplate) cardTemplate);

                CardAddress cardAddress = new CardAddress(CardAddressZone.SPELL, i, j != 0);
                CardState cardState;

                if (i <= 2) cardState = CardState.VERTICAL_UP;
                else cardState = CardState.VERTICAL_DOWN;

                addCardToBoard(card, cardAddress, cardState);
            }
        }

        Spell fieldSpell = new Spell((SpellTemplate) dataManager.getCardTemplateByName("Forest"));
        CardAddress fieldSpellAddress = new CardAddress(CardAddressZone.FIELD, 0, false);
        addCardToBoard(fieldSpell, fieldSpellAddress, CardState.VERTICAL_UP);

        Spell oppFieldSpell = new Spell((SpellTemplate) dataManager.getCardTemplateByName("Forest"));
        CardAddress oppFieldSpellAddress = new CardAddress(CardAddressZone.FIELD, 0, true);
        addCardToBoard(oppFieldSpell, oppFieldSpellAddress, CardState.VERTICAL_DOWN);

//        Monster graveyard = new Monster((MonsterTemplate) dataManager.getCardTemplateByName("Battle Ox"));
//        CardAddress graveyardAddress = new CardAddress(CardAddressZone.GRAVEYARD, 0, false);
//        addCardToBoard(graveyard, graveyardAddress, CardState.VERTICAL_UP);
//
//        Monster oppGraveyard = new Monster((MonsterTemplate) dataManager.getCardTemplateByName("Dark Blade"));
//        CardAddress oppGraveyardAddress = new CardAddress(CardAddressZone.GRAVEYARD, 0, true);
//        addCardToBoard(oppGraveyard, oppGraveyardAddress, CardState.VERTICAL_UP);

//        for (int j = 0; j < 2; j++) {
//            for (int i = 1; i <= 6; i++) {
//                Card card;
//                CardTemplate cardTemplate = dataManager.getCardTemplates().get(random.nextInt(dataManager.getCardTemplates().size()));
//                if (cardTemplate instanceof MonsterTemplate) card = new Monster((MonsterTemplate) cardTemplate);
//                else if (cardTemplate instanceof SpellTemplate) card = new Spell((SpellTemplate) cardTemplate);
//                else card = new Trap((TrapTemplate) cardTemplate);
//
//                addCardToHand(card, j == 0);
//            }
//        }
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
            // show graveyard on mouse clicked
            if (!cardContainer.getStyleClass().contains("selectable-card-image"))
                cardContainer.getStyleClass().add("selectable-card-image");
        } else {
            cardImage.setOnMouseClicked(e -> {
                showCardDetails(card);
                selectCard(card, cardImage);
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
        if (!isOpp) cardImage.setOnMouseClicked(e -> {
            showCardDetails(card);
            selectCard(card, cardImage);
        });

        Image oldImage = cardImage.getImage();
        PixelReader reader = oldImage.getPixelReader();
        int newImageWidth = (int) Math.round(oldImage.getWidth());
        int newImageHeight = (int) (95 * oldImage.getWidth() / 100);
        WritableImage newImage = new WritableImage(reader, 0, 0, newImageWidth, newImageHeight);
        cardImage.setImage(newImage);

        FlowPane hand = (FlowPane) scene.lookup("#" + (isOpp ? "opp-" : "") + "hand");
        hand.getChildren().add(cardImage);
    }

    private void clearHand(boolean isOpp) {
        FlowPane hand = (FlowPane) scene.lookup("#" + (isOpp ? "opp-" : "") + "hand");
        hand.getChildren().clear();
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


    private void selectCard(Card card, ImageView cardImage) {
        if (selectedCardImage != null) selectedCardImage.getStyleClass().remove("selected-card");
        cardImage.getStyleClass().add("selected-card");
        selectedCardImage = cardImage;
    }


    private void showPhase(Phase phase, boolean isOpp) {
        VBox[] phases = new VBox[6];
        phases[0] = (VBox) scene.lookup("#draw-phase-label");
        phases[1] = (VBox) scene.lookup("#standby-phase-label");
        phases[2] = (VBox) scene.lookup("#main-1-phase-label");
        phases[3] = (VBox) scene.lookup("#battle-phase-label");
        phases[4] = (VBox) scene.lookup("#main-2-phase-label");
        phases[5] = (VBox) scene.lookup("#end-phase-label");

        if (isOpp) {
            for (VBox phaseBox : phases) {
                if (!phaseBox.getStyleClass().contains("phase-label-opp")) {
                    phaseBox.getStyleClass().add("phase-label-opp");
                }
            }
            Button nextPhaseButton = (Button) scene.lookup("#next-phase-btn");
            nextPhaseButton.setDisable(true);
        } else for (VBox phaseBox : phases) phaseBox.getStyleClass().remove("phase-label-opp");

        for (VBox phaseBox : phases) {
            phaseBox.getStyleClass().remove("phase-label-active");
            phaseBox.getStyleClass().remove("phase-label-active-opp");
        }
        phases[phase.getNumber()].getStyleClass().add("phase-label-active" + (isOpp ? "-opp" : ""));
    }


    public static void showCardDetails(Card card) {
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

    public static void clearCardDetails() {
        VBox container = (VBox) scene.lookup("#card-details-box");
        container.getChildren().clear();
    }

    public static void updatePlayersInfo(User player1, User player2) {
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


    public void test() {
        updateLPs(5452, 3546);
        clearCardDetails();
    }


    private void initializeFonts() {
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Bold.ttf"), 20);
    }


    private void nextPhase() {
        phase = phase.getNextPhase();
        if (phase == Phase.DRAW) isOpp = !isOpp;
        showPhase(phase, isOpp);
    }

    private void centerImage(ImageView imageView) {
        Image img = imageView.getImage();
        if (img != null) {
            double x;
            double y;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();
            double reduceCoefficient = Math.min(ratioX, ratioY);

            x = img.getWidth() * reduceCoefficient;
            y = img.getHeight() * reduceCoefficient;

            imageView.setX((imageView.getFitWidth() - x) / 2);
            imageView.setY((imageView.getFitHeight() - y) / 2);
        }
    }

    private User getUser() {
        DataManager dataManager = DataManager.getInstance();
        User user = new User("username", "password", "nickname");
        dataManager.addUser(user);

        Deck deck = new Deck("My Deck");
        user.addDeck(deck);
        user.setActiveDeck(deck);
        dataManager.addDeck(deck);

        Random random = new Random();
        ArrayList<CardTemplate> cardTemplates = dataManager.getCardTemplates();
        for (int i = 0; i < 100; i++) {
            int randomIndex = random.nextInt(cardTemplates.size());
            CardTemplate template = cardTemplates.get(randomIndex);
            Card card;
            if (template instanceof MonsterTemplate) card = new Monster((MonsterTemplate) template);
            else if (template instanceof SpellTemplate) card = new Spell((SpellTemplate) template);
            else card = new Trap((TrapTemplate) template);
            if (deck.isCardFull(card)) {
                i--;
                continue;
            }
            dataManager.addCard(card);
            user.purchaseCard(card);
            if (i < 50) deck.addCardToMainDeck(card);
            else if (i < 60) deck.addCardToSideDeck(card);
        }

        return user;
    }
}
