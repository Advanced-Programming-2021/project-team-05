package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import model.card.Card;

public class DestroyOpponentAttackerAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        Table playerTable = controller.getBoard().getPlayerTable();
        int position = controller.getSelectedCardAddress().getPosition();
        if (playerTable.getMonster(position) != null) {
            playerTable.moveMonsterToGraveyard(position);
        }
    }
}
