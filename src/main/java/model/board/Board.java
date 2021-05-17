package model.board;

import model.User;
import model.card.Card;
import model.card.Monster;

public class Board {
    private Table playerTable;
    private Table opponentTable;
    private Table winnerTable;
    private Table loserTable;
    private Card attackerMonster;


    public Board(User player, User opponent) {
        this.playerTable = new Table(player);
        this.playerTable.initializeHand();
        this.opponentTable = new Table(opponent);
        this.opponentTable.initializeHand();
    }


    public Card getAttackerMonster() {
        return attackerMonster;
    }

    public void setAttackerMonster(Card attackerMonster) {
        this.attackerMonster = attackerMonster;
    }

    public Table getPlayerTable() {
        return this.playerTable;
    }

    public Table getOpponentTable() {
        return this.opponentTable;
    }


    public Table getWinnerTable() {
        return this.winnerTable;
    }

    public void setWinnerTable(Table winnerTable) {
        this.winnerTable = winnerTable;
    }


    public Table getLoserTable() {
        return this.loserTable;
    }

    public void setLoserTable(Table loserTable) {
        this.loserTable = loserTable;
    }


    public final void swapTables() {
        Table temp = this.playerTable;
        this.playerTable = this.opponentTable;
        this.opponentTable = temp;
    }


    @Override
    public String toString() {
        return opponentTable.toString(true) + "\n" +
                "-------------------------" + "\n" +
                playerTable.toString(false);
    }
}
