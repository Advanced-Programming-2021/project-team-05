package model.board;

import model.card.Card;

public abstract class Cell {
    protected Card card;
    protected CardState state;
    protected boolean isNewlyAdded;


    public Cell(Card card, CardState cardState) {
        this.reset();
        this.setCard(card);
        this.setState(cardState);
    }


    public abstract void reset();


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


    public boolean isNewlyAdded() {
        return this.isNewlyAdded;
    }

    public void setNewlyAdded(boolean isNewlyAdded) {
        this.isNewlyAdded = isNewlyAdded;
    }
}
