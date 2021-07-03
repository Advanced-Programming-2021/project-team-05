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
;
        boolean areFieldsValid = true;

        controller.createUser(username ,password , nickname);
    }

    public void run() {
        Utility.initializeScanner();
        while (true) {
            String command = Utility.getNextLine();
            if (command.startsWith("user create")) {
                createUser(command.split("\\s"));
            } else if (command.startsWith("user login")) {
                loginUser(command.split("\\s"));
            } else if (command.equals("menu show-current")) {
                showCurrentMenu();
            } else if (command.startsWith("menu enter")) {
                System.out.println("please login first");
            } else if (command.equals("menu exit")) {
                break;
            } else {
                System.out.println("invalid command");
            }
        }
    }


    public void createUser(String[] command) {
        if (command.length != 8) {
            System.out.println("invalid command");
            return;
        }

        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option<String> usernameOption = parser.addStringOption('u', "username");
        CmdLineParser.Option<String> nicknameOption = parser.addStringOption('n', "nickname");
        CmdLineParser.Option<String> passwordOption = parser.addStringOption('p', "password");
        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            System.out.println("invalid command");
            return;
        }

        String username = parser.getOptionValue(usernameOption);
        String nickname = parser.getOptionValue(nicknameOption);
        String password = parser.getOptionValue(passwordOption);
        if (username == null || nickname == null || password == null) {
            System.out.println("invalid command");
            return;
        }

        controller.createUser(username, password, nickname);
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
            default:
                System.out.println("unexpected error");
        }
    }


    public void loginUser(String[] command) {
        if (command.length != 6) {
            System.out.println("invalid command");
            return;
        }

        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option<String> usernameOption = parser.addStringOption('u', "username");
        CmdLineParser.Option<String> passwordOption = parser.addStringOption('p', "password");
        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            System.out.println("invalid command");
            return;
        }

        String username = parser.getOptionValue(usernameOption);
        String password = parser.getOptionValue(passwordOption);
        if (username == null || password == null) {
            System.out.println("invalid command");
            return;
        }

        controller.loginUser(username, password);
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


    public void showCurrentMenu() {
        System.out.println("Login Menu");
    }

    public void exit() {
        Platform.exit();
    }

}
