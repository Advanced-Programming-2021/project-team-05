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


    public void changeNickname() {

        TextField nickname = (TextField) scene.lookup("#username-field");
        String nicknameText = nickname.getText();

        if (nicknameText.length() == 0 ) {
            ViewUtility.showInformationAlert("Login", "Error", "Please fill all fields");
            return;
        }

        controller.changeNickname(nicknameText);
    }

    public void printChangeNicknameMessage(ProfileMenuMessage message, String nickname) {
        switch (message) {
            case NICKNAME_EXISTS:
                System.out.println("user with nickname " + nickname + " already exists");
                break;
            case NICKNAME_CHANGED:
                System.out.println("nickname changed successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void changePassword() {

        TextField newPassword = (TextField) scene.lookup("#username-field");
        TextField currentPassword = (TextField) scene.lookup("#password-field");
        String newPasswordText = newPassword.getText();
        String currentPasswordText = currentPassword.getText();

        if (newPasswordText.length() == 0 || currentPasswordText.length() == 0) {
            ViewUtility.showInformationAlert("ChangeNickname", "Error", "Please fill all fields");
            return;
        }

        controller.changePassword(currentPasswordText, newPasswordText);
    }

    public void printChangePasswordMessage(ProfileMenuMessage message) {
        switch (message) {
            case INVALID_CURRENT_PASSWORD:
                System.out.println("current password is invalid");
                break;
            case SAME_NEW_AND_CURRENT_PASSWORD:
                System.out.println("please enter a new password");
                break;
            case PASSWORD_CHANGED:
                System.out.println("password changed successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void back(MouseEvent mouseEvent) {
        try {
            new MainMenuView().setMainMenuScene();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
