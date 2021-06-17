package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Board;
import model.card.Monster;
import model.template.property.MonsterType;

public class UmiirukaDisableAction implements Action {
    @Override
    public void run(DuelMenuController controller) {
        for (Monster monster :
                Board.spelledCards) {

            monster.setAttack(monster.getAttack() - 500);
            monster.setDefence(monster.getDefence() + 400);

        }

        Board.spelledCards.clear();

    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
