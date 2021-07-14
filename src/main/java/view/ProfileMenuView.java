package view;

import control.controller.MainController;
import control.controller.MainMenuController;
import control.controller.ProfileMenuController;
import control.message.ProfileMenuMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import model.User;
import utils.ViewUtility;

import java.io.IOException;


public class ProfileMenuView {

    private final ProfileMenuController controller;
    private Scene scene;


    public ProfileMenuView(ProfileMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }

    public static void updateProfileScene(Scene profileScene, User user) {
        Label usernameLabel = (Label) profileScene.lookup("#username-label");
        usernameLabel.setText("Username: " + ((user == null) ? "?" : user.getUsername()));

        Label nicknameLabel = (Label) profileScene.lookup("#nickname-label");
        nicknameLabel.setText("Nickname: " + ((user == null) ? "?" : user.getNickname()));

        if (user != null) {
            String imagePath = "/images/profile-pics/" + user.getProfilePictureName();
            ImageView imageView = new ImageView(new Image(ViewUtility.class.getResource(imagePath).toExternalForm()));
            imageView.setId("profile-pic");
            HBox profilePicContainer = (HBox) profileScene.lookup("#profile-pic-container");
            profilePicContainer.getChildren().add(imageView);
        }
    }

    public void setProfileScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/profile.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeProfileSceneButtons();
            updateProfileScene(scene, MainController.getUser());
        } catch (IOException e) {
            System.out.println("Failed to load profile scene");
        }
    }

    private void initializeProfileSceneButtons() {
        Button changeNicknameButton = (Button) scene.lookup("#change-nickname-btn");
        changeNicknameButton.setOnMouseClicked(e -> setChangeNicknameScene());
        changeNicknameButton.setOnAction(e -> setChangeNicknameScene());

        Button changePasswordButton = (Button) scene.lookup("#change-password-btn");
        changePasswordButton.setOnMouseClicked(e -> setChangePasswordScene());
        changePasswordButton.setOnAction(e -> setChangePasswordScene());

        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> backToMainMenu());
        backButton.setOnAction(e -> backToMainMenu());
    }

    public void setChangeNicknameScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/change-nickname.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeChangeNicknameSceneButtons();
            updateChangeNicknameScene();
        } catch (IOException e) {
            System.out.println("Failed to load change nickname scene");
        }
    }

    private void initializeChangeNicknameSceneButtons() {
        Button changeButton = (Button) scene.lookup("#change-btn");
        changeButton.setOnMouseClicked(e -> changeNickname());
        changeButton.setOnAction(e -> changeNickname());

        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> setProfileScene());
        backButton.setOnAction(e -> setProfileScene());
    }

    public void updateChangeNicknameScene() {
        User user = MainController.getUser();

        Label nicknameLabel = (Label) scene.lookup("#nickname-label");
        nicknameLabel.setText("Nickname: " + (user == null ? "?" : user.getNickname()));
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
        changeButton.setOnAction(e -> changePassword());

        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> setProfileScene());
        backButton.setOnAction(e -> setProfileScene());
    }


    private void backToMainMenu() {
        MainMenuController mainMenuController = new MainMenuController();
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
        setProfileScene();
    }

    public void showChangeNicknameMessage(ProfileMenuMessage message, String nickname) {
        switch (message) {
            case NICKNAME_CONTAIN_WHITESPACE:
                ViewUtility.showInformationAlert("Change Nickname", "Error", "Nickname contain space");
                break;
            case NICKNAME_EXISTS:
                ViewUtility.showInformationAlert("Change Nickname", "Error", "User with nickname " + nickname + " already exists");
                break;
            case NICKNAME_CHANGED:
                updateChangeNicknameScene();
                ViewUtility.showInformationAlert("Change Nickname", "Successful", "Nickname changed successfully!");
                break;
            default:
                ViewUtility.showInformationAlert("Change Nickname", "Error", "Unexpected error");
        }
    }


    public void changePassword() {
        TextField newPassword = (TextField) scene.lookup("#newPassword");
        TextField currentPassword = (TextField) scene.lookup("#oldPassword");
        String newPasswordText = newPassword.getText();
        String currentPasswordText = currentPassword.getText();

        if (newPasswordText.length() == 0 || currentPasswordText.length() == 0) {
            ViewUtility.showInformationAlert("Change Password", "Error", "Please fill all fields");
            return;
        }
        controller.changePassword(currentPasswordText, newPasswordText);
    }

    public void showChangePasswordMessage(ProfileMenuMessage message) {
        switch (message) {
            case INVALID_CURRENT_PASSWORD:
                ViewUtility.showInformationAlert("Change Password", "Error", "Current password is invalid");
                break;
            case PASSWORD_CONTAIN_WHITESPACE:
                ViewUtility.showInformationAlert("Change Password", "Error", "Password contain space");
                break;
            case SAME_NEW_AND_CURRENT_PASSWORD:
                ViewUtility.showInformationAlert("Change Password", "Error", "Please enter a new password");
                break;
            case PASSWORD_CHANGED:
                ViewUtility.showInformationAlert("Change Password", "Successful", "Password changed successfully!");
                setProfileScene();
                break;
            default:
                ViewUtility.showInformationAlert("Change Password", "Error", "Unexpected error!");
        }
    }
}
