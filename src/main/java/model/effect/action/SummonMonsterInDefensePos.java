package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import model.card.Card;
import model.card.Monster;

public class SummonMonsterInDefensePos implements Action {
    @Override
    public void run(DuelMenuController controller) {
        Table table = controller.getBoard().getPlayerTable();
        boolean canSummon = !table.isMonsterZoneFull() && handHasMonster(table);
        controller.setSpecialSummonDefensive(canSummon);
    }

    private boolean handHasMonster(Table table) {
        for (Card card : table.getHand()) {
            if (card instanceof Monster) {
                if (((Monster) card).getLevel() <= 4) {
                    return true;
                }
            }
        }
        return false;
    }
}
