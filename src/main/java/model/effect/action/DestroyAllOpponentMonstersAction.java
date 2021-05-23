package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import view.DuelMenuView;

public class DestroyAllOpponentMonstersAction implements Action {
    @Override
    public void run(DuelMenuController controller) {
        Table targetTable = controller.getBoard().getOpponentTable();
        for (int i = 1; i <= 5; i++) {
            targetTable.moveMonsterToGraveyard(i);
        }
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }

}
