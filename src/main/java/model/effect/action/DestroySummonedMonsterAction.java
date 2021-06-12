package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;

public class DestroySummonedMonsterAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (canBeRun(controller)) {
            Table targetTable = controller.getBoard().getOpponentTable();
            targetTable.removeMonster(controller.getSelectedCardAddress().getPosition());
        }
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return controller.getSelectedCard() != null;
    }
}
