package view;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;
import control.controller.*;
import control.message.MainMenuMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import model.User;
import utils.Utility;
import utils.ViewUtility;

import java.io.IOException;


public class MainMenuView {

    private static Scene scene;
    private static MainMenuController controller;

    public void setMainMenuScene() throws IOException {
        controller.setView(new MainMenuView());
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main-menu.fxml"));
        Scene mainMenuScene = new Scene(root);
        mainMenuScene.getStylesheets().add("css/main-menu.css");
        scene = mainMenuScene;
        MainView.stage.setScene(mainMenuScene);
    }

    public static void setController(MainMenuController controller) {
        MainMenuView.controller = controller;
    }

    public void setScoreboardScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/scoreboard.fxml"));
        Scene scoreboardScene = new Scene(root);
        scene = scoreboardScene;
        MainView.stage.setScene(scoreboardScene);
        ViewUtility.updateScoreboardScene(scene , controller.getUser().getNickname());

    }

    public void setShopScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/shop.fxml"));
        Scene scoreboardScene = new Scene(root);
        scene = scoreboardScene;
        MainView.stage.setScene(scoreboardScene);
    }

    public void setDeckScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/deck.fxml"));
        Scene scoreboardScene = new Scene(root);
        scene = scoreboardScene;
        MainView.stage.setScene(scoreboardScene);
        DeckMenuController deckMenuController = new DeckMenuController(controller.getUser());
        deckMenuController.setView(new DeckMenuView());
        DeckMenuView.setController(deckMenuController);
        DeckMenuView.setScene(scene);
        DeckMenuView.updateDeckScene(scene, controller.getUser());
    }

    public void setProfileScene() throws IOException {
        ProfileMenuView.setController(new ProfileMenuController(controller.getUser()));
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/profile.fxml"));
        Scene scoreboardScene = new Scene(root);
        scene = scoreboardScene;
        MainView.stage.setScene(scoreboardScene);

    }

    public void setImportExportScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/import-export.fxml"));
        Scene scoreboardScene = new Scene(root);
        scene = scoreboardScene;
        MainView.stage.setScene(scoreboardScene);
    }

    public void startDuel() throws IOException{
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
                ViewUtility.showInformationAlert("","error" ,"there is no player with this username");
                break;
            case NO_ACTIVE_DECK:
                ViewUtility.showInformationAlert("","error" ,username + " has no active deck");
                break;
            case INVALID_DECK:
                ViewUtility.showInformationAlert("","error" ,username + "'s deck is invalid");
                break;
            case INVALID_ROUND:
                ViewUtility.showInformationAlert("","error" ,"number of rounds is not supported");
                break;
            default:
                ViewUtility.showInformationAlert("","error" ,"unexpected error");
        }
    }

    public void logOut() throws IOException {
        controller = null;
        // Alert alert = new PacmanAlert(Alert.AlertType.INFORMATION, "Log Out", "logged out successfully!", "");
        //alert.setOnCloseRequest(event -> {
        try {
            new LoginMenuView().setWelcomeScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // });
        // alert.show();
    }

    public void back() throws IOException {
        // Alert alert = new PacmanAlert(Alert.AlertType.INFORMATION, "Log Out", "logged out successfully!", "");
        //alert.setOnCloseRequest(event -> {
        try {
            new MainMenuView().setMainMenuScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // });
        // alert.show();
    }


}
