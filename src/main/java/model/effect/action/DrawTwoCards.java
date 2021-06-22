package model.effect.action;

import control.controller.DuelMenuController;
import model.board.CardAddress;
import model.board.CardAddressZone;
import model.board.Table;

public class DrawTwoCards implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (!canBeRun(controller)) return;
        Table table = controller.getBoard().getPlayerTable();
        controller.getView().showDrawMessage(table.drawCard());
        controller.getView().showDrawMessage(table.drawCard());
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        CardAddress address = controller.getSelectedCardAddress();
        if (address == null) return false;
        int maxSize;
        if (controller.getSelectedCardAddress().getZone() == CardAddressZone.HAND) maxSize = 5;
        else maxSize = 4;
        return controller.getBoard().getPlayerTable().getHand().size() <= maxSize;
    }
}
