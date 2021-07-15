package model;

import model.card.Card;

import java.util.ArrayList;

public class DeckInfo {

    private final Deck deck;
    private final ArrayList<Card> mainDeck;
    private final ArrayList<Card> sideDeck;


    public DeckInfo(Deck deck, ArrayList<Card> mainDeck, ArrayList<Card> sideDeck) {
        this.deck = deck;
        this.mainDeck = mainDeck;
        this.sideDeck = sideDeck;
    }


    public Deck getDeck() {
        return this.deck;
    }

    public ArrayList<Card> getMainDeck() {
        return this.mainDeck;
    }

    public ArrayList<Card> getSideDeck() {
        return this.sideDeck;
    }
}
