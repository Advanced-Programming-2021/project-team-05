package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Board;
import model.board.Table;

public class UmiirukaCheckAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        Board board = controller.getBoard();
        Table playerTable = controller.getBoard().getPlayerTable();
        Table opponentTable = controller.getBoard().getOpponentTable();
        board.getSpelledMonsters().removeIf(monster -> {
            if (!playerTable.hasMonster(monster) && !opponentTable.hasMonster(monster)) {
                monster.decreaseAttack(500);
                monster.increaseDefense(400);
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
