package controller;

import com.sun.xml.internal.stream.buffer.sax.DefaultWithLexicalHandler;
import model.Deck;
import model.User;
import model.card.Card;

public class DeckMenuController {

    private DataManager dataManager = DataManager.getInstance();

    private final User user;


    public DeckMenuController(User user) {
        this.user = user;
    }


    public User getUser() {
        return this.user;
    }


    public final DeckMenuMessage createDeck(String name) {

        for (String id:
             user.getDecks()) {
            if (dataManager.getDeckByUUID(id).getName().equals(name)){
                return DeckMenuMessage.DECK_NAME_EXISTS;
            }
        }
        Deck deck = new Deck(name);
        dataManager.addDeck(deck);
        user.addDeck(deck);
        return DeckMenuMessage.DECK_CREATED;
    }


    public final DeckMenuMessage deleteDeck(String name) {

        for (String id:
                user.getDecks()) {
            if (dataManager.getDeckByUUID(id).getName().equals(name)){
                user.removeDeck(dataManager.getDeckByUUID(id));
                dataManager.getAllDecks().remove(dataManager.getDeckByUUID(id));
                return DeckMenuMessage.DECK_DELETED;
            }
        }
        return DeckMenuMessage.NO_DECK_EXISTS;
    }


    public final DeckMenuMessage activateDeck(String name) {

        for (String id:
                user.getDecks()) {
            if (dataManager.getDeckByUUID(id).getName().equals(name)){
                user.setActiveDeck(dataManager.getDeckByUUID(id));
                return DeckMenuMessage.DECK_ACTIVATED;
            }
        }
        return DeckMenuMessage.NO_DECK_EXISTS;
    }


    public final DeckMenuMessage addCard(String deckName, String cardName, boolean isSideDeck) {

        Deck deck ;

        for (Card card:
                dataManager.getAllCards()) {
           if (card.getName().equals(cardName)){
               for (String id:
                       user.getDecks()) {
                   if (dataManager.getDeckByUUID(id).getName().equals(deckName)){
                       deck = dataManager.getDeckByUUID(id);
                       if (isSideDeck){
                           if (!deck.isSideDeckFull()){
                               if (!deck.isCardFull(card)){
                                   deck.addCardToSideDeck(card);
                                   return DeckMenuMessage.CARD_ADDED;
                               }
                               else {
                                   return DeckMenuMessage.DECK_IS_FULL;
                               }
                           }
                           else
                               return DeckMenuMessage.SIDE_DECK_IS_FULL;
                       }
                       else {
                           if (!deck.isMainDeckFull())
                               if (!deck.isCardFull(card)){
                                   deck.addCardToMainDeck(card);
                                   return DeckMenuMessage.CARD_ADDED;
                               }
                               else {
                                   return DeckMenuMessage.DECK_IS_FULL;
                               }
                           else
                               return DeckMenuMessage.MAIN_DECK_IS_FULL;
                       }
                   }
               }
               return DeckMenuMessage.NO_DECK_EXISTS;
           }
        }

        return DeckMenuMessage.NO_CARD_EXISTS;
    }


    public final DeckMenuMessage removeCard(String deckName, String cardName, boolean isSideDeck) {
        Deck deck ;

        for (String id:
                user.getDecks()) {
            if (dataManager.getDeckByUUID(id).getName().equals(deckName)){
                for ( Card card:
                dataManager.getAllCards()) {
                    if (card.getName().equals(cardName)){
                        deck = dataManager.getDeckByUUID(id);
                        if (isSideDeck){
                           deck.removeCardFromSideDeck(card);
                           return DeckMenuMessage.CARD_REMOVED;
                        }
                        else {
                            deck.removeCardFromMainDeck(card);
                            return DeckMenuMessage.CARD_REMOVED;
                        }
                    }
                }
                return DeckMenuMessage.NO_CARD_EXISTS;
            }
        }

        return DeckMenuMessage.NO_DECK_EXISTS;
    }

}
