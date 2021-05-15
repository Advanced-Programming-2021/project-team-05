package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import view.DuelMenuView;


public class DestroyOpponentOneCard implements Action {

    @Override
    public void run(DuelMenuController controller) {
        DuelMenuView view = controller.getView();
        Table targetTable = controller.getBoard().getOpponentTable();
        if (targetTable.getMonsterCardsCount() == 0) {
            return;
        }

        int number;
        String message = "enter monster position to destroy:";
        while (true) {
            number = view.getNumbers(1, message).get(1);
            if (number < 1 || number > 5) {
                message = "number should be between 1 and 5";
            } else if (targetTable.getMonster(number) == null) {
                message = "no monster in this position";
            } else {
                break;
            }
        }
        targetTable.moveMonsterToGraveyard(number);
    }
}
