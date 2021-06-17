package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Board;
import model.board.Table;
import model.card.Monster;
import model.template.property.MonsterType;

public class ForestDisableOneCard implements Action
{
    @Override
    public void run(DuelMenuController controller) {
        Table targetTable = controller.getBoard().getOpponentTable();
        Table ownTable = controller.getBoard().getPlayerTable();
        for (Monster monster:
                Board.spelledCards) {
            if (isDestroyed(monster , targetTable) && isDestroyed(monster , ownTable)){
                monster.setAttack(monster.getAttack() - 200);
                monster.setDefence(monster.getDefence() - 200);
                Board.spelledCards.remove(monster);
            }
        }
    }
    public boolean isDestroyed(Monster monster , Table table){

        for (int i = 1; i <= 5; i++) {
            if (table.getMonster(i) == monster)
                return false;
        }
        return true;
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return true;
    }
}
