package control.controller;

import control.DataManager;
import control.message.DeckMenuMessage;
import model.Deck;
import model.User;
import model.card.Card;
import view.DeckMenuView;

import java.util.ArrayList;


public class DeckMenuController {

    private final User user;
    private DeckMenuView view;


    public DeckMenuController(User user) {
        this.user = user;
    }


    public void setView(DeckMenuView view) {
        this.view = view;
    }


    public User getUser() {
        return this.user;
    }


    public final void createDeck(String deckName) {
        if (user.getDeckByName(deckName) != null) {
            view.showCreateDeckMessage(DeckMenuMessage.DECK_NAME_EXISTS);
            return;
        }
        Deck deck = new Deck(deckName);
        DataManager.getInstance().addDeck(deck);
        user.addDeck(deck);
        view.showCreateDeckMessage(DeckMenuMessage.DECK_CREATED);
    }


    public final void deleteDeck(String deckName) {
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            view.showDeleteDeckMessage(DeckMenuMessage.NO_DECK_EXISTS, deckName);
            return;
        }
        user.removeDeck(deck);
        DataManager.getInstance().removeDeck(deck);
        view.showDeleteDeckMessage(DeckMenuMessage.DECK_DELETED, deckName);
    }


    public final void activateDeck(String deckName) {
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            view.showActivateDeckMessage(DeckMenuMessage.NO_DECK_EXISTS, deckName);
            return;
        }
        user.setActiveDeck(deck);
        view.showActivateDeckMessage(DeckMenuMessage.DECK_ACTIVATED, deckName);
    }


    public final void addCard(String deckName, String cardName, boolean isSideDeck) {
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            view.showAddCardMessage(DeckMenuMessage.NO_DECK_EXISTS, deckName, cardName);
            return;
        }
        ArrayList<Card> cards = user.getPurchasedCardsByName(cardName);
        cards.removeIf(card -> (deck.hasCardInMainDeck(card) || deck.hasCardInSideDeck(card)));
        if (cards.size() == 0) {
            view.showAddCardMessage(DeckMenuMessage.NO_CARD_EXISTS, deckName, cardName);
            return;
        }
        if (isSideDeck) {
            if (deck.isSideDeckFull()) {
                view.showAddCardMessage(DeckMenuMessage.SIDE_DECK_IS_FULL, deckName, cardName);
                return;
            }
        } else {
            if (deck.isMainDeckFull()) {
                view.showAddCardMessage(DeckMenuMessage.MAIN_DECK_IS_FULL, deckName, cardName);
                return;
            }
        }
        Card card = cards.get(0);
        if (deck.isCardFull(card)) {
            view.showAddCardMessage(DeckMenuMessage.DECK_IS_FULL, deckName, cardName);
            return;
        }
        if (isSideDeck) {
            deck.addCardToSideDeck(card);
        } else {
            deck.addCardToMainDeck(card);
        }
        view.showAddCardMessage(DeckMenuMessage.CARD_ADDED, deckName, cardName);
    }


    public final void removeCard(String deckName, String cardName, boolean isSideDeck) {
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            view.showRemoveCardMessage(DeckMenuMessage.NO_DECK_EXISTS, deckName, cardName);
            return;
        }
        if (isSideDeck) {
            ArrayList<Card> cards = deck.getCardsByNameInSideDeck(cardName);
            if (cards.size() == 0) {
                view.showRemoveCardMessage(DeckMenuMessage.NO_CARD_EXISTS_IN_SIDE_DECK, deckName, cardName);
                return;
            }
            deck.removeCardFromSideDeck(cards.get(0));
        } else {
            ArrayList<Card> cards = deck.getCardsByNameInMainDeck(cardName);
            if (cards.size() == 0) {
                view.showRemoveCardMessage(DeckMenuMessage.NO_CARD_EXISTS_IN_MAIN_DECK, deckName, cardName);
                return;
            }
            deck.removeCardFromMainDeck(cards.get(0));
        }
        view.showRemoveCardMessage(DeckMenuMessage.CARD_REMOVED, deckName, cardName);
    }
}
