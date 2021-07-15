package control.controller;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import control.Sender;
import control.message.DeckMenuMessage;
import model.Deck;
import model.DeckInfo;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import view.DeckMenuView;
import view.MainView;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DeckMenuController {

    private DeckMenuView view;


    public void setView(DeckMenuView view) {
        this.view = view;
    }


    public final void createDeck(String deckName) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "deck");
            commandObject.addProperty("command_name", "create_deck");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            DeckMenuMessage message = DeckMenuMessage.valueOf(responseObject.get("message").getAsString());
            view.showCreateDeckMessage(message);
        } catch (Exception e) {
            view.showCreateDeckMessage(DeckMenuMessage.ERROR);
        }
    }


    public final void deleteDeck(String deckName) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "deck");
            commandObject.addProperty("command_name", "delete_deck");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            DeckMenuMessage message = DeckMenuMessage.valueOf(responseObject.get("message").getAsString());
            view.showDeleteDeckMessage(message, deckName);
        } catch (Exception e) {
            view.showDeleteDeckMessage(DeckMenuMessage.ERROR, deckName);
        }
    }


    public final void activateDeck(String deckName) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "deck");
            commandObject.addProperty("command_name", "activate_deck");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            DeckMenuMessage message = DeckMenuMessage.valueOf(responseObject.get("message").getAsString());
            view.showActivateDeckMessage(message, deckName);
        } catch (Exception e) {
            view.showActivateDeckMessage(DeckMenuMessage.ERROR, deckName);
        }
    }


    public final void addCard(String deckName, String cardName, boolean isSideDeck) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            infoObject.addProperty("card_name", cardName);
            infoObject.addProperty("is_side", isSideDeck);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "deck");
            commandObject.addProperty("command_name", "add_card");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            DeckMenuMessage message = DeckMenuMessage.valueOf(responseObject.get("message").getAsString());
            view.showAddCardMessage(message, deckName, cardName);
        } catch (Exception e) {
            view.showAddCardMessage(DeckMenuMessage.ERROR, deckName, cardName);
        }
    }


    public final void removeCard(String deckName, String cardName, boolean isSideDeck) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            infoObject.addProperty("card_name", cardName);
            infoObject.addProperty("is_side", isSideDeck);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "deck");
            commandObject.addProperty("command_name", "remove_card");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            DeckMenuMessage message = DeckMenuMessage.valueOf(responseObject.get("message").getAsString());
            view.showRemoveCardMessage(message, deckName, cardName);
        } catch (Exception e) {
            view.showRemoveCardMessage(DeckMenuMessage.ERROR, deckName, cardName);
        }
    }


    public final ArrayList<Deck> getDecks() {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "data");
            commandObject.addProperty("command_name", "get_decks");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return null;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            JsonArray decksArray = responseObject.get("data").getAsJsonArray();

            Gson gson = new Gson();
            Type deckType = new TypeToken<ArrayList<Deck>>() {
            }.getType();
            return gson.fromJson(decksArray, deckType);
        } catch (Exception e) {
            return null;
        }
    }


    public final DeckInfo getDeckInfo(String deckName) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "data");
            commandObject.addProperty("command_name", "get_deck_info");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return null;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            JsonObject deckInfo = responseObject.get("data").getAsJsonObject();
            JsonObject deckObject = deckInfo.get("deck").getAsJsonObject();
            JsonArray mainDeckArray = deckInfo.get("main_deck").getAsJsonArray();
            JsonArray sideDeckArray = deckInfo.get("side_deck").getAsJsonArray();

            Deck deck = new Gson().fromJson(deckObject, Deck.class);
            ArrayList<Card> mainDeck = parseToArrayList(mainDeckArray);
            ArrayList<Card> sideDeck = parseToArrayList(sideDeckArray);
            return new DeckInfo(deck, mainDeck, sideDeck);
        } catch (Exception e) {
            return null;
        }
    }


    public final ArrayList<Card> getAddableCards(String deckName) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "data");
            commandObject.addProperty("command_name", "get_addable_cards");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return null;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            JsonArray addableCardsArray = responseObject.get("data").getAsJsonArray();
            return parseToArrayList(addableCardsArray);
        } catch (Exception e) {
            return null;
        }
    }



    private RuntimeTypeAdapterFactory<Card> getCardAdapter() {
        return RuntimeTypeAdapterFactory
                .of(Card.class, "card_type")
                .registerSubtype(Monster.class, MonsterTemplate.class.getName())
                .registerSubtype(Spell.class, SpellTemplate.class.getName())
                .registerSubtype(Trap.class, TrapTemplate.class.getName());
    }

    private ArrayList<Card> parseToArrayList(JsonArray cardsArray) {
        RuntimeTypeAdapterFactory<Card> cardAdapter = getCardAdapter();
        Gson cardGson = new GsonBuilder().registerTypeAdapterFactory(cardAdapter).create();
        Type cardType = new TypeToken<ArrayList<Card>>() {
        }.getType();
        return cardGson.fromJson(cardsArray, cardType);
    }
}
