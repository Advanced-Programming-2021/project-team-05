package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import model.card.Card;

public class NoDamage implements Action {
    @Override
    public void run(DuelMenuController controller) {
        controller.getBoard().setNoDamageToAnyPlayer(true);
    }
}
