package view;

import com.sanityinc.jargs.CmdLineParser;
import control.controller.ProfileMenuController;
import control.message.ProfileMenuMessage;
import utils.Utility;


public class ProfileMenuView {

    private final ProfileMenuController controller;


    public ProfileMenuView(ProfileMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }


    public void run() {
        while (true) {
            String command = Utility.getNextLine();
            if (command.startsWith("profile change --nickname") || command.startsWith("profile change -n")) {
                changeNickname(command.split("\\s"));
            } else if (command.startsWith("profile change")) {
                changePassword(command.split("\\s"));
            } else if (command.equals("menu show-current")) {
                showCurrentMenu();
            } else if (command.startsWith("menu enter")) {
                System.out.println("menu navigation is not possible");
            } else if (command.equals("menu exit")) {
                break;
            } else if (command.equals("menu help")) {
                showHelp();
            } else {
                System.out.println("invalid command");
            }
        }
    }


    public void changeNickname(String[] command) {
        if (command.length != 4) {
            System.out.println("invalid command");
            return;
        }

        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option<String> nicknameOption = parser.addStringOption('n', "nickname");
        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            System.out.println("invalid command");
            return;
        }

        String nickname = parser.getOptionValue(nicknameOption);
        if (nickname == null) {
            System.out.println("invalid command");
            return;
        }

        controller.changeNickname(nickname);
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


    public void changePassword(String[] command) {
        if (command.length != 7) {
            System.out.println("invalid command");
            return;
        }

        CmdLineParser parser = new CmdLineParser();
        CmdLineParser.Option<Boolean> passwordOption = parser.addBooleanOption('p', "password");
        CmdLineParser.Option<String> currentPasswordOption = parser.addStringOption('c', "current");
        CmdLineParser.Option<String> newPasswordOption = parser.addStringOption('n', "new");
        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            System.out.println("invalid command");
            return;
        }

        boolean password = parser.getOptionValue(passwordOption, false);
        String currentPassword = parser.getOptionValue(currentPasswordOption);
        String newPassword = parser.getOptionValue(newPasswordOption);
        if (!password || currentPassword == null || newPassword == null) {
            System.out.println("invalid command");
            return;
        }

        controller.changePassword(currentPassword, newPassword);
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


    public void showCurrentMenu() {
        System.out.println("Profile Menu");
    }


    public void showHelp() {
        System.out.println(
                "commands:\r\n" +
                        "\tprofile change --nickname <nickname>\r\n" +
                        "\tprofile change --password --current <current password> --new <new password>\r\n" +
                        "\tmenu show-current\r\n" +
                        "\tmenu exit\r\n" +
                        "\tmenu help"
        );
    }
}
