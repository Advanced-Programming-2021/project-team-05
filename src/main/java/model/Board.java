package model;

public class Board {
    private Table playerTable;
    private Table opponentTable;


    public Board(User user1, User user2) {
        this.playerTable = new Table(user1);
        this.opponentTable = new Table(user2);
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
}
