package model.board;

import model.card.Card;

public class Cell {
    private Card card;
    private CardState state;
    boolean doesPositionChanged;
    boolean isNewlyAdded;
    boolean didAttack;


    public Cell(Card card, CardState cardState) {
        this.resetCell();
        this.setCard(card);
        this.setState(cardState);
    }


    public void resetCell() {
        this.setCard(null);
        this.setState(null);
        this.setDoesPositionChanged(false);
        this.setDidAttack(false);
        this.setNewlyAdded(false);
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


    public boolean doesPositionChanged() {
        return this.doesPositionChanged;
    }

    public void setDoesPositionChanged(boolean doesPositionChanged) {
        this.doesPositionChanged = doesPositionChanged;
    }


    public boolean isNewlyAdded() {
        return this.isNewlyAdded;
    }

    public void setNewlyAdded(boolean isNewlyAdded) {
        this.isNewlyAdded = isNewlyAdded;
    }


    public boolean didAttack() {
        return this.didAttack;
    }

    public void setDidAttack(boolean didAttack) {
        this.didAttack = didAttack;
    }
}
