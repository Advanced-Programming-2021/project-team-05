package view;

import com.sanityinc.jargs.CmdLineParser;
import control.controller.LoginMenuController;
import control.controller.MainMenuController;
import control.message.LoginMenuMessage;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.User;
import sun.applet.Main;
import utils.Utility;
import utils.ViewUtility;

import java.io.IOException;
import java.util.ArrayList;


public class LoginMenuView {

    private static Scene scene;
    private static LoginMenuController controller;



    public static void setController(LoginMenuController controller) {
       LoginMenuView.controller = controller;
    }



    public void setWelcomeScene() throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/welcome.fxml"));
        Scene welcomeScene = new Scene(root);
        welcomeScene.getStylesheets().add("css/welcome.css");
        scene = welcomeScene;
        MainView.stage.setScene(welcomeScene);
    }

    public void setLoginScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene loginScene = new Scene(root);
        loginScene.getStylesheets().add("css/login.css");
        scene = loginScene;
        MainView.stage.setScene(loginScene);
    }

    public void setSignupScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
        Scene signupScene = new Scene(root);
        signupScene.getStylesheets().add("css/register.css");
        scene = signupScene;
        MainView.stage.setScene(signupScene);
    }

    public void logIn() {
        TextField usernameField = (TextField) scene.lookup("#username-field");
        TextField passwordField = (TextField) scene.lookup("#password-field");
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.length() == 0 || password.length() == 0) {
            ViewUtility.showInformationAlert("","error","fill all the parts");
            return;
        }
        controller.loginUser(username, password);


    }

    public void signUp() {
        TextField usernameField = (TextField) scene.lookup("#username-field");
        TextField passwordField = (TextField) scene.lookup("#password-field");
        TextField nicknameField = (TextField) scene.lookup("#nickname-field");
        String username = usernameField.getText();
        String password = passwordField.getText();
        String nickname = nicknameField.getText();

        if (username.equals("") || nickname.equals("") || password.equals("")) {
            ViewUtility.showInformationAlert("","error","fill all the parts");
            return;
        }
;
        boolean areFieldsValid = true;

        controller.createUser(username ,password , nickname);
    }


    public void printCreateUserMessage(LoginMenuMessage message, String username, String nickname) {
        switch (message) {
            case USERNAME_EXISTS:
                ViewUtility.showInformationAlert("","username exist","user with username " + username + " already exists");
                break;
            case NICKNAME_EXISTS:
                ViewUtility.showInformationAlert("","nickname exist","user with nickname " + nickname + " already exists");
                break;
            case USER_CREATED:
                ViewUtility.showInformationAlert("","successful","user created successfully!");
                break;
            case INVALID_PASSWORD:
                ViewUtility.showInformationAlert("","error","password should not contain space!");
                break;
            case USERNAME_CONTAIN_SPACE:
                ViewUtility.showInformationAlert("","error","username should not contain space!");
                break;
            case NICKNAME_CONTAIN_SPACE:
                ViewUtility.showInformationAlert("","error","nickname should not contain space!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void printLoginUserMessage(LoginMenuMessage message) {
        switch (message) {
            case NO_MATCH:
                ViewUtility.showInformationAlert("","incorrect user or pass","username and password dont match!");
                break;
            case LOGGED_IN:
                ViewUtility.showInformationAlert("","successful","logged in successfully!");
                break;
            default:
                ViewUtility.showInformationAlert("","error","unexpected error");

        }
    }

    public void exit() {
        Platform.exit();
    }

}
