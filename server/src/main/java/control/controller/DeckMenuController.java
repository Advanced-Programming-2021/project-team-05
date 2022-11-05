package control.controller;

import com.google.gson.JsonObject;
import control.DataManager;
import control.Receiver;
import control.message.DeckMenuMessage;
import model.Deck;
import model.User;
import model.card.Card;

import java.util.ArrayList;

public class DeckMenuController {

    public static void parseCommand(String commandName, JsonObject infoObject, Receiver receiver) {
        JsonObject responseObject;
        switch (commandName) {
            case "create_deck":
                responseObject = createDeck(infoObject);
                break;
            case "delete_deck":
                responseObject = deleteDeck(infoObject);
                break;
            case "activate_deck":
                responseObject = activateDeck(infoObject);
                break;
            case "add_card":
                responseObject = addCard(infoObject);
                break;
            case "remove_card":
                responseObject = removeCard(infoObject);
                break;
            default:
                responseObject = new JsonObject();
                responseObject.addProperty("message", String.valueOf(DeckMenuMessage.ERROR));
        }
        receiver.sendResponse(responseObject.toString());
    }


    public static JsonObject createDeck(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "deck");
        responseObject.addProperty("command_name", "create_deck_response");
        responseObject.add("info", responseInfoObject);
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.ERROR));
            return responseObject;
        }
        String deckName = infoObject.get("deck_name").getAsString();
        if (user.getDeckByName(deckName) != null) {
            responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.DECK_NAME_EXISTS));
            return responseObject;
        }
        Deck deck = new Deck(deckName);
        DataManager.getInstance().addDeck(deck);
        user.addDeck(deck);
        responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.DECK_CREATED));
        return responseObject;
    }


    public static JsonObject deleteDeck(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "deck");
        responseObject.addProperty("command_name", "delete_deck_response");
        responseObject.add("info", responseInfoObject);
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.ERROR));
            return responseObject;
        }
        String deckName = infoObject.get("deck_name").getAsString();
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.NO_DECK_EXISTS));
            return responseObject;
        }
        user.removeDeck(deck);
        DataManager.getInstance().removeDeck(deck);
        responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.DECK_DELETED));
        return responseObject;
    }


    public static JsonObject activateDeck(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "deck");
        responseObject.addProperty("command_name", "activate_deck_response");
        responseObject.add("info", responseInfoObject);
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.ERROR));
            return responseObject;
        }
        String deckName = infoObject.get("deck_name").getAsString();
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.NO_DECK_EXISTS));
            return responseObject;
        }
        user.setActiveDeck(deck);
        responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.DECK_ACTIVATED));
        return responseObject;
    }


    public static JsonObject addCard(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "deck");
        responseObject.addProperty("command_name", "add_card_response");
        responseObject.add("info", responseInfoObject);
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.ERROR));
            return responseObject;
        }
        String deckName = infoObject.get("deck_name").getAsString();
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.NO_DECK_EXISTS));
            return responseObject;
        }
        String cardName = infoObject.get("card_name").getAsString();
        ArrayList<Card> cards = user.getPurchasedCardsByName(cardName);
        cards.removeIf(card -> (deck.hasCardInMainDeck(card) || deck.hasCardInSideDeck(card)));
        if (cards.size() == 0) {
            responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.NO_CARD_EXISTS));
            return responseObject;
        }
        boolean isSideDeck = infoObject.get("is_side").getAsBoolean();
        if (isSideDeck) {
            if (deck.isSideDeckFull()) {
                responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.SIDE_DECK_IS_FULL));
                return responseObject;
            }
        } else {
            if (deck.isMainDeckFull()) {
                responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.MAIN_DECK_IS_FULL));
                return responseObject;
            }
        }
        Card card = cards.get(0);
        if (deck.isCardFull(card)) {
            responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.DECK_IS_FULL));
            return responseObject;
        }
        if (isSideDeck) deck.addCardToSideDeck(card);
        else deck.addCardToMainDeck(card);
        responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.CARD_ADDED));
        return responseObject;
    }


    public static JsonObject removeCard(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "deck");
        responseObject.addProperty("command_name", "remove_card_response");
        responseObject.add("info", responseInfoObject);
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.ERROR));
            return responseObject;
        }
        String deckName = infoObject.get("deck_name").getAsString();
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.NO_DECK_EXISTS));
            return responseObject;
        }
        String cardName = infoObject.get("card_name").getAsString();
        boolean isSideDeck = infoObject.get("is_side").getAsBoolean();
        if (isSideDeck) {
            ArrayList<Card> cards = deck.getCardsByNameInSideDeck(cardName);
            if (cards.size() == 0) {
                responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.NO_CARD_EXISTS_IN_SIDE_DECK));
                return responseObject;
            }
            deck.removeCardFromSideDeck(cards.get(0));
        } else {
            ArrayList<Card> cards = deck.getCardsByNameInMainDeck(cardName);
            if (cards.size() == 0) {
                responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.NO_CARD_EXISTS_IN_MAIN_DECK));
                return responseObject;
            }
            deck.removeCardFromMainDeck(cards.get(0));
        }
        responseInfoObject.addProperty("message", String.valueOf(DeckMenuMessage.CARD_REMOVED));
        return responseObject;
    }
}
