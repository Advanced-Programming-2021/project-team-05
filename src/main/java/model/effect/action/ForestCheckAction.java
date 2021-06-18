package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Board;
import model.board.Table;
import model.card.Monster;

public class ForestCheckAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        Board board = controller.getBoard();
        Table playerTable = controller.getBoard().getPlayerTable();
        Table opponentTable = controller.getBoard().getOpponentTable();
        for (Monster monster : board.getSpelledMonsters()) {
            if (!playerTable.hasMonster(monster) && !opponentTable.hasMonster(monster)) {
                monster.decreaseAttack(200);
                monster.decreaseDefense(200);
                board.removeSpelledMonster(monster);
            }
        }
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}