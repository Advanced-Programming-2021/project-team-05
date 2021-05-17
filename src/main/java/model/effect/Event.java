package model.effect;

import control.controller.DuelMenuController;
import model.board.Board;
import model.card.Card;

public enum Event {
    CARD_FLIP_SUMMONED,
    ACTIVATE_EFFECT;


    public void trigger(DuelMenuController controller) {
        for (int i = 1; i <= 5; i++) {
            Board board = controller.getBoard();
            this.notifyCard(board.getPlayerTable().getMonster(i), controller);
            this.notifyCard(board.getPlayerTable().getSpellOrTrap(i), controller);
            this.notifyCard(board.getOpponentTable().getMonster(i), controller);
            this.notifyCard(board.getOpponentTable().getMonster(i), controller);
        }
    }

    private void notifyCard(Card card, DuelMenuController controller) {
        if (card != null) {
            card.runActions(this, controller);
        }
    }
}
