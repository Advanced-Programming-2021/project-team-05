package model.effect.action;

import control.DataManager;
import control.controller.DuelMenuController;
import model.Deck;
import model.board.Table;
import model.card.Card;
import model.template.property.CardType;
import view.DuelMenuView;

import java.util.ArrayList;

public class AddOneFiledSpellFromDeckToHandAction implements Action {
    @Override
    public void run(DuelMenuController controller) {
        DuelMenuView view = controller.getView();
        Table ownTable = controller.getBoard().getPlayerTable();
        Deck deck = ownTable.getDeck();

        DataManager dataManager = DataManager.getInstance();

        String message = "enter a card number to add to your hand:";

        int count = 0;
        int position;


        ArrayList<Card> fieldSpellCards = new ArrayList<>();

        if (!canBeRun(controller)) {
            return;
        }

        for (String id :
                deck.getMainDeckCardIds()) {
            Card card = dataManager.getCardById(id);
            if (card.getType().equals(CardType.FIELD)) {
                fieldSpellCards.add(card);
                count++;
            }
        }

        view.showCards(fieldSpellCards);

        while (true) {
            ArrayList<Integer> numbers = view.getNumbers(1, message);
            if (numbers == null) {
                view.printActionCanceled();
                return;
            }
            position = numbers.get(0);
            if (position > count || position < 1) {
                message = "position should be between 1 and " + count;
            } else
                break;
        }

        ownTable.getHand().add(fieldSpellCards.get(position));

        deck.shuffleMainDeck();

    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return false;
    }
}
