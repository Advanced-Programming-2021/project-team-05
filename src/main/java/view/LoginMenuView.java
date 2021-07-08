package view;

import control.controller.LoginMenuController;
import control.controller.MainMenuController;
import control.message.LoginMenuMessage;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import model.User;
import utils.ViewUtility;

import java.io.IOException;

public class LoginMenuView {

    private final LoginMenuController controller;
    private Scene scene;

    public LoginMenuView(LoginMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }


    public void setWelcomeScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/welcome.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeWelcomeSceneButtons();
        } catch (IOException e) {
            System.out.println("Failed to load welcome scene");
        }
    }

    private void initializeWelcomeSceneButtons() {
        Button loginButton = (Button) scene.lookup("#login-btn");
        loginButton.setOnMouseClicked(e -> setLoginScene());
        loginButton.setOnAction(e -> setLoginScene());

        Button registerButton = (Button) scene.lookup("#register-btn");
        registerButton.setOnMouseClicked(e -> setRegisterScene());
        registerButton.setOnAction(e -> setRegisterScene());

        Button exitButton = (Button) scene.lookup("#exit-btn");
        exitButton.setOnMouseClicked(e -> exit());
        exitButton.setOnAction(e -> exit());
    }


    public void setLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene loginScene = new Scene(root);
            scene = loginScene;
            MainView.stage.setScene(loginScene);
            initializeLoginSceneButtons();
        } catch (IOException e) {
            System.out.println("Failed to load login scene");
        }
    }

    private void initializeLoginSceneButtons() {
        Button loginButton = (Button) scene.lookup("#login-btn");
        loginButton.setOnMouseClicked(e -> logIn());
        loginButton.setOnAction(e -> logIn());

        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> setWelcomeScene());
        backButton.setOnAction(e -> setWelcomeScene());
    }


    public void setRegisterScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            Scene signupScene = new Scene(root);
            scene = signupScene;
            MainView.stage.setScene(signupScene);
            initializeRegisterSceneButtons();
        } catch (IOException e) {
            System.out.println("Failed to load register scene");
        }
    }

    private void initializeRegisterSceneButtons() {
        Button loginButton = (Button) scene.lookup("#register-btn");
        loginButton.setOnMouseClicked(e -> register());
        loginButton.setOnAction(e -> register());

        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> setWelcomeScene());
        backButton.setOnAction(e -> setWelcomeScene());
    }


    public void logIn() {
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
            MainMenuController mainMenuController = new MainMenuController(user);
            MainMenuView mainMenuView = new MainMenuView(mainMenuController);
            mainMenuView.setMainMenuScene();
        }
    }

    public void showLoginMessage(LoginMenuMessage message) {
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
                setLoginScene();
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
