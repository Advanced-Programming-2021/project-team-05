package utils;

import control.DataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
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
import model.ScoreboardItem;
import model.User;
import model.card.Card;
import model.template.CardTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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
            descriptionArea.getStyleClass().add("description-box");
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


    public static Image getCardImage(String cardName) {
        String imageAddress = "/images/cards/" + cardName.replaceAll(" ", "_") + ".jpg";
        return new Image(ViewUtility.class.getResource(imageAddress).toExternalForm());
    }

    public static ImageView getCardImageView(String cardName) {
        return new ImageView(getCardImage(cardName));
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
            ImageView cardImage = ViewUtility.getCardImageView(template.getName());
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
                ImageView cardImage = ViewUtility.getCardImageView(template.getName());
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


    public static void showCardDetails(Scene duelScene, Card card) {
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

        VBox container = (VBox) duelScene.lookup("#card-details-box");
        container.getChildren().clear();
        container.getChildren().addAll(cardImage, descriptionArea);
    }

    public static void updatePlayersInfo(Scene duelScene, User player1, User player2) {
        Label player1Username = (Label) duelScene.lookup("#username-label");
        player1Username.setText(player1.getUsername());

        Label player1Nickname = (Label) duelScene.lookup("#nickname-label");
        player1Nickname.setText(player1.getNickname());

        ImageView player1Pic = (ImageView) duelScene.lookup("#profile-pic");
        player1Pic.setImage(new Image(ViewUtility.class.getResource("/images/profile-pics/" + player1.getProfilePictureName()).toExternalForm()));

        Label player2Username = (Label) duelScene.lookup("#opp-username-label");
        player2Username.setText(player2.getUsername());

        Label player2Nickname = (Label) duelScene.lookup("#opp-nickname-label");
        player2Nickname.setText(player2.getNickname());

        ImageView player2Pic = (ImageView) duelScene.lookup("#opp-profile-pic");
        player2Pic.setImage(new Image(ViewUtility.class.getResource("/images/profile-pics/" + player2.getProfilePictureName()).toExternalForm()));
    }
}
