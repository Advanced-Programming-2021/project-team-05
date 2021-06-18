package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import model.card.Monster;

public class ReduceLPByAttackerAttackAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (!canBeRun(controller)) {
            return;
        }
        int damage = ((Monster) controller.getSelectedCard()).getAttack();
        Table attackerTable = controller.getBoard().getPlayerTable();
        Table targetTable = controller.getBoard().getOpponentTable();
        if (controller.checkLifePoint(targetTable, attackerTable, damage)) {
            targetTable.decreaseLifePoint(damage);
        }
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return controller.getSelectedCard() instanceof Monster;
    }
}
