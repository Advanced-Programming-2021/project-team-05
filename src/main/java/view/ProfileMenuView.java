package view;

import control.controller.MainMenuController;
import control.controller.ProfileMenuController;
import control.message.ProfileMenuMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import utils.ViewUtility;

import java.io.IOException;


public class ProfileMenuView {

    private final ProfileMenuController controller;
    private Scene scene;


    public ProfileMenuView(ProfileMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }


    public void setProfileScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/profile.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeProfileSceneButtons();
        } catch (IOException e) {
            System.out.println("Failed to load profile scene");
        }
    }

    private void initializeProfileSceneButtons() {
        Button changeNicknameButton = (Button) scene.lookup("#change-nickname-btn");
        changeNicknameButton.setOnMouseClicked(e -> setChangeNicknameScene());

        Button changePasswordButton = (Button) scene.lookup("#change-password-btn");
        changePasswordButton.setOnMouseClicked(e -> setChangePasswordScene());

        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> backToMainMenu());
    }


    public void setChangeNicknameScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/change-nickname.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeChangeNicknameSceneButtons();
        } catch (IOException e) {
            System.out.println("Failed to load change nickname scene");
        }
    }

    private void initializeChangeNicknameSceneButtons() {
        Button changeButton = (Button) scene.lookup("#change-btn");
        changeButton.setOnMouseClicked(e -> changeNickname());

        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> setProfileScene());
    }


    public void setChangePasswordScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/change-password.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeChangePasswordSceneButtons();
        } catch (IOException e) {
            System.out.println("Failed to load change password scene");
        }
    }

    private void initializeChangePasswordSceneButtons() {
        Button changeButton = (Button) scene.lookup("#change-btn");
        changeButton.setOnMouseClicked(e -> changePassword());

        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> setProfileScene());
    }


    private void backToMainMenu() {
        MainMenuController mainMenuController = new MainMenuController(controller.getUser());
        MainMenuView mainMenuView = new MainMenuView(mainMenuController);
        mainMenuView.setMainMenuScene();
    }


    public void changeNickname() {
        TextField nickname = (TextField) scene.lookup("#nickname");
        String nicknameText = nickname.getText();

        if (nicknameText.length() == 0) {
            ViewUtility.showInformationAlert("change nickname", "Error", "Please fill all fields");
            return;
        }

        controller.changeNickname(nicknameText);
    }

    public void printChangeNicknameMessage(ProfileMenuMessage message, String nickname) {
        switch (message) {
            case NICKNAME_EXISTS:
                ViewUtility.showInformationAlert("ChangeNickname", "Error", "user with nickname " + nickname + " already exists");
                break;
            case NICKNAME_CHANGED:
                ViewUtility.showInformationAlert("ChangeNickname", "successful", "nickname changed successfully!");
                break;
            default:
                ViewUtility.showInformationAlert("ChangeNickname", "Error", "unexpected error");
        }
    }


    public void changePassword() {

        TextField newPassword = (TextField) scene.lookup("#newPassword");
        TextField currentPassword = (TextField) scene.lookup("#oldPassword");
        String newPasswordText = newPassword.getText();
        String currentPasswordText = currentPassword.getText();

        if (newPasswordText.length() == 0 || currentPasswordText.length() == 0) {
            ViewUtility.showInformationAlert("changePassword", "Error", "Please fill all fields");
            return;
        }
        controller.changePassword(currentPasswordText, newPasswordText);
    }

    public void printChangePasswordMessage(ProfileMenuMessage message) {
        switch (message) {
            case INVALID_CURRENT_PASSWORD:
                ViewUtility.showInformationAlert("changePassword", "Error", "current password is invalid");
                break;
            case SAME_NEW_AND_CURRENT_PASSWORD:
                ViewUtility.showInformationAlert("changePassword", "Error", "please enter a new password");
                break;
            case PASSWORD_CHANGED:
                ViewUtility.showInformationAlert("changePassword", "Successful", "password changed successfully!");
                backToMainMenu();
                break;
            default:
                ViewUtility.showInformationAlert("changePassword", "Error", "unexpected error!");
        }
    }
}
