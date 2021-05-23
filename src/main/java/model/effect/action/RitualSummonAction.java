package model.effect.action;

import control.controller.DuelMenuController;
import control.message.DuelMenuMessage;
import model.board.Table;
import model.card.Card;
import model.card.Monster;
import model.template.property.CardType;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class RitualSummonAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        Table table = controller.getBoard().getPlayerTable();
        ArrayList<Integer> ritualMonsterLevels = new ArrayList<>();
        for (Card card : table.getHand()) {
            if (card.getType() == CardType.RITUAL && card instanceof Monster) {
                ritualMonsterLevels.add(((Monster) card).getLevel());
            }
        }

        if (ritualMonsterLevels.size() == 0) {
            controller.getView().printRitualSummonMessage(DuelMenuMessage.NO_WAY_TO_RITUAL_SUMMON);
            return;
        }
        if (!canRitualSummon(table, ritualMonsterLevels)) {
            controller.getView().printRitualSummonMessage(DuelMenuMessage.NO_WAY_TO_RITUAL_SUMMON);
            return;
        }
        controller.setRitualSummonSpellAddress(controller.getSelectedCardAddress());
    }

    private boolean canRitualSummon(Table table, ArrayList<Integer> ritualMonsterLevels) {
        for (int i = 1; i <= 5; i++) {
            Monster monster1 = table.getMonster(i);
            if (monster1 == null) {
                continue;
            }
            for (int j = i + 1; j <= 5; j++) {
                Monster monster2 = table.getMonster(j);
                if (monster2 == null) {
                    continue;
                }
                for (int k = j + 1; k <= 5; k++) {
                    Monster monster3 = table.getMonster(k);
                    if (monster3 == null) {
                        continue;
                    }
                    if (ritualMonsterLevels.contains(monster1.getLevel() + monster2.getLevel() + monster3.getLevel())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
