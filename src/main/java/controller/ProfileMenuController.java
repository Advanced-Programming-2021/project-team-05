package controller;

import model.User;

public class ProfileMenuController {

    private final User user;


    public ProfileMenuController(User user) {
        this.user = user;
    }


    public User getUser() {
        return this.user;
    }


    public final ProfileMenuMessage changeNickname(String newNickname) {
        if (DataManager.getInstance().getUserByNickname(newNickname) != null) {
            return ProfileMenuMessage.NICKNAME_EXISTS;
        } else {
            user.setNickname(newNickname);
            return ProfileMenuMessage.NICKNAME_CHANGED;
        }
    }


    public final ProfileMenuMessage changePassword(String currentPassword, String newPassword) {
        if (!user.getPassword().equals(currentPassword)) {
            return ProfileMenuMessage.INVALID_CURRENT_PASSWORD;
        } else if (user.getPassword().equals(newPassword)) {
            return ProfileMenuMessage.SAME_NEW_AND_CURRENT_PASSWORD;
        } else {
            user.setPassword(newPassword);
            return ProfileMenuMessage.PASSWORD_CHANGED;
        }
    }
}
