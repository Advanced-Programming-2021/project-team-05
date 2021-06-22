package model.effect.action;

import control.controller.DuelMenuController;
import model.board.Table;
import view.DuelMenuView;

import java.util.ArrayList;

public class DestroyOpponentOneSpellOrTrapAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (!canBeRun(controller)) return;

        DuelMenuView view = controller.getView();
        Table targetTable = controller.getBoard().getOpponentTable();
        int number;
        String message = "enter spell or trap position to destroy:";
        while (true) {
            ArrayList<Integer> numbers = view.getNumbers(1, message);
            if (numbers == null) {
                view.printActionCanceled();
                return;
            }
            number = numbers.get(0);
            if (number < 1 || number > 5) message = "number should be between 1 and 5";
            else if (targetTable.getSpellOrTrap(number) == null) message = "no spell or trap in this position";
            else break;
        }
        targetTable.moveSpellOrTrapToGraveyard(number);
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
        return controller.getBoard().getOpponentTable().getSpellTrapCardsCount() > 0;
    }
}
