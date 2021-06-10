package model.effect.action;

import control.DataManager;
import control.controller.DuelMenuController;
import model.Deck;
import model.board.Table;
import model.card.Card;
import model.template.property.CardType;
import view.DuelMenuView;

import java.util.ArrayList;

public class AddFieldSpellFromDeckToHandAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (!canBeRun(controller)) {
            return;
        }

        DataManager dataManager = DataManager.getInstance();
        DuelMenuView view = controller.getView();
        Table ownTable = controller.getBoard().getPlayerTable();
        Deck deck = ownTable.getDeck();

        int fieldSpellsCount = 0;
        ArrayList<Card> fieldSpells = new ArrayList<>();
        for (String id : deck.getMainDeckCardIds()) {
            Card card = dataManager.getCardById(id);
            if (card.getType().equals(CardType.FIELD)) {
                fieldSpells.add(card);
                fieldSpellsCount++;
            }
        }

        view.showCards(fieldSpells, "Field Spells:");
        String message = "enter a card number to add to your hand:";
        int position;
        while (true) {
            ArrayList<Integer> numbers = view.getNumbers(1, message);
            if (numbers == null) {
                view.printActionCanceled();
                return;
            }
            position = numbers.get(0);
            if (position > fieldSpellsCount || position < 1) {
                message = "position should be between 1 and " + fieldSpellsCount;
            } else {
                break;
            }
        }

        ownTable.addCardToHand(fieldSpells.get(position));
        deck.shuffleMainDeck();
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        DataManager dataManager = DataManager.getInstance();
        Table table = controller.getBoard().getPlayerTable();
        int fieldSpellsCount = 0;
        for (String id : table.getDeck().getMainDeckCardIds()) {
            Card card = dataManager.getCardById(id);
            if (card.getType().equals(CardType.FIELD)) {
                fieldSpellsCount++;
            }
        }
        return fieldSpellsCount > 0 && !table.isHandFull();
    }
}
