package control.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataController {

    private static DataController dataController;

    private User user;
    private ArrayList<ShopItem> shopItems;
    private ArrayList<Deck> decks;
    private DeckInfo deckInfo;
    private ArrayList<Card> addableCards;
    private ObservableList<ScoreboardItem> scoreboardItems;

    {
        user = null;
        shopItems = null;
        decks = null;
        deckInfo = null;
        addableCards = null;
        scoreboardItems = null;
    }


    private DataController() {
    }


    public static DataController getInstance() {
        if (dataController == null) dataController = new DataController();
        return dataController;
    }


    public User getUser() {
        return this.user;
    }

    public ArrayList<ShopItem> getShopItems() {
        return this.shopItems;
    }

    public ArrayList<Deck> getDecks() {
        return this.decks;
    }

    public DeckInfo getDeckInfo() {
        return this.deckInfo;
    }

    public ArrayList<Card> getAddableCards() {
        return this.addableCards;
    }

    public ObservableList<ScoreboardItem> getScoreboardItems() {
        return this.scoreboardItems;
    }


    public void updateUser() {
        try {
            user = null;
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "data");
            commandObject.addProperty("command_name", "get_user_by_token");
            commandObject.add("info", infoObject);
            MainController.sendMessage(commandObject.toString());
        } catch (Exception ignored) {
        }
    }

    private void updateUser(JsonObject infoObject) {
        user = new Gson().fromJson(infoObject.get("user"), User.class);
    }


    public void updateShopItems() {
        try {
            shopItems = null;
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "data");
            commandObject.addProperty("command_name", "get_shop_items");
            commandObject.add("info", infoObject);
            MainController.sendMessage(commandObject.toString());
        } catch (Exception ignored) {
        }
    }

    private void updateShopItems(JsonObject infoObject) {
        JsonArray templatesArray = infoObject.get("templates").getAsJsonArray();
        JsonArray purchasedCounts = infoObject.get("purchased_counts").getAsJsonArray();

        RuntimeTypeAdapterFactory<CardTemplate> cardTemplateAdapter = getCardTemplateAdapter();
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(cardTemplateAdapter).create();
        Type templateType = CardTemplate.class;
        ArrayList<ShopItem> shopItems = new ArrayList<>();
        for (int i = 0; i < templatesArray.size(); i++) {
            JsonObject templateObject = templatesArray.get(i).getAsJsonObject();
            CardTemplate template = gson.fromJson(templateObject, templateType);
            int count = purchasedCounts.get(i).getAsInt();
            shopItems.add(new ShopItem(template, count));
        }
        this.shopItems = shopItems;
    }


    public void updateDecks() {
        try {
            decks = null;
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "data");
            commandObject.addProperty("command_name", "get_decks");
            commandObject.add("info", infoObject);
            MainController.sendMessage(commandObject.toString());
        } catch (Exception ignored) {
        }
    }

    private void updateDecks(JsonObject infoObject) {
        JsonArray decksArray = infoObject.get("decks").getAsJsonArray();
        Gson gson = new Gson();
        Type deckType = new TypeToken<ArrayList<Deck>>() {
        }.getType();
        decks = gson.fromJson(decksArray, deckType);
    }


    public void updateDeckInfo(String deckName) {
        try {
            deckInfo = null;
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "data");
            commandObject.addProperty("command_name", "get_deck_info");
            commandObject.add("info", infoObject);
            MainController.sendMessage(commandObject.toString());
        } catch (Exception ignored) {
        }
    }

    private void updateDeckInfo(JsonObject infoObject) {
        JsonObject deckObject = infoObject.get("deck").getAsJsonObject();
        JsonArray mainDeckArray = infoObject.get("main_deck").getAsJsonArray();
        JsonArray sideDeckArray = infoObject.get("side_deck").getAsJsonArray();

        Deck deck = new Gson().fromJson(deckObject, Deck.class);
        ArrayList<Card> mainDeck = parseToArrayList(mainDeckArray);
        ArrayList<Card> sideDeck = parseToArrayList(sideDeckArray);
        deckInfo = new DeckInfo(deck, mainDeck, sideDeck);
    }


    public void updateAddableCards(String deckName) {
        try {
            addableCards = null;
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "data");
            commandObject.addProperty("command_name", "get_addable_cards");
            commandObject.add("info", infoObject);
            MainController.sendMessage(commandObject.toString());
        } catch (Exception ignored) {
        }
    }

    private void updateAddableCards(JsonObject infoObject) {
        JsonArray addableCardsArray = infoObject.get("addable_cards").getAsJsonArray();
        addableCards = parseToArrayList(addableCardsArray);
    }


    public void updateScoreboardItems() {
        try {
            scoreboardItems = null;
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "data");
            commandObject.addProperty("command_name", "get_scoreboard_items");
            commandObject.add("info", infoObject);
            MainController.sendMessage(commandObject.toString());
        } catch (Exception ignored) {
        }
    }

    private void updateScoreboardItems(JsonObject infoObject) {
        JsonArray scoreboardItems = infoObject.get("scoreboard_items").getAsJsonArray();
        Type itemType = new TypeToken<ArrayList<ScoreboardItem>>() {
        }.getType();
        Gson gson = new Gson();
        ArrayList<ScoreboardItem> parsedItems = gson.fromJson(scoreboardItems, itemType);
        ObservableList<ScoreboardItem> scoreboardItemObservableList = FXCollections.observableArrayList();
        scoreboardItemObservableList.addAll(parsedItems);
        this.scoreboardItems = scoreboardItemObservableList;
    }


    public void parseCommand(JsonObject command) {
        try {
            String commandName = command.get("command_name").getAsString();
            JsonObject infoObject = command.get("info").getAsJsonObject();
            switch (commandName) {
                case "get_user_by_token_response":
                    updateUser(infoObject);
                    break;
                case "get_shop_items_response":
                    updateShopItems(infoObject);
                    break;
                case "get_decks_response":
                    updateDecks(infoObject);
                    break;
                case "get_deck_info_response":
                    updateDeckInfo(infoObject);
                    break;
                case "get_addable_cards_response":
                    updateAddableCards(infoObject);
                    break;
                case "get_scoreboard_items_response":
                    updateScoreboardItems(infoObject);
                    break;
            }
        } catch (Exception ignored) {
        }
    }


    private RuntimeTypeAdapterFactory<CardTemplate> getCardTemplateAdapter() {
        return RuntimeTypeAdapterFactory
                .of(CardTemplate.class, "card_template_type")
                .registerSubtype(MonsterTemplate.class, MonsterTemplate.class.getName())
                .registerSubtype(SpellTemplate.class, SpellTemplate.class.getName())
                .registerSubtype(TrapTemplate.class, TrapTemplate.class.getName());
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
