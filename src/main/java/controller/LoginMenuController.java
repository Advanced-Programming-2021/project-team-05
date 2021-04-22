package controller;

import model.User;

public class LoginMenuController {

    public final LoginMenuMessage createUser(String username, String password, String nickname) {
        DataManager dataManager = DataManager.getInstance();
        if (dataManager.getUserByUsername(username) != null) {
            return LoginMenuMessage.USERNAME_EXISTS;
        }
        if (dataManager.getUserByNickname(nickname) != null) {
            return LoginMenuMessage.NICKNAME_EXISTS;
        }

        User user = new User(username, password, nickname);
        dataManager.addUser(user);
        return LoginMenuMessage.USER_CREATED;
    }


    public final User loginUser(String username, String password) {
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByUsername(username);
        if (user == null || !password.equals(user.getPassword())) {
            return null;
        }

        return user;
    }
}
