package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Board;
import model.card.Monster;

public class ForestDisableAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        Board board = controller.getBoard();
        for (Monster monster : board.getSpelledMonsters()) {
            monster.decreaseAttack(200);
            monster.decreaseDefense(200);
        }
        board.clearSpelledMonsters();
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
