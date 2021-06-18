package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;

public class DestroyAttackedCardAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (canBeRun(controller)) {
            Table opponentTable = controller.getBoard().getOpponentTable();
            int position = controller.getAttackedCardPosition();
            controller.moveMonsterToGraveyard(opponentTable, position);
        }
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        Integer attackedCardPosition = controller.getAttackedCardPosition();
        if (attackedCardPosition == null) {
            return false;
        }
        Table opponentTable = controller.getBoard().getOpponentTable();
        return opponentTable.getMonster(attackedCardPosition) != null;
    }
}
