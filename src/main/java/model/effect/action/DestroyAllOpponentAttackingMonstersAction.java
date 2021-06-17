package model.effect.action;

import control.controller.DuelMenuController;
import model.board.CardState;
import model.board.Table;

public class DestroyAllOpponentAttackingMonstersAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        Table targetTable = controller.getBoard().getOpponentTable();
        for (int i = 1; i <= 5; i++) {
            if (targetTable.getMonsterCell(i).getState() == CardState.VERTICAL_UP) {
                controller.moveMonsterToGraveyard(targetTable, i);
            }
        }
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
