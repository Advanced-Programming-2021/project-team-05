package view;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;
import controller.ProfileMenuController;
import controller.ProfileMenuMessage;
import utils.Utility;


public class ProfileMenuView {

    private final ProfileMenuController profileMenuController;


    public ProfileMenuView(ProfileMenuController profileMenuController) {
        this.profileMenuController = profileMenuController;
    }


    public void run() {
        while (true) {
            String command = Utility.getNextLine();
            if (command.startsWith("profile change --nickname") || command.startsWith("profile change -n")) {
                changeNickname(command.split("\\s"));
            } else if (command.startsWith("profile change --password") || command.startsWith("profile change -p")) {
                changePassword(command.split("\\s"));
            } else if (command.equals("menu show-current")) {
                showCurrentMenu();
            } else if (command.startsWith("menu enter")) {
                System.out.println("menu navigation is not possible");
            } else if (command.equals("menu exit")) {
                break;
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
        Option<String> nicknameOption = parser.addStringOption('n', "nickname");

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

        ProfileMenuMessage message = profileMenuController.changeNickname(nickname);
        printChangeNicknameMessage(nickname, message);
    }

    public void printChangeNicknameMessage(String nickname, ProfileMenuMessage message) {
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
        Option<String> currentPasswordOption = parser.addStringOption('c', "current");
        Option<String> newPasswordOption = parser.addStringOption('n', "new");

        command[2] = "password";
        try {
            parser.parse(command);
        } catch (CmdLineParser.OptionException e) {
            System.out.println("invalid command");
            return;
        }

        String currentPassword = parser.getOptionValue(currentPasswordOption);
        String newPassword = parser.getOptionValue(newPasswordOption);
        if (currentPassword == null || newPassword == null) {
            System.out.println("invalid command");
            return;
        }

        ProfileMenuMessage message = profileMenuController.changePassword(currentPassword, newPassword);
        printChangePasswordMessage(message);
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
}
