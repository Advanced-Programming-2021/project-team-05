package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;

public class DestroyAttackerInOpponentTableAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (canBeRun(controller)) {
            Table opponentTable = controller.getBoard().getOpponentTable();
            int position = controller.getSelectedCardAddress().getPosition();
            controller.moveMonsterToGraveyard(opponentTable, position);
        }
    }

    public boolean canBeRun(DuelMenuController controller) {
        Table table = controller.getBoard().getOpponentTable();
        int position = controller.getSelectedCardAddress().getPosition();
        return table.getMonster(position) != null;
    }
}
