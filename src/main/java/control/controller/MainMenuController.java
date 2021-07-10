package control.controller;

import control.DataManager;
import control.message.MainMenuMessage;
import model.Deck;
import model.User;
import view.DuelMenuView;
import view.MainMenuView;

public class MainMenuController {

    private MainMenuView view;
    private final User user;


    public MainMenuController(User user) {

        this.user = user;
    }


    public void setView(MainMenuView view) {
        this.view = view;
    }

    public User getUser() {
        return this.user;
    }


    public final boolean startDuelWithUser(String opponentUsername, int rounds) {
        if (user.getUsername().equals(opponentUsername)) {
            view.showStartDuelMessage(MainMenuMessage.CANT_DUEL_WITH_YOURSELF, user.getUsername());
            return false;
        }
        DataManager dataManager = DataManager.getInstance();
        User opponent = dataManager.getUserByUsername(opponentUsername);
        if (opponent == null) {
            view.showStartDuelMessage(MainMenuMessage.NO_PLAYER_EXISTS, user.getUsername());
            return false;
        }
        Deck userDeck = user.getActiveDeck();
        if (userDeck == null) {
            view.showStartDuelMessage(MainMenuMessage.NO_ACTIVE_DECK, user.getUsername());
            return true;
        }
        Deck opponentDeck = opponent.getActiveDeck();
        if (opponentDeck == null) {
            view.showStartDuelMessage(MainMenuMessage.NO_ACTIVE_DECK, opponentUsername);
            return false;
        }
        if (!userDeck.isValid()) {
            view.showStartDuelMessage(MainMenuMessage.INVALID_DECK, user.getUsername());
            return true;
        }
        if (!opponentDeck.isValid()) {
            view.showStartDuelMessage(MainMenuMessage.INVALID_DECK, opponentUsername);
            return false;
        }
        DuelMenuController duelMenuController = new DuelMenuController(user, opponent, rounds);
        new DuelMenuView(duelMenuController);
        duelMenuController.startDuel();
        return true;
    }

    public final boolean startDuelWithAi(int rounds) {
        Deck userDeck = user.getActiveDeck();
        if (userDeck == null) {
            view.showStartDuelMessage(MainMenuMessage.NO_ACTIVE_DECK, user.getUsername());
            return true;
        }
        if (!userDeck.isValid()) {
            view.showStartDuelMessage(MainMenuMessage.INVALID_DECK, user.getUsername());
            return true;
        }
        User opponent = DataManager.getInstance().getAi();
        DuelMenuController duelMenuController = new DuelMenuController(user, opponent, rounds);
        new DuelMenuView(duelMenuController);
        duelMenuController.startDuel();
        return true;
    }
}
