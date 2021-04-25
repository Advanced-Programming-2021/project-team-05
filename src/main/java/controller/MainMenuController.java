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


    public final MainMenuMessage startGameWithUser(String opponentUsername, int rounds) {
        // TODO: Complete conditions
        if (DataManager.getInstance().getUserByUsername(opponentUsername) == null) {
            return MainMenuMessage.NO_PLAYER_EXISTS;
        } else if (false) {
            return MainMenuMessage.NO_ACTIVE_DECK_1;
        } else if (false) {
            return MainMenuMessage.NO_ACTIVE_DECK_2;
        } else if (false) {
            return MainMenuMessage.INVALID_DECK_1;
        } else if (false) {
            return MainMenuMessage.INVALID_DECK_2;
        } else if (rounds != 3 && rounds != 1) {
            return MainMenuMessage.INVALID_ROUND;
        } else {
            return MainMenuMessage.GAME_STARTED_SUCCESSFULLY;
        }
    }

    public final MainMenuMessage startGameWithAi(int rounds) {
        // TODO: Complete conditions
        if (false) {
            return MainMenuMessage.NO_ACTIVE_DECK_1;
        } else if (false) {
            return MainMenuMessage.INVALID_DECK_1;
        } else if (rounds != 3 && rounds != 1) {
            return MainMenuMessage.INVALID_ROUND;
        } else {
            return MainMenuMessage.GAME_STARTED_SUCCESSFULLY;
        }
    }
}
