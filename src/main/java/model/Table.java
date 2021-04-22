package model;

import model.card.Card;
import model.card.Monster;
import model.card.Spell;

import java.util.ArrayList;

public class Table {
    private User Owner;
    private ArrayList<Card> deck;
    private ArrayList<Card> hand;
    private ArrayList<Card> graveyard;
    private Monster monsters;
    private CardState monstersState;
    private Card spellsAndTraps;
    private CardState spellsAndTrapsState;
    private Spell fieldZoneSpell;
    private int lifePoint;


    public Table(User owner) {

    }


    public final User getOwner() {
        return null;
    }


    public final ArrayList<Card> getDeck() {
        return null;
    }


    public final void setDeck(Deck deck) {

    }


    public final void removeCardFromDeck(Card card) {

    }


    public final void addCardToHand(Card card) {

    }


    public final void removeCardFromHand(Card card) {

    }


    public final ArrayList<Card> getGraveyard() {
        return null;
    }


    public final void addCardToGraveyard(Card card) {

    }


    public final Monster getMonster(int position) {
        return null;
    }


    public final void addMonster(Monster monster, CardState state) {

    }


    public final void removeMonster(int position) {

    }


    public final Card getSpellOrTrap(int position) {
        return null;
    }


    public final void addSpellOrTrap(Card card, CardState state) {

    }


    public final void removeSpellOrTrap(int position) {

    }


    public final Spell getFieldSpell() {
        return null;
    }


    public final void setFieldSpell(Spell spell) {

    }


    public final int getLifePoint() {
        return 0;
    }


    public final void increaseLifePoint(int amount) {

    }


    public final void decreaseLifePoint(int amount) {

    }
}
