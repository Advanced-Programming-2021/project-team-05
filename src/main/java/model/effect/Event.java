package model.effect;

import control.controller.DuelMenuController;
import model.board.Board;
import model.card.Card;

public enum Event {
    YOU_FLIP_SUMMONED_MANUALLY,
    YOU_DESTROYED_BY_ATTACK,
    YOU_DESTROYED_WHILE_ATTACKING,
    DECLARE_ATTACK,
    MONSTER_SUMMONED,
    MONSTER_WITH_1000_ATTACK_SUMMONED,
    ACTIVATE_EFFECT,
    DISABLE_FIELD_SPELL,
    CHECK_FIELD_SPELL,
    QUICK_ACTIVATE;


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
