package model.board;

import model.card.Card;

public class SpellTrapCell extends Cell {
    private boolean isEffectActivated;


    public SpellTrapCell(Card card, CardState cardState) {
        super(card, cardState);
    }


    public boolean isEffectActivated() {
        return this.isEffectActivated;
    }

    public void setEffectActivated(boolean isEffectActivated) {
        this.isEffectActivated = isEffectActivated;
    }


    @Override
    public void reset() {
        this.setCard(null);
        this.setState(null);
        this.setNewlyAdded(false);
        this.setEffectActivated(false);
    }
}
