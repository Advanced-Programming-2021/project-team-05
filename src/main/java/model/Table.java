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
    private Monster[] monsters = new Monster[5];
    private CardState[] monstersState = new CardState[5];
    private Card[] spellsAndTraps = new Card[5];
    private CardState[] spellsAndTrapsState = new CardState[5];
    private Spell fieldZoneSpell;
    private int lifePoint;

    private DataManager dataManager = DataManager.getInstance();

    public Table(User owner) {
        Owner = owner;
    }

    public ArrayList<Card> getHand() {
        return this.hand;
    }

    public final User getOwner() {
        return this.Owner;
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
        int pos = 1;

        pos = this.monsters.length + 1;

        this.monsters[pos]  = monster;
        this.monstersState[pos] = state;

    }

    public final void removeMonster(int position) {
        this.monsters[position] = null;
        this.monstersState[position] = null;
    }

    public final Card getSpellOrTrap(int position) {
        return this.spellsAndTraps[position];
    }

    public final void addSpellOrTrap(Card card, CardState state) {
        int pos = 1;

        pos = this.spellsAndTraps.length + 1;

        this.spellsAndTraps[pos]  = card;
        this.spellsAndTrapsState[pos] = state;

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
