package view;

import com.sanityinc.jargs.CmdLineParser;
import control.controller.LoginMenuController;
import control.message.LoginMenuMessage;
import utils.Utility;


public class LoginMenuView {

    private final LoginMenuController controller;


    public LoginMenuView(LoginMenuController controller) {
        this.controller = controller;
        controller.setView(this);
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
            } else if (command.equals("menu help")) {
                showHelp();
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


    public void showHelp() {
        System.out.println(
                "commands:\r\n" +
                        "user create --username <username> --nickname <nickname> --password <password>\r\n" +
                        "user login --username <username> --password <password>\r\n" +
                        "menu show-current\r\n" +
                        "menu enter\r\n" +
                        "menu exit\r\n" +
                        "menu help\r\n"
        );
    }
}
