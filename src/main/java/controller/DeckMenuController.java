package controller;

import model.User;

class DeckMenuController {
    private User user;


    private DeckMenuController(User user) {

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
