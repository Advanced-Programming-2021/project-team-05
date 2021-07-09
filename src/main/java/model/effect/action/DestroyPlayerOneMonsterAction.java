package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import model.board.cell.MonsterCell;
import model.card.Card;
import model.card.Monster;
import view.DuelMenuView;

import java.util.ArrayList;

public class DestroyPlayerOneMonsterAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (!canBeRun(controller)) return;

        DuelMenuView view = controller.getView();
        Table targetTable = controller.getBoard().getOpponentTable();
        controller.quickChangeTurn(false);

        ArrayList<Card> cards = new ArrayList<>();
        ArrayList<Boolean> showCards = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            MonsterCell monsterCell = targetTable.getMonsterCell(i);
            if (monsterCell.getCard() != null) {
                cards.add(monsterCell.getCard());
                showCards.add(!monsterCell.getState().isDown());
            }
        }

        ArrayList<Integer> positions = view.getCardsPosition(cards, showCards, 1, "Select monster to destroy");
        if (positions.size() == 0) return;

        controller.moveMonsterToGraveyard(targetTable, targetTable.getMonsterPosition((Monster) cards.get(positions.get(0))));
        controller.quickChangeTurn(false);
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return controller.getBoard().getPlayerTable().getMonsterCardsCount() > 0;
    }
}
