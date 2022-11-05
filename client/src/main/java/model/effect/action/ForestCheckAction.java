package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Board;
import model.board.Table;

public class ForestCheckAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        Board board = controller.getBoard();
        Table playerTable = controller.getBoard().getPlayerTable();
        Table opponentTable = controller.getBoard().getOpponentTable();
        board.getSpelledMonsters().removeIf(monster -> {
            if (playerTable.hasNotMonster(monster) && opponentTable.hasNotMonster(monster)) {
                monster.decreaseAttack(200);
                monster.decreaseDefense(200);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
