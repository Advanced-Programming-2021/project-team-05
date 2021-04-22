package controller;

import model.User;

public class MainMenuController {

    private final User user;


    public MainMenuController(User user) {
        this.user = user;
    }


    public User getUser() {
        return this.user;
    }


    public final MainMenuMessage startGame(String opponentUsername, int rounds) {
        return null;
    }
}
