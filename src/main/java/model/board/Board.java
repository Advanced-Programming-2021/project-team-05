package model.board;

import model.User;
import model.board.cell.SpellTrapCell;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;

import java.util.ArrayList;

public class Board {
    private Table playerTable;
    private Table opponentTable;
    private Table winnerTable;
    private Table loserTable;
    private boolean arePlayersImmune;

    public ArrayList<Monster> spelledMonsters;

    {
        spelledMonsters = new ArrayList<>();
    }


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


    public boolean arePlayersImmune() {
        return arePlayersImmune;
    }

    public void setPlayersImmune(boolean arePlayersImmune) {
        this.arePlayersImmune = arePlayersImmune;
    }


    public final Spell getFieldSpell() {
        if (playerTable.getFieldSpell() != null) {
            return playerTable.getFieldSpell();
        }
        return opponentTable.getFieldSpell();
    }

    public final void setFieldSpell(Spell spell, CardState state) {
        opponentTable.getFieldSpellCell().clear();
        playerTable.setFieldSpell(spell, state);
    }


    public final ArrayList<Monster> getSpelledMonsters() {
        return spelledMonsters;
    }

    public final void addSpelledMonster(Monster monster) {
        if (!spelledMonsters.contains(monster)) {
            spelledMonsters.add(monster);
        }
    }

    public final void removeSpelledMonster(Monster monster) {
        spelledMonsters.remove(monster);
    }

    public final boolean isMonsterSpelled(Monster monster) {
        for (Monster spelledMonster : spelledMonsters) {
            if (monster.equals(spelledMonster)) {
                return true;
            }
        }
        return false;
    }


    public final void swapTables() {
        Table temp = this.playerTable;
        this.playerTable = this.opponentTable;
        this.opponentTable = temp;
    }


    @Override
    public String toString() {
        return "=========================" + "\r\n" +
                opponentTable.toString(true) + "\r\n" +
                "-------------------------" + "\r\n" +
                playerTable.toString(false) + "\r\n" +
                "=========================";
    }
}
