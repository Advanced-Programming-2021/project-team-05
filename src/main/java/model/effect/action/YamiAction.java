package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import model.card.Monster;
import model.template.property.MonsterType;
import view.DuelMenuView;

import java.util.ArrayList;

public class YamiAction implements Action {
    @Override
    public void run(DuelMenuController controller) {

        Table targetTable = controller.getBoard().getOpponentTable();
        for (int i = 1; i <= 5; i++) {
            Monster monster = targetTable.getMonster(i);
            if (monster.getMonsterType().equals(MonsterType.FIEND) || )
        }
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
