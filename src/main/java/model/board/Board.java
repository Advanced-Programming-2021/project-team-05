package model.board;

import model.User;
import model.board.cell.SpellTrapCell;
import model.card.Monster;
import model.card.Spell;

import java.util.ArrayList;

public class Board {
    public final ArrayList<Monster> spelledMonsters;
    private Table playerTable;
    private Table opponentTable;
    private Table winnerTable;
    private Table loserTable;
    private boolean arePlayersImmune;

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


    public final SpellTrapCell getFieldSpellCell() {
        if (playerTable.getFieldSpell() != null) {
            return playerTable.getFieldSpellCell();
        }
        if (opponentTable.getFieldSpell() != null) {
            return opponentTable.getFieldSpellCell();
        }
        return null;
    }

    public final Spell getFieldSpell() {
        if (playerTable.getFieldSpell() != null) {
            return playerTable.getFieldSpell();
        }
        return opponentTable.getFieldSpell();
    }

    public final void setFieldSpell(Spell spell, CardState state) {
        if (playerTable.getFieldSpell() != null) {
            playerTable.addCardToGraveyard(playerTable.getFieldSpell());
        }
        if (opponentTable.getFieldSpell() != null) {
            opponentTable.addCardToGraveyard(opponentTable.getFieldSpell());
        }
        playerTable.getFieldSpellCell().clear();
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public final boolean isMonsterSpelled(Monster monster) {
        return spelledMonsters.contains(monster);
    }

    public final void clearSpelledMonsters() {
        this.spelledMonsters.clear();
    }


    public final void swapTables() {
        Table temp = this.playerTable;
        this.playerTable = this.opponentTable;
        this.opponentTable = temp;
    }


    @Override
    public String toString() {
        return "=========================" + "\n" +
                opponentTable.toString(true) + "\n" +
                "-------------------------" + "\n" +
                playerTable.toString(false) + "\n" +
                "=========================";
    }
}
