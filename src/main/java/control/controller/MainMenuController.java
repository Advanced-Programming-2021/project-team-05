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

    public final void startDuelWithUser(String opponentUsername, int rounds) {
        DataManager dataManager = DataManager.getInstance();
        User opponent = dataManager.getUserByUsername(opponentUsername);
        if (opponent == null) {
            view.printStartDuelMessage(MainMenuMessage.NO_PLAYER_EXISTS, user.getUsername());
            return;
        }
        Deck userDeck = user.getActiveDeck();
        if (userDeck == null) {
            view.printStartDuelMessage(MainMenuMessage.NO_ACTIVE_DECK, user.getUsername());
            return;
        }
        Deck opponentDeck = opponent.getActiveDeck();
        if (opponentDeck == null) {
            view.printStartDuelMessage(MainMenuMessage.NO_ACTIVE_DECK, opponentUsername);
            return;
        }
        if (!userDeck.isValid()) {
            view.printStartDuelMessage(MainMenuMessage.INVALID_DECK, user.getUsername());
            return;
        }
        if (!opponentDeck.isValid()) {
            view.printStartDuelMessage(MainMenuMessage.INVALID_DECK, opponentUsername);
            return;
        }
        if (rounds != 1 && rounds != 3) {
            view.printStartDuelMessage(MainMenuMessage.INVALID_ROUND, null);
            return;
        }

        DuelMenuController duelMenuController = new DuelMenuController(user, opponent, rounds);
        DuelMenuView duelMenuView = new DuelMenuView(duelMenuController);
        duelMenuController.startNextRound();
        duelMenuView.run();
    }

    public final void startDuelWithAi(int rounds) {
        Deck userDeck = user.getActiveDeck();
        if (userDeck == null) {
            view.printStartDuelMessage(MainMenuMessage.NO_ACTIVE_DECK, user.getUsername());
            return;
        }
        if (!userDeck.isValid()) {
            view.printStartDuelMessage(MainMenuMessage.INVALID_DECK, user.getUsername());
            return;
        }
        if (rounds != 1 && rounds != 3) {
            view.printStartDuelMessage(MainMenuMessage.INVALID_ROUND, null);
            // ToDo: After completing this function, return should be done here
        }

        // ToDo: start duel with ai
    }
}
