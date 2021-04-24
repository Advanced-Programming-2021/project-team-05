package view;

import com.sanityinc.jargs.CmdLineParser;
import com.sanityinc.jargs.CmdLineParser.Option;
import controller.ProfileMenuController;
import controller.ProfileMenuMessage;
import utils.Utility;

public class ProfileMenuView {

    private final ProfileMenuController profileMenuController;


    protected ProfileMenuView(ProfileMenuController profileMenuController) {
        this.profileMenuController = profileMenuController;
    }


    public void run() {
        while (true) {
            String command = Utility.getNextLine();
            if (command.startsWith("profile change --nickname")) {
                changeNickname(command.split("\\s"));
            } else if (command.startsWith("profile change --password")) {
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


    private void changeNickname(String[] command) {
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

    private void printChangeNicknameMessage(String nickname, ProfileMenuMessage message) {
        switch (message) {
            case NICKNAME_EXISTS:
                System.out.println("user with nickname " + nickname + " already exists");
                break;
            case NICKNAME_CHANGED:
                System.out.println("nickname changed successfully!");
                break;
            default:
                System.out.println("unexpected error!");
        }
    }


    private void changePassword(String[] command) {
        CmdLineParser parser = new CmdLineParser();
        Option<String> currentPasswordOption = parser.addStringOption('c', "current");
        Option<String> newPasswordOption = parser.addStringOption('n', "new");

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

    private void printChangePasswordMessage(ProfileMenuMessage message) {
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
                System.out.println("unexpected error!");
        }
    }


    private void showCurrentMenu() {
        System.out.println("Profile Menu");
    }
}
