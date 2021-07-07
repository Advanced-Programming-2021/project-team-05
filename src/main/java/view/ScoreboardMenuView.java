package view;

import control.controller.MainMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import model.User;
import utils.ViewUtility;

import java.io.IOException;


public class ScoreboardMenuView {

    private final User user;
    private Scene scene;


    public ScoreboardMenuView(User user) {
        this.user = user;
    }


    public void setScoreboardScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/scoreboard.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeScoreboardSceneButtons();
            ViewUtility.updateScoreboardScene(scene, user.getNickname());
        } catch (IOException e) {
            System.out.println("Failed to load main menu scene");
        }
    }

    private void initializeScoreboardSceneButtons() {
        Button logoutButton = (Button) scene.lookup("#back-btn");
        logoutButton.setOnMouseClicked(e -> {
            MainMenuController mainMenuController = new MainMenuController(user);
            MainMenuView mainMenuView = new MainMenuView(mainMenuController);
            mainMenuView.setMainMenuScene();
        });
    }
}
