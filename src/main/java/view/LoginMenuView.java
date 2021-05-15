package view;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;
import control.controller.LoginMenuController;
import control.message.LoginMenuMessage;
import control.controller.MainMenuController;
import model.User;
import utils.Utility;


public class LoginMenuView {

    private final LoginMenuController loginMenuController;


    public LoginMenuView(LoginMenuController loginMenuController) {
        this.loginMenuController = loginMenuController;
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
        Option<String> usernameOption = parser.addStringOption('u', "username");
        Option<String> nicknameOption = parser.addStringOption('n', "nickname");
        Option<String> passwordOption = parser.addStringOption('p', "password");

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

        LoginMenuMessage message = loginMenuController.createUser(username, password, nickname);
        printCreateUserMessage(username, nickname, message);
    }

    public void printCreateUserMessage(String username, String nickname, LoginMenuMessage message) {
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
        Option<String> usernameOption = parser.addStringOption('u', "username");
        Option<String> passwordOption = parser.addStringOption('p', "password");

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

        User user = loginMenuController.loginUser(username, password);
        if (user == null) {
            System.out.println("username and password didn't match!");
        } else {
            System.out.println("user logged in successfully!");
            MainMenuView mainMenuView = new MainMenuView(new MainMenuController(user));
            mainMenuView.run();
        }
    }


    public void showCurrentMenu() {
        System.out.println("Login Menu");
    }
}
