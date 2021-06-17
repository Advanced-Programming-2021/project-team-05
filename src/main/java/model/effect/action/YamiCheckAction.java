package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Board;
import model.board.Table;
import model.card.Monster;
import model.template.property.MonsterType;

public class YamiCheckAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        Board board = controller.getBoard();
        Table playerTable = controller.getBoard().getPlayerTable();
        Table opponentTable = controller.getBoard().getOpponentTable();
        for (Monster monster : board.getSpelledMonsters()) {
            if (!playerTable.hasMonster(monster) && !opponentTable.hasMonster(monster)) {
                MonsterType type = monster.getMonsterType();
                if (type == MonsterType.FIEND || type == MonsterType.SPELL_CASTER) {
                    monster.decreaseAttack(200);
                    monster.decreaseDefense(200);
                }
                if (type == MonsterType.FAIRY) {
                    monster.increaseAttack(200);
                    monster.increaseDefense(200);
                }
                board.removeSpelledMonster(monster);
            }
        }
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
