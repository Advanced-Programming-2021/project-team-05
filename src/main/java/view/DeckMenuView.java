package view;

import control.controller.DeckMenuController;
import control.controller.MainMenuController;
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
import utils.Listener;
import utils.PromptListener;
import utils.ViewUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;


public class DeckMenuView {

    private final DeckMenuController controller;
    private Scene scene;

    private Card selectedMainCard;
    private ImageView selectedMainCardImage;
    private Card selectedSideCard;
    private ImageView selectedSideCardImage;


    public DeckMenuView(DeckMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }


    public void setDeckScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/deck.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeDeckSceneButtons();
            updateDeckScene(scene, controller.getUser());
        } catch (IOException e) {
            System.out.println("Failed to load deck scene");
        }
    }

    private void initializeDeckSceneButtons() {
        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> {
            MainMenuController mainMenuController = new MainMenuController(controller.getUser());
            MainMenuView mainMenuView = new MainMenuView(mainMenuController);
            mainMenuView.setMainMenuScene();
        });
    }

    public void updateDeckScene(Scene deckScene, User user) {
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
            editButton.setOnMouseClicked(event -> setEditDeckScene(deck));
            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().addAll("default-button", "delete-button");
            deleteButton.setOnMouseClicked(event -> ViewUtility.showConfirmationAlert("Deck", "Delete Deck", "Are you sure you want to delete this deck?", "No", "Yes", new Listener() {
                @Override
                public void onConfirm() {
                    controller.deleteDeck(deck.getName());
                    updateDeckScene(deckScene, user);
                }

                @Override
                public void onCancel() {
                }
            }));
            HBox buttonContainer = new HBox(5, editButton, deleteButton);
            buttonContainer.setPrefWidth(300);

            Button activateButton = new Button("Activate Deck");
            activateButton.getStyleClass().addAll("default-button", "activate-button");
            if (deck.equals(user.getActiveDeck())) activateButton.setDisable(true);
            activateButton.setOnMouseClicked(event -> {
                user.setActiveDeck(deck);
                updateDeckScene(deckScene, user);
            });

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
        addButton.setOnMouseClicked(event -> ViewUtility.showPromptAlert("Create new Deck",
                "Enter the deck name:",
                "Deck name",
                "Create",
                new PromptListener() {
                    @Override
                    public void onOk(String input) {
                        controller.createDeck(input);
                        updateDeckScene(deckScene, user);
                    }

                    @Override
                    public void onCancel() {
                    }
                }));
        addBox.getChildren().add(addButton);
        decksContainer.getChildren().add(addBox);
    }


    private void setEditDeckScene(Deck deck) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/edit-deck.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeEditDeckSceneButtons();
            updateEditDeckScene(scene, deck, controller.getUser());
        } catch (IOException e) {
            System.out.println("Failed to load edit deck scene");
        }
    }

    private void initializeEditDeckSceneButtons() {
        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> setDeckScene());
    }

    public void updateEditDeckScene(Scene editDeckScene, Deck deck, User user) {
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

    private void updateCardsContainer(Scene editDeckScene, FlowPane cardsContainer, Deck deck, User user, boolean isSide) {
        cardsContainer.getChildren().clear();
        ArrayList<Card> cards = isSide ? deck.getSideDeck() : deck.getMainDeck();
        cards.sort(Comparator.comparing(Card::getName));
        cards.sort(Comparator.comparing(card -> card.getClass().getSimpleName()));
        for (Card card : cards) {
            ImageView cardImage = ViewUtility.getCardImageView(card.getName());
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

    private void selectDeselect(ImageView cardImage, Card card, boolean isSide) {
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

    private void updateButtons(Scene scene, Deck deck, User user) {
        updateAddButtons(scene, deck, user);
        updateRemoveButtons(scene);
        updateMoveButtons(scene, deck);
    }

    private void updateAddButtons(Scene scene, Deck deck, User user) {
        boolean hasCard = deck.getAddableCards(user.getPurchasedCards()).size() != 0;

        Button mainAddButton = (Button) scene.lookup("#main-add-btn");
        mainAddButton.setDisable(!hasCard || deck.isMainDeckFull());

        Button sideAddButton = (Button) scene.lookup("#side-add-btn");
        sideAddButton.setDisable(!hasCard || deck.isSideDeckFull());
    }

    private void updateMoveButtons(Scene scene, Deck deck) {
        Button mainMoveButton = (Button) scene.lookup("#main-move-btn");
        mainMoveButton.setDisable(selectedMainCard == null || deck.isSideDeckFull());

        Button sideMoveButton = (Button) scene.lookup("#side-move-btn");
        sideMoveButton.setDisable(selectedSideCard == null || deck.isMainDeckFull());
    }

    private void updateRemoveButtons(Scene scene) {
        Button mainRemoveButton = (Button) scene.lookup("#main-remove-btn");
        mainRemoveButton.setDisable(selectedMainCard == null);

        Button sideRemoveButton = (Button) scene.lookup("#side-remove-btn");
        sideRemoveButton.setDisable(selectedSideCard == null);
    }


    private void addCard(Deck deck, User user, boolean isSide) {
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
                ImageView cardImage = ViewUtility.getCardImageView(card.getName());
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

    public void back() {
        MainView.stage.setScene(scene);
    }

//    ----------------------Phase 1----------------------    //

    public void showCreateDeckMessage(DeckMenuMessage message) {
        switch (message) {
            case DECK_NAME_EXISTS:
                ViewUtility.showInformationAlert("Deck", "Crete Deck", "Deck name exists.");
                break;
            case DECK_CREATED:
                ViewUtility.showInformationAlert("Deck", "Crete Deck", "Deck created successfully.");
                break;
            default:
                ViewUtility.showInformationAlert("Deck", "Crete Deck", "Unexpected error.");
        }
    }

    public void showDeleteDeckMessage(DeckMenuMessage message, String deckName) {
        switch (message) {
            case NO_DECK_EXISTS:
                ViewUtility.showInformationAlert("Deck", "Delete Deck", "deck with name " + deckName + " does not exist");
                break;
            case DECK_DELETED:
                ViewUtility.showInformationAlert("Deck", "Delete Deck", "deck deleted successfully!");
                break;
            default:
                ViewUtility.showInformationAlert("Deck", "Delete Deck", "unexpected error");
        }
    }

    public void showActivateDeckMessage(DeckMenuMessage message, String deckName) {
        switch (message) {
            case NO_DECK_EXISTS:
                ViewUtility.showInformationAlert("Deck", "Activate Deck", "deck with name " + deckName + " does not exist");
                break;
            case DECK_ACTIVATED:
                ViewUtility.showInformationAlert("Deck", "Activate Deck", "deck activated successfully!");
                break;
            default:
                ViewUtility.showInformationAlert("Deck", "Activate Deck", "unexpected error");
        }
    }

    public void showAddCardMessage(DeckMenuMessage message, String deckName, String cardName) {
        switch (message) {
            case NO_CARD_EXISTS:
                ViewUtility.showInformationAlert("Deck", "Add Card", "card with name " + cardName + " does not exist");
                break;
            case NO_DECK_EXISTS:
                ViewUtility.showInformationAlert("Deck", "Add Card", "deck with name " + deckName + " does not exist");
                break;
            case MAIN_DECK_IS_FULL:
                ViewUtility.showInformationAlert("Deck", "Add Card", "main deck is full");
                break;
            case SIDE_DECK_IS_FULL:
                ViewUtility.showInformationAlert("Deck", "Add Card", "side deck is full");
                break;
            case DECK_IS_FULL:
                ViewUtility.showInformationAlert("Deck", "Add Card", "you can't add more cards with name " + cardName + " in deck " + deckName);
                break;
            case CARD_ADDED:
                ViewUtility.showInformationAlert("Deck", "Add Card", "card added to deck successfully!");
                break;
            default:
                ViewUtility.showInformationAlert("Deck", "Add Card", "unexpected error");
        }
    }

    public void showRemoveCardMessage(DeckMenuMessage message, String deckName, String cardName) {
        switch (message) {
            case NO_DECK_EXISTS:
                ViewUtility.showInformationAlert("Deck", "Remove Card", "deck with name " + deckName + " does not exist");
                break;
            case NO_CARD_EXISTS_IN_MAIN_DECK:
                ViewUtility.showInformationAlert("Deck", "Remove Card", "card with name " + cardName + " does not exist in main deck");
                break;
            case NO_CARD_EXISTS_IN_SIDE_DECK:
                ViewUtility.showInformationAlert("Deck", "Remove Card", "card with name " + cardName + " does not exist in side deck");
                break;
            case CARD_REMOVED:
                ViewUtility.showInformationAlert("Deck", "Remove Card", "card removed form deck successfully!");
                break;
            default:
                ViewUtility.showInformationAlert("Deck", "Remove Card", "unexpected error");
        }
    }
}
