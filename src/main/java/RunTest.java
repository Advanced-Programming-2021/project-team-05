import control.DataManager;
import control.controller.Phase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
import model.board.CardAddress;
import model.board.CardAddressZone;
import model.board.CardState;
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

        addCardsToBoard(scene);
        ViewUtility.updatePlayersInfo(scene, new User("username1", "", "nickname1"), new User("username2", "", "nickname2"));
    }

    private void addCardsToBoard(Scene scene) {
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

                addCardToBoard(scene, card, cardAddress, cardState);
                updateMonsterLabel(scene, card, cardAddress, cardState, (i + j) % 2 == 0);
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

                addCardToBoard(scene, card, cardAddress, cardState);
            }
        }

        Spell fieldSpell = new Spell((SpellTemplate) dataManager.getCardTemplateByName("Forest"));
        CardAddress fieldSpellAddress = new CardAddress(CardAddressZone.FIELD, 0, false);
        addCardToBoard(scene, fieldSpell, fieldSpellAddress, CardState.VERTICAL_UP);

        Spell oppFieldSpell = new Spell((SpellTemplate) dataManager.getCardTemplateByName("Forest"));
        CardAddress oppFieldSpellAddress = new CardAddress(CardAddressZone.FIELD, 0, true);
        addCardToBoard(scene, oppFieldSpell, oppFieldSpellAddress, CardState.VERTICAL_DOWN);

        Monster graveyard = new Monster((MonsterTemplate) dataManager.getCardTemplateByName("Battle Ox"));
        CardAddress graveyardAddress = new CardAddress(CardAddressZone.GRAVEYARD, 0, false);
        addCardToBoard(scene, graveyard, graveyardAddress, CardState.VERTICAL_UP);

        Monster oppGraveyard = new Monster((MonsterTemplate) dataManager.getCardTemplateByName("Dark Blade"));
        CardAddress oppGraveyardAddress = new CardAddress(CardAddressZone.GRAVEYARD, 0, true);
        addCardToBoard(scene, oppGraveyard, oppGraveyardAddress, CardState.VERTICAL_UP);

        for (int j = 0; j < 2; j++) {
            for (int i = 1; i <= 6; i++) {
                Card card;
                CardTemplate cardTemplate = dataManager.getCardTemplates().get(random.nextInt(dataManager.getCardTemplates().size()));
                if (cardTemplate instanceof MonsterTemplate) card = new Monster((MonsterTemplate) cardTemplate);
                else if (cardTemplate instanceof SpellTemplate) card = new Spell((SpellTemplate) cardTemplate);
                else card = new Trap((TrapTemplate) cardTemplate);

                addCardToHand(scene, card, j == 0);
            }
        }
    }


    private void addCardToBoard(Scene scene, Card card, CardAddress cardAddress, CardState cardState) {
        String cardName = cardState.isDown() ? "Unknown" : card.getName();
        ImageView cardImage = ViewUtility.getCardImageView(cardName);
        cardImage.setFitWidth(95);
        cardImage.setFitHeight(95);
        cardImage.setPreserveRatio(true);

        int rotateValue = (cardAddress.isForOpponent() ? 180 : 0) + (cardState.isHorizontal() ? 90 : 0);
        cardImage.setRotate(rotateValue);

        String selector = getSelector(cardAddress);

        HBox cardContainer = (HBox) scene.lookup(selector);
        cardContainer.getChildren().clear();
        cardContainer.getChildren().add(cardImage);
        if (cardAddress.isForOpponent() && cardState.isDown()) {
            cardContainer.getStyleClass().remove("selectable-card-image");
        } else if (cardAddress.getZone() == CardAddressZone.GRAVEYARD) {
            // show graveyard on mouse clicked
            if (!cardContainer.getStyleClass().contains("selectable-card-image"))
                cardContainer.getStyleClass().add("selectable-card-image");
        } else {
            cardImage.setOnMouseClicked(e -> ViewUtility.showCardDetails(scene, card));
            if (!cardContainer.getStyleClass().contains("selectable-card-image"))
                cardContainer.getStyleClass().add("selectable-card-image");
        }
    }

    private void addCardToHand(Scene scene, Card card, boolean isOpp) {
        String cardName = isOpp ? "Unknown" : card.getName();
        ImageView cardImage = ViewUtility.getCardImageView(cardName);
        cardImage.setFitWidth(100);
        cardImage.setFitHeight(95);
        cardImage.setRotate(isOpp ? 180 : 0);
        cardImage.setPreserveRatio(true);

        Image oldImage = cardImage.getImage();
        PixelReader reader = oldImage.getPixelReader();
        int newImageWidth = (int) Math.round(oldImage.getWidth());
        int newImageHeight = (int) (95 * oldImage.getWidth() / 100);
        WritableImage newImage = new WritableImage(reader, 0, 0, newImageWidth, newImageHeight);
        cardImage.setImage(newImage);

        FlowPane hand = (FlowPane) scene.lookup("#" + (isOpp ? "opp-" : "") + "hand");
        hand.getChildren().add(cardImage);
    }

    private void removeCardFromBoard(Scene scene, CardAddress cardAddress) {
        String selector = getSelector(cardAddress);
        HBox cardContainer = (HBox) scene.lookup(selector);
        cardContainer.getChildren().clear();
        cardContainer.getStyleClass().remove("selectable-card-image");
    }

    private void updateMonsterLabel(Scene scene, Monster monster, CardAddress cardAddress, CardState cardState, boolean isEffected) {
        if (cardAddress.getZone() == CardAddressZone.MONSTER) {
            String selector = "#" + (cardAddress.isForOpponent() ? "opp-" : "") + "monster-" + cardAddress.getPosition() + "-label";
            HBox monsterLabels = (HBox) scene.lookup(selector);

            Label attackLabel = (Label) monsterLabels.getChildren().get(0);
            Label slashLabel = (Label) monsterLabels.getChildren().get(1);
            Label defenseLabel = (Label) monsterLabels.getChildren().get(2);
            if (cardAddress.isForOpponent() && cardState.isDown()) {
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

    private void removeMonsterLabel(Scene scene, CardAddress cardAddress) {
        if (cardAddress.getZone() == CardAddressZone.MONSTER) {
            String selector = "#" + (cardAddress.isForOpponent() ? "opp-" : "") + "monster-" + cardAddress.getPosition() + "-label";
            HBox monsterLabels = (HBox) scene.lookup(selector);

            ((Label) monsterLabels.getChildren().get(0)).setText("");
            ((Label) monsterLabels.getChildren().get(1)).setText("");
            ((Label) monsterLabels.getChildren().get(2)).setText("");
        }
    }

    private String getSelector(CardAddress cardAddress) {
        switch (cardAddress.getZone()) {
            case MONSTER:
                return "#" + (cardAddress.isForOpponent() ? "opp-" : "") + "monster-" + cardAddress.getPosition();
            case SPELL:
                return "#" + (cardAddress.isForOpponent() ? "opp-" : "") + "spell-" + cardAddress.getPosition();
            case FIELD:
                return "#" + (cardAddress.isForOpponent() ? "opp-" : "") + "field-spell";
            case GRAVEYARD:
                return "#" + (cardAddress.isForOpponent() ? "opp-" : "") + "graveyard";
            default:
                return "#";
        }
    }


    private void showPhase(Phase phase) {
        VBox drawPhase = (VBox) scene.lookup("#draw-phase-label");
        drawPhase.getStyleClass().remove("phase-label-active");
        VBox standbyPhase = (VBox) scene.lookup("#standby-phase-label");
        standbyPhase.getStyleClass().remove("phase-label-active");
        VBox main1Phase = (VBox) scene.lookup("#main-1-phase-label");
        main1Phase.getStyleClass().remove("phase-label-active");
        VBox battlePhase = (VBox) scene.lookup("#battle-phase-label");
        battlePhase.getStyleClass().remove("phase-label-active");
        VBox main2Phase = (VBox) scene.lookup("#main-2-phase-label");
        main2Phase.getStyleClass().remove("phase-label-active");
        VBox endPhase = (VBox) scene.lookup("#end-phase-label");
        endPhase.getStyleClass().remove("phase-label-active");

        switch (phase) {
            case DRAW:
                drawPhase.getStyleClass().add("phase-label-active");
                break;
            case STANDBY:
                standbyPhase.getStyleClass().add("phase-label-active");
                break;
            case MAIN_1:
                main1Phase.getStyleClass().add("phase-label-active");
                break;
            case BATTLE:
                battlePhase.getStyleClass().add("phase-label-active");
                break;
            case MAIN_2:
                main2Phase.getStyleClass().add("phase-label-active");
                break;
            case END:
                endPhase.getStyleClass().add("phase-label-active");
                break;
        }
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


    private void initializeFonts() {
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Bold.ttf"), 20);
    }
}
