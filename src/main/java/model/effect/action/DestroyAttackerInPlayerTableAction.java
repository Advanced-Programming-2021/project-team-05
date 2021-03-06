package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;

public class DestroyAttackerInPlayerTableAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (canBeRun(controller)) {
            Table playerTable = controller.getBoard().getPlayerTable();
            int position = controller.getSelectedCardAddress().getPosition();
            controller.moveMonsterToGraveyard(playerTable, position);
        }
    }

    public boolean canBeRun(DuelMenuController controller) {
        Table table = controller.getBoard().getPlayerTable();
        int position = controller.getSelectedCardAddress().getPosition();
        return table.getMonster(position) != null;
    }
}
