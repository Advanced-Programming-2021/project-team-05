package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Board;
import model.board.Table;
import model.card.Monster;
import model.template.property.MonsterType;

public class ForestEnableAction implements Action{
    @Override
    public void run(DuelMenuController controller) {
        Table table;

        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= 5; j++) {
                if (i == 1) {
                    table = controller.getBoard().getOpponentTable();
                } else {
                    table = controller.getBoard().getPlayerTable();
                }
                Monster monster = table.getMonster(j);
                if (!cardIsSpelled(monster)) {
                    if (monster.getMonsterType().equals(MonsterType.INSECT) || monster.getMonsterType().equals(MonsterType.BEAST) || monster.getMonsterType().equals(MonsterType.BEAST_WARRIOR)) {
                        monster.setAttack(monster.getAttack() + 200);
                        monster.setDefence(monster.getDefence() + 200);
                    }
                    Board.spelledCards.add(monster);
                }

            }

        }

    }

    public boolean cardIsSpelled(Monster card) {
        for (Monster card1 :
                Board.spelledCards) {
            if (card == card1)
                return true;
        }
        return false;
    }


    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
