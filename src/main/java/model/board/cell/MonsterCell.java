package model.board.cell;

import model.board.CardState;
import model.card.Monster;

public class MonsterCell extends Cell{
    protected boolean doesPositionChanged;
    protected boolean didAttack;


    public MonsterCell(Monster monster, CardState cardState) {
        super(monster, cardState);
    }


    public boolean doesPositionChanged() {
        return this.doesPositionChanged;
    }

    public void setDoesPositionChanged(boolean doesPositionChanged) {
        this.doesPositionChanged = doesPositionChanged;
    }


    public boolean didAttack() {
        return this.didAttack;
    }

    public void setDidAttack(boolean didAttack) {
        this.didAttack = didAttack;
    }


    @Override
    public void reset() {
        this.setCard(null);
        this.setState(null);
        this.setDoesPositionChanged(false);
        this.setDidAttack(false);
        this.setNewlyAdded(false);
    }
}
