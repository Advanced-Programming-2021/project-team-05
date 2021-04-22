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
        return null;
    }


    public final ProfileMenuMessage changePassword(String currentPassword, String newPassword) {
        return null;
    }
}
