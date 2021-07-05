package control.controller;

import control.DataManager;
import control.message.LoginMenuMessage;
import model.User;
import view.LoginMenuView;

public class LoginMenuController {

    private LoginMenuView view;


    public void setView(LoginMenuView view) {
        this.view = view;
    }


    public final void createUser(String username, String password, String nickname) {
        if (username.contains(" ")) {
            view.showRegisterMessage(LoginMenuMessage.USERNAME_CONTAIN_SPACE, username, nickname);
            return;
        }
        if (nickname.contains(" ")) {
            view.showRegisterMessage(LoginMenuMessage.NICKNAME_CONTAIN_SPACE, username, nickname);
            return;
        }
        if (password.contains(" ")) {
            view.showRegisterMessage(LoginMenuMessage.PASSWORD_CONTAIN_SPACE, username, nickname);
            return;
        }
        DataManager dataManager = DataManager.getInstance();
        if (dataManager.getUserByUsername(username) != null) {
            view.showRegisterMessage(LoginMenuMessage.USERNAME_EXISTS, username, nickname);
            return;
        }
        if (dataManager.getUserByNickname(nickname) != null) {
            view.showRegisterMessage(LoginMenuMessage.NICKNAME_EXISTS, username, nickname);
            return;
        }
        User user = new User(username, password, nickname);
        dataManager.addUser(user);
        view.showRegisterMessage(LoginMenuMessage.USER_CREATED, username, nickname);
    }


    public final User loginUser(String username, String password) {
        if (username.contains(" ")) {
            view.showLoginMessage(LoginMenuMessage.USERNAME_CONTAIN_SPACE);
            return null;
        }
        if (password.contains(" ")) {
            view.showLoginMessage(LoginMenuMessage.PASSWORD_CONTAIN_SPACE);
            return null;
        }
        User user = DataManager.getInstance().getUserByUsername(username);
        if (user == null || !password.equals(user.getPassword())) {
            view.showLoginMessage(LoginMenuMessage.NO_MATCH);
            return null;
        }
        view.showLoginMessage(LoginMenuMessage.LOGGED_IN);
        return user;
    }
}
