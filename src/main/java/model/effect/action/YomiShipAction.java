package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import model.card.Card;

public class YomiShipAction implements Action {

    @Override
    public void run(DuelMenuController controller) {

        Card attackerMonster = controller.getBoard().getAttackerMonster();
        Table opponentTable = controller.getBoard().getOpponentTable();

        opponentTable.addCardToGraveyard(attackerMonster);
    }
}
