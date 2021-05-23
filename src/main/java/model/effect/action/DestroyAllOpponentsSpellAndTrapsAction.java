package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import view.DuelMenuView;

public class DestroyAllOpponentsSpellAndTrapsAction implements Action{

    public void run(DuelMenuController controller) {
        DuelMenuView view = controller.getView();
        Table targetTable = controller.getBoard().getOpponentTable();

        for (int i = 1; i <= 5; i++) {
            targetTable.moveSpellOrTrapToGraveyard(i);
        }
    }
}
