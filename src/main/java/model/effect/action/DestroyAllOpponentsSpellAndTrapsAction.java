package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import view.DuelMenuView;

public class DestroyAllOpponentsSpellAndTrapsAction implements Action{
    @Override
    public void run(DuelMenuController controller) {
        Table targetTable = controller.getBoard().getOpponentTable();
        for (int i = 1; i <= 5; i++) {
            if (targetTable.getSpellOrTrap(i) != null) {
                targetTable.moveSpellOrTrapToGraveyard(i);
            }
        }
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
