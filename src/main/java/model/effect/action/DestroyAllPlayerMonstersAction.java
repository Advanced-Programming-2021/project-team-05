package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;

public class DestroyAllPlayerMonstersAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        Table targetTable = controller.getBoard().getPlayerTable();
        for (int i = 1; i <= 5; i++)
            if (targetTable.getMonster(i) != null) controller.moveMonsterToGraveyard(targetTable, i);
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
