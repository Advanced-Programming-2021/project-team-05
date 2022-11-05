package control.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import control.DataManager;
import control.Receiver;
import model.Deck;
import model.ScoreboardItem;
import model.User;

import java.util.ArrayList;

public class DataController {

    public static void parseCommand(String commandName, JsonObject infoObject, Receiver receiver) {
        JsonObject responseObject;
        switch (commandName) {
            case "get_user_by_token":
                responseObject = getUserByToken(infoObject);
                break;
            case "get_shop_items":
                responseObject = getShopItems(infoObject);
                break;
            case "get_decks":
                responseObject = getDecks(infoObject);
                break;
            case "get_deck_info":
                responseObject = getDeckInfo(infoObject);
                break;
            case "get_addable_cards":
                responseObject = getAddableCards(infoObject);
                break;
            case "get_scoreboard_items":
                responseObject = getScoreboardItems(infoObject);
                break;
            case "get_messages":
                responseObject = getMessages(infoObject);
                break;
            default:
                responseObject = new JsonObject();
                responseObject.addProperty("message", "ERROR");
        }
        receiver.sendResponse(responseObject.toString());
    }


    private static JsonObject getUserByToken(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "data");
        responseObject.addProperty("command_name", "get_user_by_token_response");
        responseObject.add("info", responseInfoObject);
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        Gson gson = new GsonBuilder().serializeNulls().create();
        responseInfoObject.add("user", gson.toJsonTree(user));
        return responseObject;
    }


    private static JsonObject getShopItems(JsonObject infoObject) {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "data");
        responseObject.addProperty("command_name", "get_shop_items_response");
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            JsonObject responseInfoObject = new JsonObject();
            responseInfoObject.add("templates", null);
            responseInfoObject.add("purchased_counts", null);
            responseObject.add("info", responseInfoObject);
            return responseObject;
        }
        JsonObject items = dataManager.getShopItems(user);
        responseObject.add("info", items);
        return responseObject;
    }


    private static JsonObject getDecks(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "data");
        responseObject.addProperty("command_name", "get_decks_response");
        responseObject.add("info", responseInfoObject);
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseInfoObject.add("decks", null);
            return responseObject;
        }
        JsonArray decks = dataManager.getUserDecks(user);
        responseInfoObject.add("decks", decks);
        return responseObject;
    }


    private static JsonObject getDeckInfo(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        responseInfoObject.add("deck", null);
        responseInfoObject.add("main_deck", null);
        responseInfoObject.add("side_deck", null);
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "data");
        responseObject.addProperty("command_name", "get_deck_info_response");
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseObject.add("info", responseInfoObject);
            return responseObject;
        }
        String deckName = infoObject.get("deck_name").getAsString();
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            responseObject.add("info", responseInfoObject);
            return responseObject;
        }
        JsonObject deckInfo = dataManager.getDeckInfo(deck);
        responseObject.add("info", deckInfo);
        return responseObject;
    }


    private static JsonObject getAddableCards(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "data");
        responseObject.addProperty("command_name", "get_addable_cards_response");
        responseObject.add("info", responseInfoObject);
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseInfoObject.add("addable_cards", null);
            return responseObject;
        }
        String deckName = infoObject.get("deck_name").getAsString();
        Deck deck = user.getDeckByName(deckName);
        if (deck == null) {
            responseInfoObject.add("addable_cards", null);
            return responseObject;
        }
        JsonArray addableCards = dataManager.getAddableCards(deck, user);
        responseInfoObject.add("addable_cards", addableCards);
        return responseObject;
    }


    private static JsonObject getScoreboardItems(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "data");
        responseObject.addProperty("command_name", "get_scoreboard_items_response");
        responseObject.add("info", responseInfoObject);
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseInfoObject.add("scoreboard_items", null);
            return responseObject;
        }
        ArrayList<ScoreboardItem> scoreboardItems = dataManager.getScoreboardItems(user);
        Gson gson = new Gson();
        responseInfoObject.add("scoreboard_items", gson.toJsonTree(scoreboardItems).getAsJsonArray());
        return responseObject;
    }


    private static JsonObject getMessages(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "data");
        responseObject.addProperty("command_name", "get_messages_response");
        responseObject.add("info", responseInfoObject);
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseInfoObject.add("messages", null);
            return responseObject;
        }
        JsonArray messagesArray = dataManager.getMessagesArray();
        responseInfoObject.add("messages", messagesArray);
        return responseObject;
    }
}
