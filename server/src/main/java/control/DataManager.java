package control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import control.controller.ChatRoomController;
import control.controller.ScoreboardMenuController;
import model.Deck;
import model.Message;
import model.ScoreboardItem;
import model.User;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import model.template.property.CardType;
import model.template.property.MonsterAttribute;
import model.template.property.MonsterType;
import model.template.property.SpellTrapStatus;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class DataManager {

    private static final String USERS_JSON_PATH = "data" + File.separator + "users.json";
    private static final String CARDS_JSON_PATH = "data" + File.separator + "cards.json";
    private static final String DECKS_JSON_PATH = "data" + File.separator + "decks.json";
    private static final String MESSAGES_JSON_PATH = "data" + File.separator + "messages.json";
    private static final String MONSTER_CSV_PATH = "data" + File.separator + "Monster.csv";
    private static final String SPELL_TRAP_CSV_PATH = "data" + File.separator + "SpellTrap.csv";

    private static DataManager dataManager;
    private final ArrayList<User> users;
    private final ArrayList<Deck> decks;
    private final ArrayList<CardTemplate> templates;
    private final ArrayList<Card> cards;
    private final ArrayList<Message> messages;
    private final HashMap<String, String> loggedInUsers;

    {
        users = new ArrayList<>();
        decks = new ArrayList<>();
        templates = new ArrayList<>();
        cards = new ArrayList<>();
        messages = new ArrayList<>();
        loggedInUsers = new HashMap<>();
    }


    private DataManager() {
        Thread saveThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException ignored) {
                }
                saveData();
            }
        });
        saveThread.start();
    }


    public static DataManager getInstance() {
        if (dataManager == null) dataManager = new DataManager();
        return dataManager;
    }


    public void addUser(User user) {
        synchronized (this.users) {
            this.users.add(user);
        }
        ScoreboardMenuController.refreshScoreboard();
    }

    public User getUserByUsername(String username) {
        synchronized (this.users) {
            for (User user : this.users) {
                if (username.equals(user.getUsername())) {
                    return user;
                }
            }
            return null;
        }
    }

    public User getUserByNickname(String nickname) {
        synchronized (this.users) {
            for (User user : this.users) {
                if (nickname.equals(user.getNickname())) {
                    return user;
                }
            }
            return null;
        }
    }

    private void sortUsers() {
        synchronized (this.users) {
            this.users.sort(Comparator
                    .comparing(User::getScore, Comparator.reverseOrder())
                    .thenComparing(User::getNickname));
        }
    }


    public void addDeck(Deck deck) {
        synchronized (this.decks) {
            this.decks.add(deck);
        }
    }

    public Deck getDeckById(String id) {
        synchronized (this.decks) {
            for (Deck deck : this.decks) {
                if (deck.getId().equals(id)) {
                    return deck;
                }
            }
            return null;
        }
    }

    public void removeDeck(Deck deck) {
        synchronized (this.decks) {
            this.decks.remove(deck);
        }
    }


    public CardTemplate getCardTemplateByName(String name) {
        synchronized (this.templates) {
            for (CardTemplate template : this.templates) {
                if (name.equals(template.getName())) {
                    return template;
                }
            }
            return null;
        }
    }


    public void addCard(Card card) {
        synchronized (this.cards) {
            this.cards.add(card);
        }
    }

    public Card getCardById(String id) {
        synchronized (this.cards) {
            for (Card card : this.cards) {
                if (card.getId().equals(id)) {
                    return card;
                }
            }
            return null;
        }
    }


    public void addMessage(Message message) {
        synchronized (this.messages) {
            this.messages.add(message);
        }
        ChatRoomController.updateMessages();
    }

    public void removeMessage(Message message) {
        synchronized (this.messages) {
            this.messages.remove(message);
        }
        ChatRoomController.updateMessages();
    }

    public Message getMessageById(String id) {
        synchronized (this.messages) {
            for (Message message : this.messages) {
                if (id.equals(message.getId())) return message;
            }
            return null;
        }
    }


    public void addLoggedInUser(String token, String username) {
        synchronized (this.loggedInUsers) {
            this.loggedInUsers.put(token, username);
        }
        ScoreboardMenuController.refreshScoreboard();
    }

    public void removeLoggedInUser(String token) {
        synchronized (this.loggedInUsers) {
            this.loggedInUsers.remove(token);
        }
        ScoreboardMenuController.refreshScoreboard();
    }

    public User getUserByToken(String token) {
        synchronized (this.loggedInUsers) {
            String username = this.loggedInUsers.get(token);
            if (username == null) return null;
            return getUserByUsername(username);
        }
    }

    public boolean isOnline(User user) {
        synchronized (this.loggedInUsers) {
            return this.loggedInUsers.containsValue(user.getUsername());
        }
    }


    private void loadUsers() {
        try {
            Gson gson = new Gson();
            JsonReader userReader = new JsonReader(new FileReader(USERS_JSON_PATH));
            Type userType = new TypeToken<ArrayList<User>>() {
            }.getType();
            this.users.clear();
            this.users.addAll(gson.fromJson(userReader, userType));
            userReader.close();
        } catch (IOException e) {
            System.out.println("Failed to load users");
        }
    }

    public void loadMonsterTemplatesFromCSV() {
        try {
            CSVReader csvReader = new CSVReaderBuilder(new FileReader(MONSTER_CSV_PATH)).withSkipLines(1).build();
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String name = nextLine[0];
                int level = Integer.parseInt(nextLine[1]);
                MonsterAttribute attribute = MonsterAttribute.getMonsterAttributeByName(nextLine[2]);
                MonsterType monsterType = MonsterType.getMonsterTypeByName(nextLine[3]);
                CardType type = CardType.getTypeByName(nextLine[4]);
                int attack = Integer.parseInt(nextLine[5]);
                int defense = Integer.parseInt(nextLine[6]);
                String description = nextLine[7];
                int price = Integer.parseInt(nextLine[8]);

                if (attribute == null || monsterType == null || type == null) {
                    throw new Exception("error at " + nextLine[2] + "|" + nextLine[3] + "|" + nextLine[4]);
                }

                this.templates.add(new MonsterTemplate(name, type, description, price, monsterType, attribute, level, attack, defense));
            }
            csvReader.close();
        } catch (Exception e) {
            System.out.println("Failed to load monster templates");
        }
    }

    public void loadSpellTrapTemplatesFromCSV() {
        try {
            CSVReader csvReader = new CSVReaderBuilder(new FileReader(SPELL_TRAP_CSV_PATH)).withSkipLines(1).build();
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String name = nextLine[0];
                String cardType = nextLine[1];
                CardType type = CardType.getTypeByName(nextLine[2]);
                String description = nextLine[3];
                SpellTrapStatus status = SpellTrapStatus.getStatusByName(nextLine[4]);
                int price = Integer.parseInt(nextLine[5]);

                if (type == null) {
                    throw new Exception("error at " + nextLine[2]);
                }

                if ("Spell".equals(cardType)) {
                    this.templates.add(new SpellTemplate(name, type, description, price, status));
                } else if ("Trap".equals(cardType)) {
                    this.templates.add(new TrapTemplate(name, type, description, price, status));
                } else {
                    throw new Exception("card type wasn't Spell or Trap");
                }
            }
            csvReader.close();
        } catch (Exception e) {
            System.out.println("Failed to load spell and trap templates");
        }
    }

    private void loadCards() {
        try {
            RuntimeTypeAdapterFactory<Card> cardAdapter = getCardAdapter();
            Gson cardGson = new GsonBuilder().registerTypeAdapterFactory(cardAdapter).create();
            JsonReader cardReader = new JsonReader(new FileReader(CARDS_JSON_PATH));
            Type cardType = new TypeToken<ArrayList<Card>>() {
            }.getType();
            this.cards.clear();
            this.cards.addAll(cardGson.fromJson(cardReader, cardType));
            cardReader.close();
        } catch (IOException e) {
            System.out.println("Failed to load cards");
        }
    }

    private void loadDecks() {
        try {
            Gson gson = new Gson();
            JsonReader deckReader = new JsonReader(new FileReader(DECKS_JSON_PATH));
            Type deckType = new TypeToken<ArrayList<Deck>>() {
            }.getType();
            this.decks.clear();
            this.decks.addAll(gson.fromJson(deckReader, deckType));
            deckReader.close();
        } catch (IOException e) {
            System.out.println("Failed to load decks");
        }
    }

    private void loadMessages() {
        try {
            Gson gson = new Gson();
            JsonReader messagesReader = new JsonReader(new FileReader(MESSAGES_JSON_PATH));
            Type messageType = new TypeToken<ArrayList<Message>>() {
            }.getType();
            this.messages.clear();
            this.messages.addAll(gson.fromJson(messagesReader, messageType));
            messagesReader.close();
        } catch (IOException e) {
            System.out.println("Failed to load messages");
        }
    }

    public void loadData() {
        templates.clear();
        loadUsers();
        loadMonsterTemplatesFromCSV();
        loadSpellTrapTemplatesFromCSV();
        loadCards();
        loadDecks();
        loadMessages();
    }


    private void saveUsers() {
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            FileWriter userWriter = new FileWriter(USERS_JSON_PATH);
            gson.toJson(this.users, userWriter);
            userWriter.flush();
            userWriter.close();
        } catch (IOException e) {
            System.out.println("Failed to save users");
        }
    }

    private void saveCards() {
        try {
            RuntimeTypeAdapterFactory<Card> cardAdapter = getCardAdapter();
            Gson cardGson = new GsonBuilder().serializeNulls().registerTypeAdapterFactory(cardAdapter).create();
            FileWriter cardsWriter = new FileWriter(CARDS_JSON_PATH);
            Type type = new TypeToken<ArrayList<Card>>() {
            }.getType();
            cardGson.toJson(this.cards, type, cardsWriter);
            cardsWriter.flush();
            cardsWriter.close();
        } catch (IOException e) {
            System.out.println("Failed to save cards");
        }
    }

    private void saveDecks() {
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            FileWriter decksWriter = new FileWriter(DECKS_JSON_PATH);
            gson.toJson(this.decks, decksWriter);
            decksWriter.flush();
            decksWriter.close();
        } catch (IOException e) {
            System.out.println("Failed to save decks");
        }
    }

    private void saveMessages() {
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            FileWriter messagesWriter = new FileWriter(MESSAGES_JSON_PATH);
            gson.toJson(this.messages, messagesWriter);
            messagesWriter.flush();
            messagesWriter.close();
        } catch (IOException e) {
            System.out.println("Failed to save messages");
        }
    }

    public void saveData() {
        saveUsers();
        saveCards();
        saveDecks();
        saveMessages();
    }


    public JsonObject getShopItems(User user) {
        RuntimeTypeAdapterFactory<CardTemplate> cardTemplateAdapter = dataManager.getCardTemplateAdapter();
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(cardTemplateAdapter).create();
        Type templateType = CardTemplate.class;

        JsonArray templatesArray = new JsonArray();
        JsonArray purchasedArray = new JsonArray();
        for (CardTemplate template : this.templates) {
            templatesArray.add(gson.toJsonTree(template, templateType));
            purchasedArray.add(user.getPurchasedCardsByName(template.getName()).size());
        }
        JsonObject shopItems = new JsonObject();
        shopItems.add("templates", templatesArray);
        shopItems.add("purchased_counts", purchasedArray);
        return shopItems;
    }

    public JsonObject getDeckInfo(Deck deck) {
        JsonObject deckObject = new Gson().toJsonTree(deck).getAsJsonObject();
        JsonArray mainDeckCards = parseToJsonArray(deck.getMainDeck());
        JsonArray sideDeckCards = parseToJsonArray(deck.getSideDeck());
        JsonObject deckInfo = new JsonObject();
        deckInfo.add("deck", deckObject);
        deckInfo.add("main_deck", mainDeckCards);
        deckInfo.add("side_deck", sideDeckCards);
        return deckInfo;
    }

    public JsonArray getUserDecks(User user) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJsonTree(user.getDecks()).getAsJsonArray();
    }

    public JsonArray getAddableCards(Deck deck, User user) {
        ArrayList<Card> addableCards = deck.getAddableCards(user.getPurchasedCards());
        return parseToJsonArray(addableCards);
    }

    public ArrayList<ScoreboardItem> getScoreboardItems(User user) {
        sortUsers();
        ArrayList<ScoreboardItem> scoreboardItems = new ArrayList<>();
        synchronized (this.users) {
            for (int i = 0, rank = 1, size = this.users.size(); i < size; i++) {
                User currentUser = this.users.get(i);
                String rankString = String.valueOf(rank);
                String scoreString = String.valueOf(currentUser.getScore());
                boolean isCurrentUser = user.equals(currentUser);
                boolean isOnline = this.isOnline(currentUser);
                scoreboardItems.add(new ScoreboardItem(rankString, currentUser.getNickname(), scoreString, isCurrentUser, isOnline));
                if (i < size - 1 && currentUser.getScore() != this.users.get(i + 1).getScore()) rank = i + 2;
            }
        }
        for (int i = scoreboardItems.size(); i < 20; i++) {
            scoreboardItems.add(new ScoreboardItem("-", "-", "-", false, false));
        }
        return scoreboardItems;
    }

    public JsonArray getMessagesArray() {
        Gson gson = new Gson();
        synchronized (this.messages) {
            return gson.toJsonTree(messages).getAsJsonArray();
        }
    }


    private RuntimeTypeAdapterFactory<Card> getCardAdapter() {
        return RuntimeTypeAdapterFactory
                .of(Card.class, "card_type")
                .registerSubtype(Monster.class, MonsterTemplate.class.getName())
                .registerSubtype(Spell.class, SpellTemplate.class.getName())
                .registerSubtype(Trap.class, TrapTemplate.class.getName());
    }

    private RuntimeTypeAdapterFactory<CardTemplate> getCardTemplateAdapter() {
        return RuntimeTypeAdapterFactory
                .of(CardTemplate.class, "card_template_type")
                .registerSubtype(MonsterTemplate.class, MonsterTemplate.class.getName())
                .registerSubtype(SpellTemplate.class, SpellTemplate.class.getName())
                .registerSubtype(TrapTemplate.class, TrapTemplate.class.getName());
    }

    private JsonArray parseToJsonArray(ArrayList<Card> cards) {
        RuntimeTypeAdapterFactory<Card> cardAdapter = getCardAdapter();
        Gson cardGson = new GsonBuilder().serializeNulls().registerTypeAdapterFactory(cardAdapter).create();
        Type type = new TypeToken<ArrayList<Card>>() {
        }.getType();
        return cardGson.toJsonTree(cards, type).getAsJsonArray();
    }
}
