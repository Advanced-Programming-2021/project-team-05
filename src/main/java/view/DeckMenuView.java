package view;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;
import control.DataManager;
import control.controller.DeckMenuController;
import control.message.DeckMenuMessage;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Deck;
import model.User;
import model.card.Card;
import model.template.CardTemplate;
import utils.Utility;
import utils.ViewUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;


public class DeckMenuView {

    private static Scene scene;
    private static DeckMenuController controller;

    private static Card selectedMainCard;
    private static ImageView selectedMainCardImage;
    private static Card selectedSideCard;
    private static ImageView selectedSideCardImage;


    public static void setController(DeckMenuController controller) {
        DeckMenuView.controller = controller;
    }


    public static void updateDeckScene(Scene deckScene, User user) {
        FlowPane decksContainer = (FlowPane) deckScene.lookup("#decks-container");
        decksContainer.setAlignment(Pos.TOP_CENTER);
        decksContainer.getChildren().clear();
        for (Deck deck : user.getDecks()) {
            TextArea deckDescription = new TextArea(deck.toString());
            deckDescription.setEditable(false);
            deckDescription.setPrefHeight(150);
            deckDescription.getStyleClass().add("deck-description");

            Button editButton = new Button("Edit");
            editButton.getStyleClass().addAll("default-button", "edit-button");
            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().addAll("default-button", "delete-button");
            HBox buttonContainer = new HBox(5, editButton, deleteButton);
            buttonContainer.setPrefWidth(300);

            Button activateButton = new Button("Activate Deck");
            activateButton.getStyleClass().addAll("default-button", "activate-button");
            if (deck.equals(user.getActiveDeck())) activateButton.setDisable(true);

            VBox container = new VBox(7, deckDescription, buttonContainer, activateButton);
            if (deck.equals(user.getActiveDeck())) container.setId("active-deck-container");
            container.setPrefWidth(250);
            container.setPrefHeight(290);
            decksContainer.getChildren().add(container);
        }
        VBox addBox = new VBox();
        addBox.setPrefWidth(250);
        addBox.setPrefHeight(287);
        addBox.setAlignment(Pos.TOP_CENTER);

        Button addButton = new Button("+");
        addButton.setId("add-button");
        addButton.setPrefWidth(250);
        addButton.setPrefHeight(287);
        addButton.setCursor(Cursor.HAND);
        addBox.getChildren().add(addButton);
        decksContainer.getChildren().add(addBox);
    }

    public static void updateEditDeckScene(Scene editDeckScene, Deck deck, User user) {
        selectedMainCardImage = null;
        selectedMainCard = null;
        selectedSideCardImage = null;
        selectedSideCard = null;

        Label mainDeckLabel = (Label) editDeckScene.lookup("#main-deck-label");
        mainDeckLabel.setText("Main Deck (" + deck.getMainDeckSize() + "/60)");

        Label sideDeckLabel = (Label) editDeckScene.lookup("#side-deck-label");
        sideDeckLabel.setText("Side Deck (" + deck.getSideDeckSize() + "/20)");

        FlowPane mainCardsContainer = (FlowPane) editDeckScene.lookup("#main-cards-container");
        updateCardsContainer(editDeckScene, mainCardsContainer, deck, user, false);

        FlowPane sideCardsContainer = (FlowPane) editDeckScene.lookup("#side-cards-container");
        updateCardsContainer(editDeckScene, sideCardsContainer, deck, user, true);

        updateButtons(editDeckScene, deck, user);
    }

    private static void updateCardsContainer(Scene editDeckScene, FlowPane cardsContainer, Deck deck, User user, boolean isSide) {
        cardsContainer.getChildren().clear();
        ArrayList<Card> cards = isSide ? deck.getSideDeck() : deck.getMainDeck();
        cards.sort(Comparator.comparing(Card::getName));
        cards.sort(Comparator.comparing(card -> card.getClass().getSimpleName()));
        for (Card card : cards) {
            ImageView cardImage = ViewUtility.getCardImage(card.getName());
            cardImage.getStyleClass().add("card-image");
            cardImage.setFitWidth(100);
            cardImage.setFitHeight(165);
            cardImage.setOnMouseClicked(e -> {
                selectDeselect(cardImage, card, isSide);
                updateButtons(editDeckScene, deck, user);
            });

            Button showButton = new Button("Show");
            showButton.getStyleClass().addAll("default-button", "show-button");
            showButton.setOnMouseClicked(e -> ViewUtility.showCard(card.getName()));

            VBox container = new VBox(2, cardImage, showButton);
            container.setPrefWidth(100);
            container.setPrefHeight(205);
            cardsContainer.getChildren().add(container);
        }
        VBox addBox = new VBox();
        addBox.setPrefWidth(100);
        addBox.setPrefHeight(205);
        addBox.setAlignment(Pos.TOP_CENTER);

        Button addButton = new Button("+");
        addButton.getStyleClass().add("add-button");
        addButton.setId((isSide ? "side" : "main") + "-add-btn");
        addButton.setPrefWidth(100);
        addButton.setPrefHeight(205);
        addButton.setCursor(Cursor.HAND);
        addButton.setOnMouseClicked(e -> addCard(deck, user, isSide));
        addBox.getChildren().add(addButton);
        cardsContainer.getChildren().add(addBox);
    }

    private static void selectDeselect(ImageView cardImage, Card card, boolean isSide) {
        if (isSide) {
            if (cardImage.equals(selectedSideCardImage)) {
                selectedSideCardImage = null;
                selectedSideCard = null;
                cardImage.getStyleClass().remove("selected-image");
            } else {
                if (selectedSideCardImage != null) selectedSideCardImage.getStyleClass().remove("selected-image");
                cardImage.getStyleClass().add("selected-image");
                selectedSideCardImage = cardImage;
                selectedSideCard = card;
            }
        } else {
            if (cardImage.equals(selectedMainCardImage)) {
                selectedMainCardImage = null;
                selectedMainCard = null;
                cardImage.getStyleClass().remove("selected-image");
            } else {
                if (selectedMainCardImage != null) selectedMainCardImage.getStyleClass().remove("selected-image");
                cardImage.getStyleClass().add("selected-image");
                selectedMainCardImage = cardImage;
                selectedMainCard = card;
            }
        }
    }

    private static void updateButtons(Scene scene, Deck deck, User user) {
        updateAddButtons(scene, deck, user);
        updateRemoveButtons(scene);
        updateMoveButtons(scene, deck);
    }

    private static void updateAddButtons(Scene scene, Deck deck, User user) {
        boolean hasCard = deck.getAddableCards(user.getPurchasedCards()).size() != 0;

        Button mainAddButton = (Button) scene.lookup("#main-add-btn");
        mainAddButton.setDisable(!hasCard || deck.isMainDeckFull());

        Button sideAddButton = (Button) scene.lookup("#side-add-btn");
        sideAddButton.setDisable(!hasCard || deck.isSideDeckFull());
    }

    private static void updateMoveButtons(Scene scene, Deck deck) {
        Button mainMoveButton = (Button) scene.lookup("#main-move-btn");
        mainMoveButton.setDisable(selectedMainCard == null || deck.isSideDeckFull());

        Button sideMoveButton = (Button) scene.lookup("#side-move-btn");
        sideMoveButton.setDisable(selectedSideCard == null || deck.isMainDeckFull());
    }

    private static void updateRemoveButtons(Scene scene) {
        Button mainRemoveButton = (Button) scene.lookup("#main-remove-btn");
        mainRemoveButton.setDisable(selectedMainCard == null);

        Button sideRemoveButton = (Button) scene.lookup("#side-remove-btn");
        sideRemoveButton.setDisable(selectedSideCard == null);
    }

    private static void addCard(Deck deck, User user, boolean isSide) {
        try {
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Add Card");
            stage.initModality(Modality.APPLICATION_MODAL);

            Parent root = FXMLLoader.load(ViewUtility.class.getResource("/fxml/show-cards.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            FlowPane cardsContainer = (FlowPane) scene.lookup("#cards-container");
            Button backButton = (Button) scene.lookup("#back-btn");
            backButton.setOnMouseClicked(e -> stage.close());
            for (Card card : deck.getAddableCards(user.getPurchasedCards())) {
                ImageView cardImage = ViewUtility.getCardImage(card.getName());
                cardImage.getStyleClass().add("card-image");
                cardImage.setFitWidth(184);
                cardImage.setFitHeight(300);
                cardImage.setOnMouseClicked(e -> {
                    // ToDo: add card to side or main deck, show message, call updateEditDeckScene
                    stage.close();
                });

                Button showButton = new Button("Show");
                showButton.getStyleClass().addAll("default-button", "show-button");
                showButton.setOnMouseClicked(e -> ViewUtility.showCard(card.getName()));

                VBox container = new VBox(2, cardImage, showButton);
                container.setPrefWidth(184);
                container.setPrefHeight(332);
                cardsContainer.getChildren().add(container);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    ----------------------Phase 1----------------------

//    public void createDeck(String[] command) {
//        String deckName = command[2];
//        controller.createDeck(deckName);
//    }
//
    public void printCreateDeckMessage(DeckMenuMessage message, String deckName) {
        switch (message) {
            case DECK_NAME_EXISTS:
                System.out.println("deck with name " + deckName + " already exists");
                break;
            case DECK_CREATED:
                System.out.println("deck created successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }
//
//
//    public void deleteDeck(String[] command) {
//        String deckName = command[2];
//        controller.deleteDeck(deckName);
//    }
//
    public void printDeleteDeckMessage(DeckMenuMessage message, String deckName) {
        switch (message) {
            case NO_DECK_EXISTS:
                System.out.println("deck with name " + deckName + " does not exist");
                break;
            case DECK_DELETED:
                System.out.println("deck deleted successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }
//
//
//    public void activateDeck(String[] command) {
//        String deckName = command[2];
//        controller.activateDeck(deckName);
//    }
//
    public void printActivateDeckMessage(DeckMenuMessage message, String deckName) {
        switch (message) {
            case NO_DECK_EXISTS:
                System.out.println("deck with name " + deckName + " does not exist");
                break;
            case DECK_ACTIVATED:
                System.out.println("deck activated successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }
//
//
//    public void addOrRemoveCard(String[] command, boolean addCard) {
//        CmdLineParser parser = new CmdLineParser();
//        Option<String> deckNameOption = parser.addStringOption('d', "deck");
//        Option<String> cardNameOption = parser.addStringOption('c', "card");
//        Option<Boolean> isSideOption = parser.addBooleanOption('s', "side");
//        try {
//            parser.parse(command);
//        } catch (CmdLineParser.OptionException e) {
//            System.out.println("invalid command");
//            return;
//        }
//
//        String deckName = parser.getOptionValue(deckNameOption);
//        String cardName = parser.getOptionValue(cardNameOption);
//        Boolean isSide = parser.getOptionValue(isSideOption, false);
//        if (deckName == null || cardName == null) {
//            System.out.println("invalid command");
//            return;
//        }
//        if ((isSide && command.length != 7) || (!isSide && command.length != 6)) {
//            System.out.println("invalid command");
//            return;
//        }
//        cardName = cardName.replace('_', ' ');
//
//        if (addCard) {
//            controller.addCard(deckName, cardName, isSide);
//        } else {
//            controller.removeCard(deckName, cardName, isSide);
//        }
//    }
//
    public void printAddCardMessage(DeckMenuMessage message, String deckName, String cardName) {
        switch (message) {
            case NO_CARD_EXISTS:
                System.out.println("card with name " + cardName + " does not exist");
                break;
            case NO_DECK_EXISTS:
                System.out.println("deck with name " + deckName + " does not exist");
                break;
            case MAIN_DECK_IS_FULL:
                System.out.println("main deck is full");
                break;
            case SIDE_DECK_IS_FULL:
                System.out.println("side deck is full");
                break;
            case DECK_IS_FULL:
                System.out.println("you can't add more cards with name " + cardName + " in deck " + deckName);
                break;
            case CARD_ADDED:
                System.out.println("card added to deck successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }
//
    public void printRemoveCardMessage(DeckMenuMessage message, String deckName, String cardName) {
        switch (message) {
            case NO_DECK_EXISTS:
                System.out.println("deck with name " + deckName + " does not exist");
                break;
            case NO_CARD_EXISTS_IN_MAIN_DECK:
                System.out.println("card with name " + cardName + " does not exist in main deck");
                break;
            case NO_CARD_EXISTS_IN_SIDE_DECK:
                System.out.println("card with name " + cardName + " does not exist in side deck");
                break;
            case CARD_REMOVED:
                System.out.println("card removed form deck successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }
//
//
//    public void showAllDecks() {
//        User user = controller.getUser();
//        ArrayList<Deck> allDecks = user.getDecks();
//
//        System.out.println("Decks:");
//
//        System.out.println("Active Deck:");
//        Deck activeDeck = user.getActiveDeck();
//        if (activeDeck != null) {
//            allDecks.remove(activeDeck);
//            System.out.println(activeDeck);
//        }
//
//        System.out.println("Other Decks:");
//        if (allDecks.size() != 0) {
//            allDecks.sort(Comparator.comparing(Deck::getName));
//            for (Deck deck : allDecks) {
//                System.out.println(deck);
//            }
//        }
//    }
//
//
//    public void showDeck(String[] command) {
//        CmdLineParser parser = new CmdLineParser();
//        Option<String> deckNameOption = parser.addStringOption('d', "deck-name");
//        Option<Boolean> isSideOption = parser.addBooleanOption('s', "side");
//        try {
//            parser.parse(command);
//        } catch (CmdLineParser.OptionException e) {
//            System.out.println("invalid command");
//            return;
//        }
//
//        String deckName = parser.getOptionValue(deckNameOption);
//        Boolean isSide = parser.getOptionValue(isSideOption, false);
//        if (deckName == null) {
//            System.out.println("invalid command");
//            return;
//        }
//        if ((isSide && command.length != 5) || (!isSide && command.length != 4)) {
//            System.out.println("invalid command");
//            return;
//        }
//
//        User user = controller.getUser();
//        Deck deck = user.getDeckByName(deckName);
//        if (deck == null) {
//            System.out.println("no deck found");
//        } else {
//            System.out.println(deck.detailedToString(isSide));
//        }
//    }
//
//
//    public void showAllCards() {
//        User user = controller.getUser();
//        ArrayList<Card> cards = user.getPurchasedCards();
//        cards.sort(Comparator.comparing(Card::getName));
//
//        for (Card card : cards) {
//            System.out.println(card);
//        }
//    }
//
//
//    public void showCard(String[] command) {
//        String cardName;
//        try {
//            cardName = command[2].replace('_', ' ');
//        } catch (IndexOutOfBoundsException e) {
//            System.out.println("invalid command");
//            return;
//        }
//        DataManager dataManager = DataManager.getInstance();
//        CardTemplate template = dataManager.getCardTemplateByName(cardName);
//        if (template == null) {
//            System.out.println("invalid card name");
//        } else {
//            System.out.println(template.detailedToString());
//        }
//    }
//
//
//    private void showCurrentMenu() {
//        System.out.println("Deck Menu");
//    }
//
//
//    public void showHelp() {
//        System.out.println(
//                "commands:\r\n" +
//                        "\tdeck create <deck name>\r\n" +
//                        "\tdeck delete <deck name>\r\n" +
//                        "\tdeck set-activate <deck name>\r\n" +
//                        "\tdeck add-card --card <card name> --deck <deck name> --side(optional)\r\n" +
//                        "\tdeck rm-card --card <card name> --deck <deck name> --side(optional)\r\n" +
//                        "\tdeck show --all\r\n" +
//                        "\tdeck show --deck-name <deck name> --side(optional)\r\n" +
//                        "\tdeck show --cards\r\n" +
//                        "\tcard show <card name>\r\n" +
//                        "\tmenu show-current\r\n" +
//                        "\tmenu exit\r\n" +
//                        "\tmenu help"
//        );
//    }
}
