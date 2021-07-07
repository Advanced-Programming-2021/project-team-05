package view;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;
import control.controller.*;
import control.message.MainMenuMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
        startDuelButton.setOnMouseClicked(e -> startDuel());

        Button profileButton = (Button) scene.lookup("#profile-btn");
        profileButton.setOnMouseClicked(e -> {
            ProfileMenuController profileMenuController = new ProfileMenuController(controller.getUser());
            ProfileMenuView profileMenuView = new ProfileMenuView(profileMenuController);
            profileMenuView.setProfileScene();
        });

        Button shopButton = (Button) scene.lookup("#shop-btn");
        shopButton.setOnMouseClicked(e -> {
            ShopMenuController shopMenuController = new ShopMenuController(controller.getUser());
            ShopMenuView shopMenuView = new ShopMenuView(shopMenuController);
            shopMenuView.setShopScene();
        });

        Button deckButton = (Button) scene.lookup("#deck-btn");
        deckButton.setOnMouseClicked(e -> {
            DeckMenuController deckMenuController = new DeckMenuController(controller.getUser());
            DeckMenuView deckMenuView = new DeckMenuView(deckMenuController);
            deckMenuView.setDeckScene();
        });

        Button createCardButton = (Button) scene.lookup("#create-card-btn");
        // ToDo: setCreateCardScene

        Button scoreboardButton = (Button) scene.lookup("#scoreboard-btn");
        scoreboardButton.setOnMouseClicked(e -> {
            ScoreboardMenuView scoreboardMenuView = new ScoreboardMenuView(controller.getUser());
            scoreboardMenuView.setScoreboardScene();
        });

        Button importExportButton = (Button) scene.lookup("#import-export-btn");
        importExportButton.setOnMouseClicked(e -> {
            ImportExportMenuController importExportMenuController = new ImportExportMenuController(controller.getUser());
            ImportExportMenuView importExportMenuView = new ImportExportMenuView(importExportMenuController);
            importExportMenuView.setImportExportScene();
        });

        Button logoutButton = (Button) scene.lookup("#logout-btn");
        logoutButton.setOnMouseClicked(e -> logOut());
    }


    public void startDuel() {
        CmdLineParser parser = new CmdLineParser();
        Option<Boolean> newOption = parser.addBooleanOption('n', "new");
        Option<String> opponentUsernameOption = parser.addStringOption('p', "second-player");
        Option<Integer> roundsOption = parser.addIntegerOption('r', "rounds");
        Option<Boolean> aiOption = parser.addBooleanOption('a', "ai");


        boolean newDuel = parser.getOptionValue(newOption, false);
        String opponentUsername = parser.getOptionValue(opponentUsernameOption);
        Integer rounds = parser.getOptionValue(roundsOption);
        boolean ai = parser.getOptionValue(aiOption, false);


        if (!newDuel || rounds == null || (opponentUsername != null) == ai) {
            return;
        }
        if (ai) {
            controller.startDuelWithAi(rounds);
        } else {
            controller.startDuelWithUser(opponentUsername, rounds);
        }
    }

    public void printStartDuelMessage(MainMenuMessage message, String username) {
        switch (message) {
            case NO_PLAYER_EXISTS:
                ViewUtility.showInformationAlert("", "error", "there is no player with this username");
                break;
            case NO_ACTIVE_DECK:
                ViewUtility.showInformationAlert("", "error", username + " has no active deck");
                break;
            case INVALID_DECK:
                ViewUtility.showInformationAlert("", "error", username + "'s deck is invalid");
                break;
            case INVALID_ROUND:
                ViewUtility.showInformationAlert("", "error", "number of rounds is not supported");
                break;
            default:
                ViewUtility.showInformationAlert("", "error", "unexpected error");
        }
    }


    public void logOut() {
        LoginMenuController loginMenuController = new LoginMenuController();
        LoginMenuView loginMenuView = new LoginMenuView(loginMenuController);
        loginMenuView.setWelcomeScene();
    }
}
