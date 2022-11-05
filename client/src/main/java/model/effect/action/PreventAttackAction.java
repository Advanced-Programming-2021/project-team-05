package model.effect.action;

import control.controller.DuelMenuController;

public class PreventAttackAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        controller.setPreventAttack(true);
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
