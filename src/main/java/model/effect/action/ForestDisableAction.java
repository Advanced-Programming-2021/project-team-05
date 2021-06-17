package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Board;
import model.card.Monster;
import model.template.property.MonsterType;

public class ForestDisableAction implements Action{
    @Override
    public void run(DuelMenuController controller) {

        for (Monster monster :
                Board.spelledCards) {
            if (monster.getMonsterType().equals(MonsterType.INSECT) || monster.getMonsterType().equals(MonsterType.BEAST) || monster.getMonsterType().equals(MonsterType.BEAST_WARRIOR)) {
                monster.setAttack(monster.getAttack() - 200);
                monster.setDefence(monster.getDefence() - 200);
            }

        }

        Board.spelledCards.clear();

    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
