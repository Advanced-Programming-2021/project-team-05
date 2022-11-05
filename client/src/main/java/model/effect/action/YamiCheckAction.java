package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Board;
import model.board.Table;
import model.template.property.MonsterType;

public class YamiCheckAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        Board board = controller.getBoard();
        Table playerTable = controller.getBoard().getPlayerTable();
        Table opponentTable = controller.getBoard().getOpponentTable();
        board.getSpelledMonsters().removeIf(monster -> {
            if (playerTable.hasNotMonster(monster) && opponentTable.hasNotMonster(monster)) {
                MonsterType type = monster.getMonsterType();
                if (type == MonsterType.FIEND || type == MonsterType.SPELL_CASTER) {
                    monster.decreaseAttack(200);
                    monster.decreaseDefense(200);
                }
                if (type == MonsterType.FAIRY) {
                    monster.increaseAttack(200);
                    monster.increaseDefense(200);
                }
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
