package model.effect.action;

import control.controller.DuelMenuController;
import control.message.DuelMenuMessage;
import model.board.Table;

public class DrawTwoCards implements Action{
    @Override
    public void run(DuelMenuController controller) {
        if (!canBeRun(controller)) {
            return;
        }
        Table table = controller.getBoard().getPlayerTable();
        table.drawCard();
        table.drawCard();
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return controller.getBoard().getPlayerTable().getHand().size() <= 4;
    }
}
