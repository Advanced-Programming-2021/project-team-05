package utils;

import control.DataManager;
import control.controller.ImportExportController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Deck;
import model.ScoreboardItem;
import model.User;
import model.card.Card;
import model.template.CardTemplate;
import view.ImportExportMenuView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class ViewUtility {

    // ToDo: move these to DeckView
    private static Card selectedMainCard;
    private static ImageView selectedMainCardImage;
    private static Card selectedSideCard;
    private static ImageView selectedSideCardImage;


    public static void showConfirmationAlert(String title, String header, String message, String cancelText, String confirmText, Listener listener) {
        Alert alert = getAlert(title, header, message);

        ButtonType cancelButtonType = new ButtonType(cancelText, ButtonBar.ButtonData.NO);
        ButtonType confirmButtonType = new ButtonType(confirmText, ButtonBar.ButtonData.APPLY);
        alert.getButtonTypes().add(cancelButtonType);
        alert.getButtonTypes().add(confirmButtonType);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get().equals(confirmButtonType)) listener.onConfirm();
        else listener.onCancel();
    }

    public static void showInformationAlert(String title, String header, String message) {
        Alert alert = getAlert(title, header, message);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().add(okButtonType);

        alert.show();
    }

    private static Alert getAlert(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("game-alert");
        dialogPane.getStylesheets().add(ViewUtility.class.getResource("/css/alert.css").toExternalForm());
        return alert;
    }


    public static void showPromptAlert(String title, String message, String label, String okText, PromptListener listener) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(message);
        textInputDialog.setContentText(label);
        textInputDialog.getDialogPane().getStyleClass().add("game-alert");
        textInputDialog.getDialogPane().getStylesheets().add(ViewUtility.class.getResource("/css/alert.css").toExternalForm());

        textInputDialog.getDialogPane().getButtonTypes().clear();
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        ButtonType confirmButtonType = new ButtonType(okText, ButtonBar.ButtonData.OK_DONE);
        textInputDialog.getDialogPane().getButtonTypes().add(cancelButtonType);
        textInputDialog.getDialogPane().getButtonTypes().add(confirmButtonType);

        Optional<String> result = textInputDialog.showAndWait();
        String input = result.orElse(null);
        if (input != null) listener.onOk(input);
        else listener.onCancel();
    }


    public static void showCard(String cardName) {
        try {
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle(cardName);
            stage.initModality(Modality.APPLICATION_MODAL);

            Parent root = FXMLLoader.load(ViewUtility.class.getResource("/fxml/card.fxml"));
            Scene scene = new Scene(root);
            VBox container = (VBox) scene.lookup("#container");

            String address = "/images/cards/" + cardName.replace(" ", "_") + ".jpg";
            ImageView cardImage = new ImageView(new Image(ViewUtility.class.getResource(address).toExternalForm()));
            cardImage.setFitWidth(334);
            cardImage.setFitHeight(500);
            container.getChildren().add(0, cardImage);

            String description = DataManager.getInstance().getCardTemplateByName(cardName).detailedToString();
            TextArea descriptionArea = new TextArea(description);
            descriptionArea.setId("description-area");
            descriptionArea.setEditable(false);
            descriptionArea.setWrapText(true);
            descriptionArea.setMinHeight(150);
            descriptionArea.setMaxHeight(150);
            container.getChildren().add(1, descriptionArea);

            scene.lookup("#back-button").setOnMouseClicked(e -> stage.close());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static ImageView getCardImage(String cardName) {
        String imageAddress = "/images/cards/" + cardName.replaceAll(" ", "_") + ".jpg";
        return new ImageView(new Image(ViewUtility.class.getResource(imageAddress).toExternalForm()));
    }


    // ToDo: move this to ScoreboardView
    public static void updateScoreboardScene(Scene scoreboardScene, String userNickname) {
        // TODO: 2021-06-26 delete this
        ObservableList<ScoreboardItem> scoreboardItems = FXCollections.observableArrayList();
        for (int i = 0; i < 20; i++) {
            scoreboardItems.add(new ScoreboardItem(String.valueOf(i + 1), "test" + i, String.valueOf(20 - i)));
        }

        TableColumn<ScoreboardItem, String> rankColumn = new TableColumn<>("Rank");
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        rankColumn.setId("rank-column");
        rankColumn.setPrefWidth(100);
        rankColumn.setResizable(false);
        rankColumn.setSortable(false);

        TableColumn<ScoreboardItem, String> nicknameColumn = new TableColumn<>("Nickname");
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        nicknameColumn.setId("nickname-column");
        nicknameColumn.setPrefWidth(230);
        nicknameColumn.setResizable(false);
        nicknameColumn.setSortable(false);

        TableColumn<ScoreboardItem, String> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreColumn.setId("score-column");
        scoreColumn.setPrefWidth(150);
        scoreColumn.setResizable(false);
        scoreColumn.setSortable(false);

        TableView<ScoreboardItem> tableView = new TableView<>();
        tableView.setId("scoreboard-table");
        tableView.getColumns().addAll(rankColumn, nicknameColumn, scoreColumn);
        // TODO: 2021-06-26 delete next line, uncomment other line
        tableView.setItems(scoreboardItems);
//        tableView.setItems(DataManager.getInstance().getScoreboardItems());
        tableView.setEditable(true);
        tableView.setRowFactory(tv -> new TableRow<ScoreboardItem>() {
            @Override
            public void updateItem(ScoreboardItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) return;
                if (userNickname.equals(item.getNickname())) setId("current-user-row");
                else setId(null);
            }
        });

        HBox scoreboardContainer = (HBox) scoreboardScene.lookup("#scoreboard-container");
        scoreboardContainer.getChildren().clear();
        scoreboardContainer.getChildren().add(tableView);
    }


    // ToDo: move these to ProfileView
    public static void updateProfileScene(Scene profileScene, User user) {
        Label usernameLabel = (Label) profileScene.lookup("#username-label");
        usernameLabel.setText("Username: " + user.getUsername());

        Label nicknameLabel = (Label) profileScene.lookup("#nickname-label");
        nicknameLabel.setText("Nickname: " + user.getNickname());

        String imagePath = "/images/profile-pics/" + user.getProfilePictureName();
        ImageView imageView = new ImageView(new Image(ViewUtility.class.getResource(imagePath).toExternalForm()));
        imageView.setId("profile-pic");
        HBox profilePicContainer = (HBox) profileScene.lookup("#profile-pic-container");
        profilePicContainer.getChildren().add(imageView);
    }

    public static void updateChangeNicknameScene(Scene changeNicknameScene, String userNickname) {
        Label nicknameLabel = (Label) changeNicknameScene.lookup("#nickname-label");
        nicknameLabel.setText("Nickname: " + userNickname);
    }


    // ToDo: move this to ShopView
    public static void updateShopScene(Scene shopScene, User user) {
        Label moneyLabel = (Label) shopScene.lookup("#money-label");
        moneyLabel.setText("Money: " + user.getMoney());

        DataManager dataManager = DataManager.getInstance();
        FlowPane cardsContainer = (FlowPane) shopScene.lookup("#cards-container");
        cardsContainer.getChildren().clear();
        for (CardTemplate template : dataManager.getCardTemplates()) {
            ImageView cardImage = ViewUtility.getCardImage(template.getName());
            cardImage.getStyleClass().add("shop-image");
            cardImage.setFitWidth(184);
            cardImage.setFitHeight(300);
            cardImage.setOnMouseClicked(e -> ViewUtility.showCard(template.getName()));

            Label countLabel = new Label("Purchased: " + user.getPurchasedCardsByName(template.getName()).size());
            countLabel.getStyleClass().addAll("default-label", "number-label");

            Button buyButton = new Button("Buy (" + template.getPrice() + ")");
            buyButton.getStyleClass().addAll("default-button", "buy-button");
            if (user.getMoney() < template.getPrice()) buyButton.setDisable(true);

            VBox container = new VBox(2, cardImage, countLabel, buyButton);
            container.setPrefWidth(184);
            container.setPrefHeight(364);
            cardsContainer.getChildren().add(container);
        }
    }


    // ToDo: move this to DeckView
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


    // ToDo: move these to DeckView
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


    // ToDo: move these to Import/Export
    public static void initializeImportExportScene(Stage stage, Scene importExportScene) {
        ChoiceBox<String> typeChoiceBox = new ChoiceBox<>();
        typeChoiceBox.setId("type-box");
        typeChoiceBox.setValue("Select Card Type");
        typeChoiceBox.getItems().add("Select Card Type");
        typeChoiceBox.getItems().add("Monster");
        typeChoiceBox.getItems().add("Spell");
        typeChoiceBox.getItems().add("Trap");
        HBox choiceBoxContainer = (HBox) importExportScene.lookup("#type-choice-box-container");
        choiceBoxContainer.getChildren().add(typeChoiceBox);

        Button fileButton = (Button) importExportScene.lookup("#file-btn");
        fileButton.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Card");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) fileButton.setText(selectedFile.getName());
        });
        fileButton.setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) e.acceptTransferModes(TransferMode.ANY);
        });
        fileButton.setOnDragDropped(e -> {
            File selectedFile = e.getDragboard().getFiles().get(0);
            if (selectedFile.getName().matches(".*?\\.json")) fileButton.setText(selectedFile.getName());
            else ViewUtility.showInformationAlert("Import", "Error", "File type should be json");
        });

        Button exportButton = (Button) importExportScene.lookup("#export-btn");
        exportButton.setOnMouseClicked(e -> exportCard());
    }

    private static void exportCard() {
        try {
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Export Card");
            stage.initModality(Modality.APPLICATION_MODAL);

            Parent root = FXMLLoader.load(ViewUtility.class.getResource("/fxml/show-cards.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            FlowPane cardsContainer = (FlowPane) scene.lookup("#cards-container");
            Button backButton = (Button) scene.lookup("#back-btn");
            backButton.setOnMouseClicked(e -> stage.close());
            ArrayList<CardTemplate> cardTemplates = DataManager.getInstance().getCardTemplates();
            cardTemplates.sort(Comparator.comparing(CardTemplate::getName));
            cardTemplates.sort(Comparator.comparing(template -> template.getClass().getSimpleName()));
            for (CardTemplate template : cardTemplates) {
                ImageView cardImage = ViewUtility.getCardImage(template.getName());
                cardImage.getStyleClass().add("card-image");
                cardImage.setFitWidth(184);
                cardImage.setFitHeight(300);
                cardImage.setOnMouseClicked(e -> {
                    // ToDo: export card
                    stage.close();
                });

                Button showButton = new Button("Show");
                showButton.getStyleClass().addAll("default-button", "show-button");
                showButton.setOnMouseClicked(e -> ViewUtility.showCard(template.getName()));

                VBox container = new VBox(2, cardImage, showButton);
                container.setPrefWidth(184);
                container.setPrefHeight(332);
                cardsContainer.getChildren().add(container);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
