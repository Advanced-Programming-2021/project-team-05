package model.board;

import model.Deck;
import model.User;
import model.board.cell.Cell;
import model.board.cell.MonsterCell;
import model.board.cell.SpellTrapCell;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import utils.Utility;

import java.util.ArrayList;


public class Table {
    private final ArrayList<Card> hand;
    private final ArrayList<Card> graveyard;
    private final MonsterCell[] monsterCells;
    private final SpellTrapCell[] spellAndTrapCells;
    private final SpellTrapCell fieldZoneCell;
    private User owner;
    private int lifePoint;
    private Deck deck;
    private boolean canSummonOrSet;

    {
        hand = new ArrayList<>();
        graveyard = new ArrayList<>();

        monsterCells = new MonsterCell[5];
        for (int i = 0; i < monsterCells.length; i++) {
            monsterCells[i] = new MonsterCell(null, null);
        }

        spellAndTrapCells = new SpellTrapCell[5];
        for (int i = 0; i < spellAndTrapCells.length; i++) {
            spellAndTrapCells[i] = new SpellTrapCell(null, null);
        }

        fieldZoneCell = new SpellTrapCell(null, null);
    }


    public Table(User owner) {
        this.setOwner(owner);
        try {
            this.setDeck(owner.getActiveDeck().clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.setCanSummonOrSet(true);
        this.setLifePoint(8000);
    }


    public final User getOwner() {
        return this.owner;
    }

    public final void setOwner(User owner) {
        this.owner = owner;
    }


    public final int getLifePoint() {
        return this.lifePoint;
    }

    public final void setLifePoint(int lifePoint) {
        this.lifePoint = lifePoint;
    }

    public final void increaseLifePoint(int amount) {
        this.lifePoint += amount;
    }

    public final void decreaseLifePoint(int amount) {
        this.lifePoint -= amount;
    }


    public final Deck getDeck() {
        return this.deck;
    }

    public final void setDeck(Deck deck) {
        this.deck = deck;
    }


    public final Cell getCellByAddress(CardAddress cardAddress) {
        if (cardAddress.isForOpponent()) {
            return null;
        }
        switch (cardAddress.getZone()) {
            case FIELD:
                return this.fieldZoneCell;
            case MONSTER:
                return this.monsterCells[cardAddress.getPosition() - 1];
            case SPELL:
                return this.spellAndTrapCells[cardAddress.getPosition() - 1];
            default:
                return null;
        }
    }

    public final MonsterCell getMonsterCell(int position) {
        return this.monsterCells[position - 1];
    }

    public final SpellTrapCell getSpellOrTrapCell(int position) {
        return this.spellAndTrapCells[position - 1];
    }

    public final SpellTrapCell getFieldSpellCell() {
        return this.fieldZoneCell;
    }


    public ArrayList<Card> getHand() {
        return this.hand;
    }

    public Card getCardFromHand(int position) {
        return this.hand.get(position - 1);
    }

    public final void addCardToHand(Card card) {
        this.hand.add(card);
    }

    public final void removeCardFromHand(int position) {
        this.hand.remove(position - 1);
    }

    public final boolean isHandFull() {
        return hand.size() >= 6;
    }

    public final void initializeHand() {
        this.getDeck().shuffleMainDeck();
        for (int i = 0; i < 5; i++) {
            this.drawCard();
        }
    }


    public final void drawCard() {
        this.addCardToHand(deck.drawCard());
    }


    public final ArrayList<Card> getGraveyard() {
        return this.graveyard;
    }

    public final void addCardToGraveyard(Card card) {
        this.graveyard.add(card);
    }

    public final void removeCardFromGraveyard(Card card) {
        this.graveyard.remove(card);
    }

    public final void moveMonsterToGraveyard(int position) {
        this.addCardToGraveyard(this.getMonster(position));
        this.removeMonster(position);
    }

    public final void moveSpellOrTrapToGraveyard(int position) {
        this.addCardToGraveyard(this.getSpellOrTrap(position));
        this.removeSpellOrTrap(position);
    }


    public final Monster getMonster(int position) {
        return (Monster) this.monsterCells[position - 1].getCard();
    }

    public final CardState getMonsterState(int position) {
        return this.monsterCells[position - 1].getState();
    }

    public final int getMonsterPosition(Monster monster) {
        for (int i = 1; i <= 5; i++) {
            if (monster.equals(this.getMonster(i))) return i;
        }
        return -1;
    }

    public final void addMonster(Monster monster, CardState state) {
        Cell cell = this.monsterCells[this.getFirstEmptyMonsterCellPosition() - 1];
        if (cell.getCard() == null) {
            cell.clear();
            cell.setCard(monster);
            cell.setState(state);
            cell.setNewlyAdded(true);
        }
    }

    public final void removeMonster(int position) {
        this.monsterCells[position - 1].clear();
    }

    public final int getMonsterCardsCount() {
        int count = 0;
        for (Cell cell : this.monsterCells) {
            if (cell.getCard() != null) {
                count++;
            }
        }
        return count;
    }

    public final boolean isMonsterZoneFull() {
        for (Cell cell : this.monsterCells) {
            if (cell.getCard() == null) {
                return false;
            }
        }
        return true;
    }

    public final int getFirstEmptyMonsterCellPosition() {
        for (int i = 0; i < 5; i++) {
            Cell cell = this.monsterCells[i];
            if (cell.getCard() == null) {
                return i + 1;
            }
        }
        return -1;
    }

    public final boolean hasMonster(Monster monster) {
        for (int i = 1; i <= 5; i++) {
            if (monster.equals(this.getMonster(i))) {
                return true;
            }
        }
        return false;
    }


    public final Card getSpellOrTrap(int position) {
        return this.spellAndTrapCells[position - 1].getCard();
    }

    public final CardState getSpellOrTrapState(int position) {
        return this.spellAndTrapCells[position - 1].getState();
    }

    public final int getSpellOrTrapPosition(Card card) {
        for (int i = 1; i <= 5; i++) {
            if (card.equals(this.getSpellOrTrap(i))) return i;
        }
        return -1;
    }

    public final void addSpellOrTrap(Card card, CardState state) {
        for (int i = 0; i < 5; i++) {
            Cell cell = this.spellAndTrapCells[i];
            if (cell.getCard() == null) {
                cell.clear();
                cell.setCard(card);
                cell.setState(state);
                cell.setNewlyAdded(true);
                return;
            }
        }
    }

    public final void removeSpellOrTrap(int position) {
        this.spellAndTrapCells[position - 1].clear();
    }

    public final int getSpellTrapCardsCount() {
        int count = 0;
        for (Cell cell : this.spellAndTrapCells) {
            if (cell.getCard() != null) {
                count++;
            }
        }
        return count;
    }

    public final boolean isSpellTrapZoneFull() {
        for (Cell cell : this.spellAndTrapCells) {
            if (cell.getCard() == null) {
                return false;
            }
        }
        return true;
    }


    public final Spell getFieldSpell() {
        return (Spell) this.fieldZoneCell.getCard();
    }

    public final void setFieldSpell(Spell spell, CardState state) {
        this.fieldZoneCell.clear();
        this.fieldZoneCell.setCard(spell);
        this.fieldZoneCell.setState(state);
    }


    public final void reset() {
        this.setCanSummonOrSet(true);
        for (int i = 1; i <= 5; i++) {
            this.getMonsterCell(i).reset();
            this.getSpellOrTrapCell(i).reset();
        }
        this.getFieldSpellCell().reset();
    }


    public final boolean canSummonOrSet() {
        return this.canSummonOrSet;
    }

    public final void setCanSummonOrSet(boolean canSummonOrSet) {
        this.canSummonOrSet = canSummonOrSet;
    }


    private String[] monsterStatesToString() {
        String[] monsterZone = new String[5];
        for (int i = 0; i < 5; i++) {
            CardState cardState = this.getMonsterState(i + 1);
            if (cardState == null) {
                monsterZone[i] = "E";
            } else {
                switch (cardState) {
                    case VERTICAL_UP:
                        monsterZone[i] = "OO";
                        break;
                    case HORIZONTAL_UP:
                        monsterZone[i] = "DO";
                        break;
                    case HORIZONTAL_DOWN:
                        monsterZone[i] = "DH";
                        break;
                }
            }
        }

        return monsterZone;
    }

    private String[] spellAndTrapStatesToString() {
        String[] spellZone = new String[5];
        for (int i = 0; i < 5; i++) {
            CardState cardState = this.getSpellOrTrapState(i + 1);
            if (cardState == null) {
                spellZone[i] = "E";
            } else {
                switch (cardState) {
                    case VERTICAL_UP:
                        spellZone[i] = "O";
                        break;
                    case VERTICAL_DOWN:
                        spellZone[i] = "H";
                        break;
                }
            }
        }

        return spellZone;
    }

    private String[] handToString() {
        String[] handString = new String[6];
        for (int i = 0; i < 6; i++) {
            if (i < this.getHand().size()) {
                handString[i] = "c";
            } else {
                handString[i] = "";
            }
        }

        return handString;
    }

    public final String graveyardToString() {
        if (this.graveyard.size() == 0) {
            return "graveyard empty";
        }
        StringBuilder graveyardString = new StringBuilder();
        for (int i = 0, cardsSize = this.graveyard.size(); i < cardsSize; i++) {
            Card card = this.graveyard.get(i);
            graveyardString.append(i + 1).append(". ").append(card.toString());
            if (i < cardsSize - 1) {
                graveyardString.append("\r\n");
            }
        }
        return graveyardString.toString();
    }


    public final String toString(boolean isReversed) {
        StringBuilder tableString = new StringBuilder();
        String fieldZone = this.getFieldSpell() == null ? "E" : "O";
        int graveyardSize = this.getGraveyard().size();
        int deckSize = this.getDeck().getMainDeckSize();
        String[] monsterZone = this.monsterStatesToString();
        String[] spellZone = this.spellAndTrapStatesToString();
        String[] handString = this.handToString();

        if (isReversed) {
            Utility.swapElements(monsterZone, 1, 2);
            Utility.swapElements(monsterZone, 3, 4);
            Utility.swapElements(spellZone, 1, 2);
            Utility.swapElements(spellZone, 3, 4);
        }

        if (isReversed) {
            tableString.append(graveyardSize).append("\t\t\t\t\t\t").append(fieldZone).append("\n");
        } else {
            tableString.append(fieldZone).append("\t\t\t\t\t\t").append(graveyardSize).append("\n");
        }
        tableString.append("\t")
                .append(monsterZone[4]).append("\t")
                .append(monsterZone[2]).append("\t")
                .append(monsterZone[0]).append("\t")
                .append(monsterZone[1]).append("\t")
                .append(monsterZone[3]).append("\t")
                .append("\n");
        tableString.append("\t")
                .append(spellZone[4]).append("\t")
                .append(spellZone[2]).append("\t")
                .append(spellZone[0]).append("\t")
                .append(spellZone[1]).append("\t")
                .append(spellZone[3]).append("\t")
                .append("\n");
        if (!isReversed) {
            tableString.append("\t\t\t\t\t\t");
        }
        tableString.append(deckSize).append("\n");
        if (isReversed) {
            for (int i = 0; i < 7 - this.hand.size(); i++) {
                tableString.append("\t");
            }
        }
        for (int i = 0; i < 6; i++) {
            tableString.append(handString[i]).append("\t");
        }
        tableString.append("\n");
        tableString.append(this.getOwner().getUsername()).append(":").append(this.getLifePoint());

        if (isReversed) {
            String[] splitTable = tableString.toString().split("\n");
            Utility.reverseArray(splitTable);
            return Utility.joinArray(splitTable, '\n');
        } else {
            return tableString.toString();
        }
    }
}
