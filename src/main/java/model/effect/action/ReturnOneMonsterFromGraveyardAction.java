package model.effect.action;

import control.controller.DuelMenuController;
import control.message.DuelMenuMessage;
import model.board.Board;
import model.board.CardState;
import model.board.Table;
import model.card.Card;
import model.card.Monster;

import java.util.ArrayList;

public class ReturnOneMonsterFromGraveyardAction implements Action {

    @Override
    public void run(DuelMenuController controller) {
        if (!canBeRun(controller)) {
            return;
        }
        Board board = controller.getBoard();
        ArrayList<Card> playerGraveyardMonsters = getMonstersFromCards(board.getPlayerTable().getGraveyard());
        ArrayList<Card> opponentGraveyardMonsters = getMonstersFromCards(board.getOpponentTable().getGraveyard());
        ArrayList<Card> allCards = new ArrayList<>();
        allCards.addAll(playerGraveyardMonsters);
        allCards.addAll(opponentGraveyardMonsters);
        controller.getView().showCards(allCards, "Graveyards Monsters");

        int position;
        Table cardTable;
        String message = "enter monster position to return it from graveyard:";
        while (true) {
            ArrayList<Integer> positions = controller.getView().getNumbers(1, message);
            if (positions == null) {
                controller.getView().printActionCanceled();
                return;
            }
            position = positions.get(0);
            if (position < 1 || position > allCards.size()) {
                message = "position should be between 1 and " + allCards.size();
                continue;
            }
            if (position < playerGraveyardMonsters.size()) {
                cardTable = board.getPlayerTable();
            } else {
                cardTable = board.getOpponentTable();
            }
            break;
        }
        Card targetCard = allCards.get(position - 1);
        cardTable.removeCardFromGraveyard(targetCard);
        controller.addMonsterToTable((Monster) targetCard, board.getPlayerTable(), CardState.VERTICAL_UP);
    }

    @Override
    public boolean canBeRun(DuelMenuController controller) {
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


    private ArrayList<Card> getMonstersFromCards(ArrayList<Card> cards) {
        ArrayList<Card> monsters = new ArrayList<>();
        for (Card card : cards) {
            if (card instanceof Monster) {
                monsters.add(card);
            }
        }
        return monsters;
    }
}
