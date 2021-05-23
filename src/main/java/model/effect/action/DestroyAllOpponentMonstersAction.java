package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import view.DuelMenuView;

public class DestroyAllOpponentMonstersAction implements Action {

    public void run(DuelMenuController controller) {
        DuelMenuView view = controller.getView();
        Table targetTable = controller.getBoard().getOpponentTable();
        if (targetTable.getMonsterCardsCount() == 0) {
            return;
        }

        for (int i = 1; i <= 5; i++) {
            targetTable.moveMonsterToGraveyard(i);
        }
    }

}
