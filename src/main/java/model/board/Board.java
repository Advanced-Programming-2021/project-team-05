package model.board;

import model.User;

public class Board {
    private Table playerTable;
    private Table opponentTable;


    public Board(User player, User opponent) {
        this.playerTable = new Table(player);
        this.opponentTable = new Table(opponent);
    }


    public Table getPlayerTable() {
        return this.playerTable;
    }

    public Table getOpponentTable() {
        return this.opponentTable;
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
