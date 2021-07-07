package control.controller;

import control.DataManager;
import control.message.ProfileMenuMessage;
import model.User;
import view.ProfileMenuView;


public class ProfileMenuController {

    private ProfileMenuView view;
    private final User user;


    public ProfileMenuController(User user) {
        this.user = user;
    }


    public void setView(ProfileMenuView view) {
        this.view = view;
    }


    public User getUser() {
        return this.user;
    }


    public final void changeNickname(String newNickname) {
        DataManager dataManager = DataManager.getInstance();
        if (dataManager.getUserByNickname(newNickname) != null) {
            view.showChangeNicknameMessage(ProfileMenuMessage.NICKNAME_EXISTS, newNickname);
            return;
        }
        user.setNickname(newNickname);
        view.showChangeNicknameMessage(ProfileMenuMessage.NICKNAME_CHANGED, newNickname);
    }


    public final void changePassword(String currentPassword, String newPassword) {
        if (!user.getPassword().equals(currentPassword)) {
            view.showChangePasswordMessage(ProfileMenuMessage.INVALID_CURRENT_PASSWORD);
            return;
        }
        if (user.getPassword().equals(newPassword)) {
            view.showChangePasswordMessage(ProfileMenuMessage.SAME_NEW_AND_CURRENT_PASSWORD);
            return;
        }
        user.setPassword(newPassword);
        view.showChangePasswordMessage(ProfileMenuMessage.PASSWORD_CHANGED);
    }
}
