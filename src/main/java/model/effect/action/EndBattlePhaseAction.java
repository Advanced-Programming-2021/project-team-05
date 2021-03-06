package model.effect.action;

import control.Phase;
import control.controller.DuelMenuController;

public class EndBattlePhaseAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (canBeRun(controller)) controller.goToNextPhase(false);
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return controller.getPhase() == Phase.BATTLE;
    }
}
