package model.effect.action;

import control.controller.DuelMenuController;
import control.message.DuelMenuMessage;
import model.board.Board;
import model.board.CardState;
import model.board.Table;
import model.card.Card;
import model.card.Monster;

import java.util.ArrayList;

public class ReturnOneCardFromGraveyardAction implements Action {
    @Override
    public void run(DuelMenuController controller) {
        Board board = controller.getBoard();
        if (!checkPreparations(controller)) {
            controller.getView().printActivateEffectMessage(DuelMenuMessage.PREPARATIONS_NOT_DONE_YET);
            return;
        }
        String getValueMessage = "your graveyard or opponent? (myself/opponent)";
        String getValueInvalidMessage = "invalid input";
        String isForOpponentString = controller.getView()
                .getOneOfValues("myself", "opponent", getValueMessage, getValueInvalidMessage);
        if (isForOpponentString == null) {
            controller.getView().printActionCanceled();
            return;
        }

        Table targetTable = "opponent".equals(isForOpponentString) ? board.getOpponentTable() : board.getPlayerTable();
        int position;
        Card targetCard;
        String message = "enter card position in graveyard:";
        while (true) {
            ArrayList<Integer> positions = controller.getView().getNumbers(1, message);
            if (positions == null) {
                controller.getView().printActionCanceled();
                return;
            }
            position = positions.get(0);
            if (position < 1 || position > targetTable.getGraveyard().size()) {
                message = "position should be between 1 and " + targetTable.getGraveyard().size();
                continue;
            }
            targetCard = targetTable.getGraveyard().get(position - 1);
            if (!(targetCard instanceof Monster)) {
                message = "target card should be monster";
                continue;
            }
            break;
        }

        targetTable.getGraveyard().remove(position - 1);
        targetTable.addMonster((Monster) targetCard, CardState.VERTICAL_UP);
        controller.getView().printActivateEffectMessage(DuelMenuMessage.SPELL_ACTIVATED);
    }

    private boolean checkPreparations(DuelMenuController controller) {
        int monstersCountInGraveyards = 0;
        for (Card card : controller.getBoard().getPlayerTable().getGraveyard()) {
            if (card instanceof Monster) {
                monstersCountInGraveyards++;
            }
        }
        for (Card card : controller.getBoard().getOpponentTable().getGraveyard()) {
            if (card instanceof Monster) {
                monstersCountInGraveyards++;
            }
        }
        if (monstersCountInGraveyards == 0) {
            return false;
        }
        return !controller.getBoard().getPlayerTable().isMonsterZoneFull();
    }
}
