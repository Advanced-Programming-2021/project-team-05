package control.controller;

import control.DataManager;
import control.message.LoginMenuMessage;
import model.User;
import view.LoginMenuView;
import view.MainMenuView;

import java.util.ArrayList;
import java.util.Comparator;


public class LoginMenuController {

    private LoginMenuView view;


    public void setView(LoginMenuView view) {
        this.view = view;
    }


    public final void createUser(String username, String password, String nickname) {
        DataManager dataManager = DataManager.getInstance();
        if (username.contains(" ")) {
            view.printCreateUserMessage(LoginMenuMessage.USERNAME_CONTAIN_SPACE, username, nickname);
            return;
        }
        if (password.contains(" ")) {
            view.printCreateUserMessage(LoginMenuMessage.INVALID_PASSWORD, username, nickname);
            return;
        }
        if (nickname.contains(" ")) {
            view.printCreateUserMessage(LoginMenuMessage.NICKNAME_CONTAIN_SPACE, username, nickname);
            return;
        }
        if (dataManager.getUserByUsername(username) != null) {
            view.printCreateUserMessage(LoginMenuMessage.USERNAME_EXISTS, username, nickname);
            return;
        }
        if (dataManager.getUserByNickname(nickname) != null) {
            view.printCreateUserMessage(LoginMenuMessage.NICKNAME_EXISTS, username, nickname);
            return;
        }

        User user = new User(username, password, nickname);
        dataManager.addUser(user);
        view.printCreateUserMessage(LoginMenuMessage.USER_CREATED, username, nickname);


    }


    public final void loginUser(String username, String password) {
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByUsername(username);
        if (user == null || !password.equals(user.getPassword())) {
            view.printLoginUserMessage(LoginMenuMessage.NO_MATCH);
            return;
        }

        view.printLoginUserMessage(LoginMenuMessage.LOGGED_IN);
        MainMenuController mainMenuController = new MainMenuController(user);
        MainMenuView mainMenuView = new MainMenuView(mainMenuController);
        // mainMenuView.run();
    }
}
