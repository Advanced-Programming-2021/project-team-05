package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import model.card.Card;

public class DestroyOpponentAttackerMonsterWithNoDamageAction implements Action {
    @Override
    public void run(DuelMenuController controller) {

        Card attackerMonster = controller.getBoard().getAttackerMonster();
        Table opponentTable = controller.getBoard().getOpponentTable();

        opponentTable.addCardToGraveyard(attackerMonster);
        controller.getBoard().setNoDamageToAnyPlayer(true);

    }
}
