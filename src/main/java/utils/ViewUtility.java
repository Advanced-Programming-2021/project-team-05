package utils;

import control.DataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Deck;
import model.ScoreboardItem;
import model.User;
import model.template.CardTemplate;

import java.io.IOException;
import java.util.Optional;

public class ViewUtility {

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


    public static void addScoreboardToScene(Scene scene, String userNickname) {
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

        HBox scoreboardContainer = (HBox) scene.lookup("#scoreboard-container");
        scoreboardContainer.getChildren().add(tableView);
    }


    public static void setProfileValues(Scene scene, User user) {
        Label usernameLabel = (Label) scene.lookup("#username-label");
        usernameLabel.setText("Username: " + user.getUsername());

        Label nicknameLabel = (Label) scene.lookup("#nickname-label");
        nicknameLabel.setText("Nickname: " + user.getNickname());

        String imagePath = "/images/profile-pics/" + user.getProfilePictureName();
        ImageView imageView = new ImageView(new Image(ViewUtility.class.getResource(imagePath).toExternalForm()));
        imageView.setId("profile-pic");
        HBox profilePicContainer = (HBox) scene.lookup("#profile-pic-container");
        profilePicContainer.getChildren().add(imageView);
    }


    public static void initializeShop(Scene shopScene, User user) {
        Label moneyLabel = (Label) shopScene.lookup("#money-label");
        moneyLabel.setText("Money: " + user.getMoney());
        addCards(shopScene, user);
    }

    private static void addCards(Scene shopScene, User user) {
        DataManager dataManager = DataManager.getInstance();
        FlowPane cardsContainer = (FlowPane) shopScene.lookup("#cards-container");
        for (CardTemplate template : dataManager.getCardTemplates()) {
            String imageAddress = "/images/cards/" + template.getName().replaceAll(" ", "_") + ".jpg";
            ImageView cardImage = new ImageView(new Image(ViewUtility.class.getResource(imageAddress).toExternalForm()));
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


    public static void initializeDeck(Scene deckScene, User user) {
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
        addBox.getChildren().add(addButton);
        decksContainer.getChildren().add(addBox);
    }
}
