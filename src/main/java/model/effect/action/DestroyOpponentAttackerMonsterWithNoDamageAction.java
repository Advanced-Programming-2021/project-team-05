package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import model.card.Card;

public class DestroyOpponentAttackerMonsterWithNoDamageAction implements Action {
    @Override
    public void run(DuelMenuController controller) {

        Table opponentTable = controller.getBoard().getOpponentTable();

        opponentTable.moveMonsterToGraveyard(controller.getSelectedCardAddress().getPosition());
        controller.getBoard().setNoDamageToAnyPlayer(true);

    }
}
