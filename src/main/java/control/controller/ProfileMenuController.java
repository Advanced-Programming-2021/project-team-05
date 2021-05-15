package control.controller;

import control.DataManager;
import control.message.ProfileMenuMessage;
import model.User;


public class ProfileMenuController {

    private final User user;


    public ProfileMenuController(User user) {
        this.user = user;
    }


    public final ProfileMenuMessage changeNickname(String newNickname) {
        DataManager dataManager = DataManager.getInstance();
        if (dataManager.getUserByNickname(newNickname) != null) {
            return ProfileMenuMessage.NICKNAME_EXISTS;
        }

        user.setNickname(newNickname);
        return ProfileMenuMessage.NICKNAME_CHANGED;
    }


    public final ProfileMenuMessage changePassword(String currentPassword, String newPassword) {
        if (!user.getPassword().equals(currentPassword)) {
            return ProfileMenuMessage.INVALID_CURRENT_PASSWORD;
        }
        if (user.getPassword().equals(newPassword)) {
            return ProfileMenuMessage.SAME_NEW_AND_CURRENT_PASSWORD;
        }

        user.setPassword(newPassword);
        return ProfileMenuMessage.PASSWORD_CHANGED;
    }
}
