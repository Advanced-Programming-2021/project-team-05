package view;

import com.sanityinc.jargs.CmdLineParser;
import control.controller.MainMenuController;
import control.controller.ProfileMenuController;
import control.message.ProfileMenuMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import model.User;
import utils.Utility;
import utils.ViewUtility;

import java.io.IOException;


public class ProfileMenuView {

    private static ProfileMenuController controller;
    private static Scene scene;


    public static void setController(ProfileMenuController controller) {
        ProfileMenuView.controller = controller;
    }


    public void changeNickname() throws IOException {

        TextField nickname = (TextField) scene.lookup("#nickname");
        String nicknameText = nickname.getText();

        if (nicknameText.length() == 0) {
            ViewUtility.showInformationAlert("change nickname", "Error", "Please fill all fields");
            return;
        }

        controller.changeNickname(nicknameText);
    }

    public void setNicknameScene() throws IOException {
        controller.setView(new ProfileMenuView());
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/change-nickname.fxml"));
        Scene changeNickname = new Scene(root);
        scene = changeNickname;
        MainView.stage.setScene(changeNickname);
    }

    public void setPasswordScene() throws IOException {
        controller.setView(new ProfileMenuView());
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/change-password.fxml"));
        Scene changeNickname = new Scene(root);
        scene = changeNickname;
        MainView.stage.setScene(changeNickname);
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
                back();
                break;
            default:
                ViewUtility.showInformationAlert("changePassword", "Error", "unexpected error!");
        }
    }


    public void back() {
        try {
            new MainMenuView().setMainMenuScene();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
