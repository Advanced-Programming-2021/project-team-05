package model.effect.action;

import control.controller.DuelMenuController;

public class NoDamageAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        controller.getBoard().setPlayersImmune(true);
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
