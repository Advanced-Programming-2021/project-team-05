import control.DataManager;
import control.controller.DuelMenuController;
import control.controller.LoginMenuController;
import control.controller.MainMenuController;
import model.Deck;
import model.User;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import utils.Utility;
import view.DuelMenuView;
import view.LoginMenuView;
import view.MainMenuView;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        startDuel();
//        DataManager.getInstance().loadData();
//        LoginMenuView loginMenuView = new LoginMenuView(new LoginMenuController());
//        loginMenuView.run();
//        DataManager.getInstance().saveData();
    }

    public static void startDuel() {
        Utility.initializeScanner();
        DataManager manager = DataManager.getInstance();
        manager.loadData();

        User userOne = new User("myUser", "myPass", "myNick");
        User userTwo = new User("opsUser", "opsPass", "opNick");

        manager.addUser(userOne);
        manager.addUser(userTwo);

        ArrayList<CardTemplate> templates = manager.getCardTemplates();
        ArrayList<Card> cards = manager.getCards();
        cards.clear();

        for (CardTemplate cardTemplate : templates) {
            if (cardTemplate instanceof MonsterTemplate) {
                manager.addCard(new Monster((MonsterTemplate) cardTemplate));
            } else if (cardTemplate instanceof SpellTemplate) {
                manager.addCard(new Spell((SpellTemplate) cardTemplate));
            } else {
                manager.addCard(new Trap((TrapTemplate) cardTemplate));
            }
        }

        for (CardTemplate cardTemplate : templates) {
            if (cardTemplate instanceof MonsterTemplate) {
                manager.addCard(new Monster((MonsterTemplate) cardTemplate));
            } else if (cardTemplate instanceof SpellTemplate) {
                manager.addCard(new Spell((SpellTemplate) cardTemplate));
            } else {
                manager.addCard(new Trap((TrapTemplate) cardTemplate));
            }
        }

        Deck deckOne = new Deck("deckOne");
        Deck deckTwo = new Deck("deckTwo");

        manager.addDeck(deckOne);
        manager.addDeck(deckTwo);

        for (int i = 0; i < 47; i++) {
            deckOne.addCardToMainDeck(cards.get(i));
        }
        for (int i = 47; i < 94; i++) {
            deckTwo.addCardToMainDeck(cards.get(i));
        }

        userOne.addDeck(deckOne);
        userTwo.addDeck(deckTwo);

        userOne.setActiveDeck(deckOne);
        userTwo.setActiveDeck(deckTwo);

        MainMenuController controller = new MainMenuController(userOne);
        MainMenuView view = new MainMenuView(controller);

        controller.startDuelWithUser("opsUser", 1);
    }
}
