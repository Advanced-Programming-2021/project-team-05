package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import model.card.Card;
import model.card.Monster;
import model.template.property.CardType;

import java.util.ArrayList;

public class RitualSummonAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (canBeRun(controller)) {
            controller.setRitualSummonSpell(controller.getSelectedCard());
            controller.setRitualSummonSpellAddress(controller.getSelectedCardAddress());
        }
    }

    public boolean canBeRun(DuelMenuController controller) {
        Table table = controller.getBoard().getPlayerTable();
        ArrayList<Integer> ritualMonsterLevels = new ArrayList<>();
        for (Card card : table.getHand()) {
            if (card.getType() == CardType.RITUAL && card instanceof Monster)
                ritualMonsterLevels.add(((Monster) card).getLevel());
        }
        return ritualMonsterLevels.size() != 0 && doesMonstersMatchRitualLevels(table, ritualMonsterLevels);
    }

    private boolean doesMonstersMatchRitualLevels(Table table, ArrayList<Integer> ritualMonsterLevels) {
        for (int i = 1; i <= 5; i++) {
            Monster monster1 = table.getMonster(i);
            if (monster1 == null) continue;
            for (int j = i + 1; j <= 5; j++) {
                Monster monster2 = table.getMonster(j);
                if (monster2 == null) continue;
                for (int k = j + 1; k <= 5; k++) {
                    Monster monster3 = table.getMonster(k);
                    if (monster3 == null) continue;
                    for (Integer ritualMonsterLevel : ritualMonsterLevels) {
                        if (monster1.getLevel() + monster2.getLevel() + monster3.getLevel() >= ritualMonsterLevel)
                            return true;
                    }
                }
            }
        }
        return false;
    }
}
