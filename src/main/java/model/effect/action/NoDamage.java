package model.effect.action;

import control.controller.DuelMenuController;

public class NoDamage implements Action {

    @Override
    public void run(DuelMenuController controller) {
        controller.getBoard().setPlayersImmune(true);
    }
}
