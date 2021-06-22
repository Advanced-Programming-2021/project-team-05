package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import view.DuelMenuView;

import java.util.ArrayList;


public class DestroyOpponentOneMonsterAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (!canBeRun(controller)) return;

        DuelMenuView view = controller.getView();
        Table targetTable = controller.getBoard().getOpponentTable();
        int position;
        String message = "enter monster position to destroy:";
        while (true) {
            ArrayList<Integer> numbers = view.getNumbers(1, message);
            if (numbers == null) {
                view.printActionCanceled();
                return;
            }
            position = numbers.get(0);
            if (position < 1 || position > 5) message = "position should be between 1 and 5";
            else if (targetTable.getMonster(position) == null) message = "no monster in this position";
            else break;
        }
        controller.moveMonsterToGraveyard(targetTable, position);
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return controller.getBoard().getOpponentTable().getMonsterCardsCount() > 0;
    }
}
