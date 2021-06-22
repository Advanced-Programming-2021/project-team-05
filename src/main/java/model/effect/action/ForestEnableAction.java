package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Board;
import model.board.Table;
import model.card.Monster;
import model.template.property.MonsterType;

public class ForestEnableAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        Board board = controller.getBoard();
        Table table;
        for (int i = 1; i <= 2; i++) {
            if (i == 1) table = controller.getBoard().getOpponentTable();
            else table = controller.getBoard().getPlayerTable();
            for (int j = 1; j <= 5; j++) {
                Monster monster = table.getMonster(j);
                if (monster == null) continue;
                if (!board.isMonsterSpelled(monster)) {
                    MonsterType type = monster.getMonsterType();
                    if (type == MonsterType.INSECT || type == MonsterType.BEAST || type == MonsterType.BEAST_WARRIOR) {
                        monster.increaseAttack(200);
                        monster.increaseDefense(200);
                        board.addSpelledMonster(monster);
                    }
                }
            }
        }
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
