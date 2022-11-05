package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import model.board.cell.Cell;
import model.card.Card;
import view.DuelMenuView;

import java.util.ArrayList;

public class DestroyOpponentOneSpellOrTrapAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (!canBeRun(controller)) return;

        DuelMenuView view = controller.getView();
        Table targetTable = controller.getBoard().getOpponentTable();

        ArrayList<Card> cards = new ArrayList<>();
        ArrayList<Boolean> showCards = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Cell cell = targetTable.getSpellOrTrapCell(i);
            if (cell.getCard() != null) {
                cards.add(cell.getCard());
                showCards.add(!cell.getState().isDown());
            }
        }

        ArrayList<Integer> positions = view.getCardsPosition(cards, showCards, 1, "Select spell or trap to destroy");
        if (positions.size() == 0) return;

        targetTable.moveSpellOrTrapToGraveyard(targetTable.getSpellOrTrapPosition(cards.get(positions.get(0))));
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return controller.getBoard().getOpponentTable().getSpellTrapCardsCount() > 0;
    }
}
