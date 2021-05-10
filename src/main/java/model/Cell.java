package model;

import model.card.Card;

public class Cell {
    private Card card;
    private CardState state;
    boolean isPositionChanged;


    public Cell(Card card, CardState cardState) {
        this.setCard(card);
        this.setState(cardState);
        this.setPositionChanged(false);
    }


    public void resetCell() {
        this.setCard(null);
        this.setState(null);
        this.setPositionChanged(false);
    }


    public Card getCard() {
        return this.card;
    }

    public void setCard(Card card) {
        this.card = card;
    }


    public CardState getState() {
        return this.state;
    }

    public void setState(CardState state) {
        this.state = state;
    }


    public boolean isPositionChanged() {
        return this.isPositionChanged;
    }

    public void setPositionChanged(boolean positionChanged) {
        this.isPositionChanged = positionChanged;
    }
}
