package model;

import controller.DataManager;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;

import java.util.ArrayList;


public class Table {
    private User Owner;
    private Deck deck;
    private ArrayList<Card> hand;
    private ArrayList<Card> graveyard;
    private final Monster[] monsters;
    private final CardState[] monstersState;
    private final Card[] spellsAndTraps;
    private final CardState[] spellsAndTrapsState;
    private Spell fieldZoneSpell;
    private int lifePoint;


    {
        monsters = new Monster[5];
        monstersState = new CardState[5];
        spellsAndTraps = new Card[5];
        spellsAndTrapsState = new CardState[5];
    }


    public Table(User owner) {
        this.setOwner(owner);
    }


    public ArrayList<Card> getHand() {
        return this.hand;
    }

    public Card getCardFromHand(int position) {
        return this.hand.get(position - 1);
    }

    public final User getOwner() {
        return this.Owner;
    }

    public final void setOwner(User owner) {
        this.Owner = owner;
    }


    public final Deck getDeck() {
        return this.deck;
    }

    public final void setDeck(Deck deck) {

    }

    public final Card getCardByAddress(CardAddress cardAddress){
        switch (cardAddress.getZone()){
            case FIELD:
                break;
            case MONSTER:
                return getMonster(cardAddress.getPosition());
            case SPELL:
                return getSpellOrTrap(cardAddress.getPosition());
            case HAND:
                return getHand().get(cardAddress.getPosition());
        }
        return null;
    }


    public final void removeCardFromDeck(Card card) {

        this.deck.removeCardFromMainDeck(card);
    }


    public final void addCardToHand(Card card) {
        this.hand.add(card);
    }

    public final void removeCardFromHand(Card card) {
        this.hand.remove(card);
    }


    public final ArrayList<Card> getGraveyard() {
        return this.graveyard;
    }

    public final void addCardToGraveyard(Card card) {
        this.graveyard.add(card);
    }


    public final Monster getMonster(int position) {
        return this.monsters[position];
    }

    public final void addMonster(Monster monster, CardState state) {

        for (int i = 0; i < 5; i++) {
            if (this.monsters[i] == null){
                monsters[i] = monster;
                monstersState[i] = state;
            }
        }
    }


    public final void removeMonster(int position) {
        this.monsters[position] = null;
        this.monstersState[position] = null;
    }

    public final int getMonsterCardsCount() {
        int count = 0;
        for (Monster monster : monsters) {
            if (monster != null) {
                count++;
            }
        }
        return count;
    }

    public final boolean isMonsterZoneFull() {
        for (Monster monster : monsters) {
            if (monster == null) {
                return false;
            }
        }
        return true;
    }


    public final Card getSpellOrTrap(int position) {
        return this.spellsAndTraps[position];
    }

    public final void addSpellOrTrap(Card card, CardState state) {
        for (int i = 0; i < 5; i++) {
            if (this.monsters[i] == null){
                spellsAndTraps[i] = card;
                spellsAndTrapsState[i] = state;
            }
        }

    }

    public final void removeSpellOrTrap(int position) {
        this.spellsAndTrapsState[position] = null;
        this.spellsAndTraps[position] = null;
    }


    public final Spell getFieldSpell() {
        return this.fieldZoneSpell;
    }

    public final void setFieldSpell(Spell spell) {
        this.fieldZoneSpell = spell;
    }


    public final int getLifePoint() {
        return this.lifePoint;
    }

    public final void increaseLifePoint(int amount) {
        this.lifePoint += amount;
    }

    public final void decreaseLifePoint(int amount) {
        this.lifePoint -= amount;
    }
}
