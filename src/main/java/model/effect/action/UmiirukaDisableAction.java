package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Board;
import model.card.Monster;

public class UmiirukaDisableAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        Board board = controller.getBoard();
        for (Monster monster : board.getSpelledMonsters()) {
            monster.decreaseAttack(500);
            monster.increaseDefense(400);
            board.removeSpelledMonster(monster);
        }
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
