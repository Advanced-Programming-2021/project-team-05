package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import model.card.Card;

public class DestroyOpponentAttackerMonsterAction implements Action {

    @Override
    public void run(DuelMenuController controller) {

        Table playerTable = controller.getBoard().getPlayerTable();

        playerTable.moveMonsterToGraveyard(controller.getSelectedCardAddress().getPosition());
    }
}
