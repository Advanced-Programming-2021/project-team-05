package view;

import control.controller.LoginMenuController;
import control.controller.MainMenuController;
import control.message.LoginMenuMessage;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import model.User;
import utils.ViewUtility;

import java.io.IOException;

public class LoginMenuView {

    private static Scene scene;
    private static LoginMenuController controller;


    public static void setController(LoginMenuController controller) {
        LoginMenuView.controller = controller;
    }


    public void setWelcomeScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/welcome.fxml"));
        Scene welcomeScene = new Scene(root);
        scene = welcomeScene;
        MainView.stage.setScene(welcomeScene);
    }

    public void setLoginScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene loginScene = new Scene(root);
        scene = loginScene;
        MainView.stage.setScene(loginScene);
    }

    public void setRegisterScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
        Scene signupScene = new Scene(root);
        scene = signupScene;
        MainView.stage.setScene(signupScene);
    }

    public void setMainMenuScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main-menu.fxml"));
        Scene signupScene = new Scene(root);
        scene = signupScene;
        MainView.stage.setScene(signupScene);
    }


    public void logIn() throws IOException {
        TextField usernameField = (TextField) scene.lookup("#username-field");
        TextField passwordField = (TextField) scene.lookup("#password-field");
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.length() == 0 || password.length() == 0) {
            ViewUtility.showInformationAlert("Login", "Error", "Please fill all fields");
            return;
        }
        User user = controller.loginUser(username, password);
        if (user != null) {
            MainMenuView.setController(new MainMenuController(user));
            setMainMenuScene();
        }
    }

    public void showLoginMessage(LoginMenuMessage message) throws IOException {
        switch (message) {
            case NO_MATCH:
                ViewUtility.showInformationAlert("Login", "Incorrect Username or Password", "Username and password don't match!");
                break;
            case LOGGED_IN:
                ViewUtility.showInformationAlert("Login", "Successful", "Logged in successfully!");
                break;
            default:
                ViewUtility.showInformationAlert("Login", "Error", "Unexpected error");
        }
    }


    public void register() {
        TextField usernameField = (TextField) scene.lookup("#username-field");
        TextField passwordField = (TextField) scene.lookup("#password-field");
        TextField nicknameField = (TextField) scene.lookup("#nickname-field");
        String username = usernameField.getText();
        String password = passwordField.getText();
        String nickname = nicknameField.getText();

        if (username.length() == 0 || nickname.length() == 0 || password.length() == 0) {
            ViewUtility.showInformationAlert("Signup", "Error", "Please fill all fields");
            return;
        }
        controller.createUser(username, password, nickname);
    }

    public void showRegisterMessage(LoginMenuMessage message, String username, String nickname) {
        switch (message) {
            case USERNAME_EXISTS:
                ViewUtility.showInformationAlert("Register", "Username Exist", "User with username " + username + " already exists");
                break;
            case NICKNAME_EXISTS:
                ViewUtility.showInformationAlert("Register", "Nickname Exist", "User with nickname " + nickname + " already exists");
                break;
            case USERNAME_CONTAIN_SPACE:
                ViewUtility.showInformationAlert("Register", "Error", "Username should not contain whitespace!");
                break;
            case PASSWORD_CONTAIN_SPACE:
                ViewUtility.showInformationAlert("Register", "Error", "Password should not contain whitespace!");
                break;
            case NICKNAME_CONTAIN_SPACE:
                ViewUtility.showInformationAlert("Register", "Error", "Nickname should not contain whitespace!");
                break;
            case USER_CREATED:
                try {
                    setLoginScene();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ViewUtility.showInformationAlert("Register", "Successful", "User created successfully!");
                break;
            default:
                ViewUtility.showInformationAlert("Register", "Error", "Unexpected error");
        }
    }


    public void exit() {
        Platform.exit();
    }
}
