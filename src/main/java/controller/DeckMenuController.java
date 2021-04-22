package controller;

import model.User;

public class DeckMenuController {

    private final User user;


    public DeckMenuController(User user) {
        this.user = user;
    }


    public User getUser() {
        return this.user;
    }


    public final DeckMenuMessage createDeck(String name) {
        return null;
    }


    public final DeckMenuMessage deleteDeck(String name) {
        return null;
    }


    public final DeckMenuMessage activateDeck(String name) {
        return null;
    }


    public final DeckMenuMessage addCard(String deckName, String cardName, boolean isSideDeck) {
        return null;
    }


    public final DeckMenuMessage removeCard(String deckName, String cardName, boolean isSideDeck) {
        return null;
    }


    public final String showAllDecks() {
        return null;
    }


    public final String showDeck(String name, boolean isSideDeck) {
        return null;
    }


    public final String showAllCards() {
        return null;
    }
}
