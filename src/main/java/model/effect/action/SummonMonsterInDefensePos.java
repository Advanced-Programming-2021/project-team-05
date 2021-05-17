package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;

public class SummonMonsterInDefensePos implements Action {
    @Override
    public void run(DuelMenuController controller) {
        Table table = controller.getBoard().getPlayerTable();

        boolean canSummon = canSummon(table);

        if (table.getHand().size() == 0) {
            canSummon = false;
        }

        controller.setSpecialSummon(canSummon);
    }

    boolean canSummon(Table table) {
        for (int i = 0; i < 5; i++) {
            if (table.getMonster(i) == null) {
                return true;
            }
        }
        return false;
    }
}
