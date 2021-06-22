package model.effect.action;

import control.controller.DuelMenuController;

public class NullAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
