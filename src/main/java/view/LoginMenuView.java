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
import utils.ViewUtility;

import java.io.IOException;

public class LoginMenuView extends View {

    private final LoginMenuController controller;


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
        loginButton.setOnAction(e -> setLoginScene());

        Button registerButton = (Button) scene.lookup("#register-btn");
        registerButton.setOnAction(e -> setRegisterScene());

        Button exitButton = (Button) scene.lookup("#exit-btn");
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
        loginButton.setOnAction(e -> {
            if (controller.isWaiting())
                ViewUtility.showInformationAlert("", "Error", "You can't do this now");
            else logIn();
        });

        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnAction(e -> {
            if (controller.isWaiting())
                ViewUtility.showInformationAlert("", "Error", "You can't do this now");
            else setWelcomeScene();
        });
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
        Button registerButton = (Button) scene.lookup("#register-btn");
        registerButton.setOnAction(e -> {
            if (controller.isWaiting())
                ViewUtility.showInformationAlert("", "Error", "You can't do this now");
            else register();
        });

        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnAction(e -> {
            if (controller.isWaiting())
                ViewUtility.showInformationAlert("", "Error", "You can't do this now");
            else setWelcomeScene();
        });
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
        controller.loginUser(username, password);
    }

    public void showLoginMessage(LoginMenuMessage message) {
        switch (message) {
            case USERNAME_CONTAIN_SPACE:
                ViewUtility.showInformationAlert("Register", "Error", "Username should not contain space!");
                break;
            case PASSWORD_CONTAIN_SPACE:
                ViewUtility.showInformationAlert("Register", "Error", "Password should not contain space!");
                break;
            case NO_MATCH:
                ViewUtility.showInformationAlert("Login", "Incorrect Username or Password", "Username and password don't match!");
                break;
            case LOGGED_IN:
                ViewUtility.showInformationAlert("Login", "Successful", "Logged in successfully!");
                MainMenuController mainMenuController = new MainMenuController();
                MainMenuView mainMenuView = new MainMenuView(mainMenuController);
                mainMenuView.setMainMenuScene();
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

    public void showRegisterMessage(LoginMenuMessage message) {
        switch (message) {
            case USERNAME_EXISTS:
                ViewUtility.showInformationAlert("Register", "Username Exist", "User with entered username already exists");
                break;
            case NICKNAME_EXISTS:
                ViewUtility.showInformationAlert("Register", "Nickname Exist", "User with entered nickname already exists");
                break;
            case USERNAME_CONTAIN_SPACE:
                ViewUtility.showInformationAlert("Register", "Error", "Username should not contain space!");
                break;
            case PASSWORD_CONTAIN_SPACE:
                ViewUtility.showInformationAlert("Register", "Error", "Password should not contain space!");
                break;
            case NICKNAME_CONTAIN_SPACE:
                ViewUtility.showInformationAlert("Register", "Error", "Nickname should not contain space!");
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
