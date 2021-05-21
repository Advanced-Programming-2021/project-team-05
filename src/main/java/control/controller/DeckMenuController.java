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
            view.printCreateDeckMessage(DeckMenuMessage.DECK_NAME_EXISTS, deckName);
            return;
        }
        Deck deck = new Deck(deckName);
        DataManager.getInstance().addDeck(deck);
        user.addDeck(deck);
        view.printCreateDeckMessage(DeckMenuMessage.DECK_CREATED, deckName);
    }


    public final void deleteDeck(String deckName) {
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            view.printDeleteDeckMessage(DeckMenuMessage.NO_DECK_EXISTS, deckName);
            return;
        }
        user.removeDeck(deck);
        DataManager.getInstance().removeDeck(deck);
        view.printDeleteDeckMessage(DeckMenuMessage.DECK_DELETED, deckName);
    }


    public final void activateDeck(String deckName) {
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            view.printActivateDeckMessage(DeckMenuMessage.NO_DECK_EXISTS, deckName);
            return;
        }
        user.setActiveDeck(deck);
        view.printActivateDeckMessage(DeckMenuMessage.DECK_ACTIVATED, deckName);
    }


    public final void addCard(String deckName, String cardName, boolean isSideDeck) {
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            view.printAddCardMessage(DeckMenuMessage.NO_DECK_EXISTS, deckName, cardName);
            return;
        }
        ArrayList<Card> cards = user.getPurchasedCardsByName(cardName);
        cards.removeIf(card -> (deck.hasCardInMainDeck(card) || deck.hasCardInSideDeck(card)));
        if (cards.size() == 0) {
            view.printAddCardMessage(DeckMenuMessage.NO_CARD_EXISTS, deckName, cardName);
            return;
        }
        if (isSideDeck) {
            if (deck.isSideDeckFull()) {
                view.printAddCardMessage(DeckMenuMessage.SIDE_DECK_IS_FULL, deckName, cardName);
                return;
            }
        } else {
            if (deck.isMainDeckFull()) {
                view.printAddCardMessage(DeckMenuMessage.MAIN_DECK_IS_FULL, deckName, cardName);
                return;
            }
        }
        Card card = cards.get(0);
        if (deck.isCardFull(card)) {
            view.printAddCardMessage(DeckMenuMessage.DECK_IS_FULL, deckName, cardName);
            return;
        }
        if (isSideDeck) {
            deck.addCardToSideDeck(card);
        } else {
            deck.addCardToMainDeck(card);
        }
        view.printAddCardMessage(DeckMenuMessage.CARD_ADDED, deckName, cardName);
    }


    public final void removeCard(String deckName, String cardName, boolean isSideDeck) {
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            view.printRemoveCardMessage(DeckMenuMessage.NO_DECK_EXISTS, deckName, cardName);
            return;
        }
        if (isSideDeck) {
            ArrayList<Card> cards = deck.getCardsByNameInSideDeck(cardName);
            if (cards.size() == 0) {
                view.printRemoveCardMessage(DeckMenuMessage.NO_CARD_EXISTS_IN_SIDE_DECK, deckName, cardName);
                return;
            }
            deck.removeCardFromSideDeck(cards.get(0));
        } else {
            ArrayList<Card> cards = deck.getCardsByNameInMainDeck(cardName);
            if (cards.size() == 0) {
                view.printRemoveCardMessage(DeckMenuMessage.NO_CARD_EXISTS_IN_MAIN_DECK, deckName, cardName);
                return;
            }
            deck.removeCardFromMainDeck(cards.get(0));
        }
        view.printRemoveCardMessage(DeckMenuMessage.CARD_REMOVED, deckName, cardName);
    }
}
