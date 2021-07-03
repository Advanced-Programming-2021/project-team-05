package view;

import com.sanityinc.jargs.CmdLineParser;
import control.controller.LoginMenuController;
import control.controller.MainMenuController;
import control.message.LoginMenuMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.User;
import sun.applet.Main;
import utils.Utility;

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
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
        Scene signupScene = new Scene(root);
        signupScene.getStylesheets().add("css/signup.css");
        scene = signupScene;
        MainView.stage.setScene(signupScene);
    }

    public void logIn() {
        TextField usernameField = (TextField) scene.lookup("#username-field");
        TextField passwordField = (TextField) scene.lookup("#password-field");
        String username = usernameField.getText();
        String password = passwordField.getText();

        Label errorLabel = (Label) scene.lookup("#login-error");
        errorLabel.setText("");

        if (username.length() == 0 || password.length() == 0) {
            System.out.println("response: fill in all fields");
            errorLabel.setText("please fill in all fields!");
            return;
        }
        controller.loginUser(username, password);

        System.out.println("logging in...");

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
                System.out.println("user with username " + username + " already exists");
                break;
            case NICKNAME_EXISTS:
                System.out.println("user with nickname " + nickname + " already exists");
                break;
            case USER_CREATED:
                System.out.println("user created successfully!");
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
                System.out.println("username and password didn't match");
                break;
            case LOGGED_IN:
                System.out.println("user logged in successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void showCurrentMenu() {
        System.out.println("Login Menu");
    }


}
