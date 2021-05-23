package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import view.DuelMenuView;

public class DestroyAllMonstersOnTableAction implements Action{

    public void run(DuelMenuController controller) {

        Table targetTable = controller.getBoard().getOpponentTable();
        Table ownTable = controller.getBoard().getPlayerTable();

        for (int i = 1; i <= 5; i++) {
            targetTable.moveMonsterToGraveyard(i);
            ownTable.moveMonsterToGraveyard(i);
        }
    }
}
