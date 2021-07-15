package view;

import control.controller.*;
import control.message.MainMenuMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.ViewUtility;

import java.io.IOException;

public class MainMenuView {

    private final MainMenuController controller;
    private Scene scene;


    public MainMenuView(MainMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }


    public void setMainMenuScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/main-menu.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeMainMenuSceneButtons();
        } catch (IOException e) {
            System.out.println("Failed to load main menu scene");
        }
    }

    private void initializeMainMenuSceneButtons() {
        Button startDuelButton = (Button) scene.lookup("#start-duel-btn");
        startDuelButton.setOnMouseClicked(e -> showDuelSettingsStage());
        startDuelButton.setOnAction(e -> showDuelSettingsStage());

        Button profileButton = (Button) scene.lookup("#profile-btn");
        profileButton.setOnMouseClicked(e -> {
            ProfileMenuController profileMenuController = new ProfileMenuController();
            ProfileMenuView profileMenuView = new ProfileMenuView(profileMenuController);
            profileMenuView.setProfileScene();
        });
        profileButton.setOnAction(e -> {
            ProfileMenuController profileMenuController = new ProfileMenuController();
            ProfileMenuView profileMenuView = new ProfileMenuView(profileMenuController);
            profileMenuView.setProfileScene();
        });

        Button shopButton = (Button) scene.lookup("#shop-btn");
        shopButton.setOnMouseClicked(e -> {
            ShopMenuController shopMenuController = new ShopMenuController();
            ShopMenuView shopMenuView = new ShopMenuView(shopMenuController);
            shopMenuView.setShopScene();
        });
        shopButton.setOnAction(e -> {
            ShopMenuController shopMenuController = new ShopMenuController();
            ShopMenuView shopMenuView = new ShopMenuView(shopMenuController);
            shopMenuView.setShopScene();
        });

        Button deckButton = (Button) scene.lookup("#deck-btn");
        deckButton.setOnMouseClicked(e -> {
            DeckMenuController deckMenuController = new DeckMenuController();
            DeckMenuView deckMenuView = new DeckMenuView(deckMenuController);
            deckMenuView.setDeckScene();
        });
        deckButton.setOnAction(e -> {
            DeckMenuController deckMenuController = new DeckMenuController();
            DeckMenuView deckMenuView = new DeckMenuView(deckMenuController);
            deckMenuView.setDeckScene();
        });

        Button createCardButton = (Button) scene.lookup("#create-card-btn");
        createCardButton.setOnMouseClicked(e -> {
            CreateCardController createCardController = new CreateCardController(controller.getUser());
            CreateCardView createCardView = new CreateCardView(createCardController);
            createCardView.setCreateCardScene();
        });
        createCardButton.setOnAction(e -> {
            CreateCardController createCardController = new CreateCardController(controller.getUser());
            CreateCardView createCardView = new CreateCardView(createCardController);
            createCardView.setCreateCardScene();
        });

        Button scoreboardButton = (Button) scene.lookup("#scoreboard-btn");
        scoreboardButton.setOnMouseClicked(e -> {
            ScoreboardMenuView scoreboardMenuView = new ScoreboardMenuView(controller.getUser());
            scoreboardMenuView.setScoreboardScene();
        });
        scoreboardButton.setOnAction(e -> {
            ScoreboardMenuView scoreboardMenuView = new ScoreboardMenuView(controller.getUser());
            scoreboardMenuView.setScoreboardScene();
        });

        Button importExportButton = (Button) scene.lookup("#import-export-btn");
        importExportButton.setOnMouseClicked(e -> {
            ImportExportMenuController importExportMenuController = new ImportExportMenuController(controller.getUser());
            ImportExportMenuView importExportMenuView = new ImportExportMenuView(importExportMenuController);
            importExportMenuView.setImportExportScene();
        });
        importExportButton.setOnAction(e -> {
            ImportExportMenuController importExportMenuController = new ImportExportMenuController(controller.getUser());
            ImportExportMenuView importExportMenuView = new ImportExportMenuView(importExportMenuController);
            importExportMenuView.setImportExportScene();
        });

        Button logoutButton = (Button) scene.lookup("#logout-btn");
        logoutButton.setOnMouseClicked(e -> logOut());
        logoutButton.setOnAction(e -> logOut());
    }


    private void showDuelSettingsStage() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Duel Settings");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/duel-settings.fxml"));
            Scene duelSettingScene = new Scene(root);
            stage.setScene(duelSettingScene);
            stage.show();

            ChoiceBox<Integer> roundsChoiceBox = new ChoiceBox<>();
            roundsChoiceBox.setId("rounds-choice-box");
            roundsChoiceBox.setValue(1);
            roundsChoiceBox.getItems().add(1);
            roundsChoiceBox.getItems().add(3);
            VBox choiceBoxContainer = (VBox) duelSettingScene.lookup("#rounds-choice-box-container");
            choiceBoxContainer.getChildren().add(roundsChoiceBox);

            CheckBox aiCheckBox = (CheckBox) duelSettingScene.lookup("#ai-check-box");
            aiCheckBox.setOnMouseClicked(e -> {
                TextField oppUsernameField = (TextField) duelSettingScene.lookup("#opp-username-input");
                oppUsernameField.setDisable(aiCheckBox.isSelected());
            });
            aiCheckBox.setOnAction(e -> {
                TextField oppUsernameField = (TextField) duelSettingScene.lookup("#opp-username-input");
                oppUsernameField.setDisable(aiCheckBox.isSelected());
            });

            Button backButton = (Button) duelSettingScene.lookup("#back-btn");
            backButton.setOnMouseClicked(e -> stage.close());
            backButton.setOnAction(e -> stage.close());

            Button startDuelButton = (Button) duelSettingScene.lookup("#start-duel-btn");
            startDuelButton.setOnMouseClicked(e -> startDuel(stage, duelSettingScene));
            startDuelButton.setOnAction(e -> startDuel(stage, duelSettingScene));
        } catch (IOException e) {
            System.out.println("Failed to load duel settings scene");
        }
    }


    public void startDuel(Stage duelSettingStage, Scene duelSettingScene) {
        CheckBox aiCheckBox = (CheckBox) duelSettingScene.lookup("#ai-check-box");
        boolean ai = aiCheckBox.isSelected();

        ChoiceBox<Integer> roundsChoiceBox = (ChoiceBox<Integer>) duelSettingScene.lookup("#rounds-choice-box");
        int rounds = roundsChoiceBox.getValue();

        boolean closeSettingStage;
        if (ai) {
            closeSettingStage = controller.startDuelWithAi(rounds);
        } else {
            TextField oppUsernameField = (TextField) duelSettingScene.lookup("#opp-username-input");
            String opponentUsername = oppUsernameField.getText();

            closeSettingStage = controller.startDuelWithUser(opponentUsername, rounds);
        }
        if (closeSettingStage) duelSettingStage.close();
    }

    public void showStartDuelMessage(MainMenuMessage message, String username) {
        switch (message) {
            case CANT_DUEL_WITH_YOURSELF:
                ViewUtility.showInformationAlert("Start Duel", "Error", "You can't duel with yourself");
                break;
            case NO_PLAYER_EXISTS:
                ViewUtility.showInformationAlert("Start Duel", "Error", "There is no player with this username");
                break;
            case NO_ACTIVE_DECK:
                ViewUtility.showInformationAlert("Start Duel", "Error", username + " has no active deck");
                break;
            case INVALID_DECK:
                ViewUtility.showInformationAlert("Start Duel", "Error", username + "'s deck is invalid");
                break;
            default:
                ViewUtility.showInformationAlert("Start Duel", "Error", "Unexpected error");
        }
    }


    public void logOut() {
        MainController.logOut();
        LoginMenuController loginMenuController = new LoginMenuController();
        LoginMenuView loginMenuView = new LoginMenuView(loginMenuController);
        loginMenuView.setWelcomeScene();
    }
}
