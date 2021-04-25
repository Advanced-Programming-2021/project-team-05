package controller;

import model.Deck;
import model.User;
import model.card.Card;

import java.util.ArrayList;


public class DeckMenuController {

    private final User user;


    public DeckMenuController(User user) {
        this.user = user;
    }


    public User getUser() {
        return this.user;
    }


    public final DeckMenuMessage createDeck(String name) {
        if (user.getDeckByName(name) != null) {
            return DeckMenuMessage.DECK_NAME_EXISTS;
        }

        Deck deck = new Deck(name);
        DataManager.getInstance().addDeck(deck);
        user.addDeck(deck);
        return DeckMenuMessage.DECK_CREATED;
    }


    public final DeckMenuMessage deleteDeck(String name) {
        Deck deck = user.getDeckByName(name);
        if (deck == null) {
            return DeckMenuMessage.NO_DECK_EXISTS;
        }

        user.removeDeck(deck);
        DataManager.getInstance().removeDeck(deck);
        return DeckMenuMessage.DECK_DELETED;
    }


    public final DeckMenuMessage activateDeck(String name) {
        Deck deck = user.getDeckByName(name);
        if (deck == null) {
            return DeckMenuMessage.NO_DECK_EXISTS;
        }

        user.setActiveDeck(deck);
        return DeckMenuMessage.DECK_ACTIVATED;
    }


    public final DeckMenuMessage addCard(String deckName, String cardName, boolean isSideDeck) {
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            return DeckMenuMessage.NO_DECK_EXISTS;
        }

        ArrayList<Card> cards = user.getPurchasedCardsByName(cardName);
        cards.removeIf(card -> (deck.hasCardInMainDeck(card) || deck.hasCardInSideDeck(card)));
        if (cards.size() == 0) {
            return DeckMenuMessage.NO_CARD_EXISTS;
        }

        if (isSideDeck) {
            if (deck.isSideDeckFull()) {
                return DeckMenuMessage.SIDE_DECK_IS_FULL;
            }
        } else {
            if (deck.isMainDeckFull()) {
                return DeckMenuMessage.MAIN_DECK_IS_FULL;
            }
        }

        Card card = cards.get(0);
        if (deck.isCardFull(card)) {
            return DeckMenuMessage.DECK_IS_FULL;
        }

        if (isSideDeck) {
            deck.addCardToSideDeck(card);
        } else {
            deck.addCardToMainDeck(card);
        }
        return DeckMenuMessage.CARD_ADDED;
    }


    public final DeckMenuMessage removeCard(String deckName, String cardName, boolean isSideDeck) {
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            return DeckMenuMessage.NO_DECK_EXISTS;
        }

        if (isSideDeck) {
            ArrayList<Card> cards = deck.getCardsByNameInSideDeck(cardName);
            if (cards.size() == 0) {
                return DeckMenuMessage.NO_CARD_EXISTS_IN_SIDE_DECK;
            }
            deck.removeCardFromSideDeck(cards.get(0));
        } else {
            ArrayList<Card> cards = deck.getCardsByNameInMainDeck(cardName);
            if (cards.size() == 0) {
                return DeckMenuMessage.NO_CARD_EXISTS_IN_MAIN_DECK;
            }
            deck.removeCardFromMainDeck(cards.get(0));
        }
        return DeckMenuMessage.CARD_REMOVED;
    }
}
