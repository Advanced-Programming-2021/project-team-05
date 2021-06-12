package model.effect.action;

import control.controller.DuelMenuController;
import control.controller.Phase;

public class EndBattlePhaseAction implements Action {
    @Override
    public void run(DuelMenuController controller) {
        if (!canBeRun(controller)) {
            return;
        }
        controller.goToNextPhase();
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return controller.getPhase() == Phase.BATTLE;
    }
}
