package model;

import model.card.Card;

import java.util.ArrayList;

public class Deck {
    private String id;
    private String name;
    private ArrayList<String> mainDeckCards;
    private ArrayList<String> sideDeckCards;


    public Deck(String name) {

    }


    public final String getId() {
        return null;
    }


    public final void setId(String id) {

    }


    public final String getName() {
        return null;
    }


    public final void setName(String name) {

    }


    public final void addCardToMainDeck(Card card) {

    }


    public final void removeCardFromMainDeck(Card card) {

    }


    public final Card getCardByUUIDInMainDeck(Card card) {
        return null;
    }


    public final ArrayList<Card> getCardsByNameInMainDeck(String name) {
        return null;
    }


    public final boolean isMainDeckFull() {
        return false;
    }


    public final void addCardToSideDeck(Card card) {

    }


    public final void removeCardFromSideDeck(Card card) {

    }


    public final Card getCardByUUIDInSideDeck(Card card) {
        return null;
    }


    public final ArrayList<Card> getCardsByNameInSideDeck(String name) {
        return null;
    }


    public final boolean isSideDeckFull() {
        return false;
    }


    public final boolean isCardFull(Card card) {
        return false;
    }


    public final boolean isValid() {
        return false;
    }


    public final String detailedToString() {
        return null;
    }


    public final String toString() {
        return null;
    }
}
