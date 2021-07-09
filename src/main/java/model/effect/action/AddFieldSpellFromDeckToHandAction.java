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
        if (!canBeRun(controller)) return;

        DataManager dataManager = DataManager.getInstance();
        DuelMenuView view = controller.getView();
        Table ownTable = controller.getBoard().getPlayerTable();
        Deck deck = ownTable.getDeck();

        ArrayList<Card> fieldSpells = new ArrayList<>();
        for (String id : deck.getMainDeckCardIds()) {
            Card card = dataManager.getCardById(id);
            if (card.getType().equals(CardType.FIELD)) fieldSpells.add(card);
        }

        ArrayList<Integer> positions = view.getCardsPosition(fieldSpells, 1, "Select field spell to move from deck to hand");
        if (positions.size() == 0) return;

        ownTable.addCardToHand(fieldSpells.get(positions.get(0)));
        deck.shuffleMainDeck();
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        DataManager dataManager = DataManager.getInstance();
        Table table = controller.getBoard().getPlayerTable();
        int fieldSpellsCount = 0;
        for (String id : table.getDeck().getMainDeckCardIds()) {
            Card card = dataManager.getCardById(id);
            if (card.getType().equals(CardType.FIELD)) fieldSpellsCount++;
        }
        return fieldSpellsCount > 0 && !table.isHandFull();
    }
}
