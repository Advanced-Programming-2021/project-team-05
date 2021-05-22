package model.board;

import model.User;
import model.card.Card;
import model.card.Monster;

public class Board {
    private Table playerTable;
    private Table opponentTable;
    private Table winnerTable;
    private Table loserTable;
    private int attackerMonsterPosition;
    private boolean noDamageToAnyPlayer;


    public Board(User player, User opponent) {
        this.playerTable = new Table(player);
        this.playerTable.initializeHand();
        this.opponentTable = new Table(opponent);
        this.opponentTable.initializeHand();
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


    public int getAttackerMonster() {
        return attackerMonsterPosition;
    }

    public void setAttackerMonster(int attackerMonsterPosition) {
        this.attackerMonsterPosition = attackerMonsterPosition;
    }


    public boolean isNoDamageToAnyPlayer() {
        return noDamageToAnyPlayer;
    }

    public void setNoDamageToAnyPlayer(boolean noDamageToAnyPlayer) {
        this.noDamageToAnyPlayer = noDamageToAnyPlayer;
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
