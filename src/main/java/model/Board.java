package model;

public class Board {
    private Table playerTable;
    private Table opponentTable;


    public Board(User user1, User user2) {
        Table table1 = new Table(user1);
        Table table2 = new Table(user2);
        this.playerTable = table1;
        this.opponentTable = table2;
    }

    public Table getPlayerTable() {
        return playerTable;
    }

    public Table getOpponentTable() {
        return opponentTable;
    }

    public final void swapTables() {

        Table temp = playerTable;
        playerTable = opponentTable;
        opponentTable = temp;
    }
}
