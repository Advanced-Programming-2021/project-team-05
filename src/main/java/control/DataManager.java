package control;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import model.Deck;
import model.User;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.effect.Effect;
import model.effect.Event;
import model.effect.action.ActionEnum;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import model.template.property.CardType;
import model.template.property.MonsterAttribute;
import model.template.property.MonsterType;
import model.template.property.SpellTrapStatus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;

public class DataManager {

    private static final String USERS_JSON_PATH = "data\\users.json";
    private static final String CARDS_JSON_PATH = "data\\cards.json";
    private static final String DECKS_JSON_PATH = "data\\decks.json";
    private static final String EFFECTS_JSON_PATH = "data\\effects.json";
    private static final String MONSTER_CSV_PATH = "data\\Monster.csv";
    private static final String SPELL_TRAP_CSV_PATH = "data\\SpellTrap.csv";

    private static DataManager dataManager;

    private ArrayList<User> users;
    private ArrayList<Card> cards;
    private final ArrayList<CardTemplate> templates;
    private ArrayList<Deck> decks;

    {
        users = new ArrayList<>();
        cards = new ArrayList<>();
        templates = new ArrayList<>();
        decks = new ArrayList<>();
    }


    private DataManager() {
    }


    public static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }

        return dataManager;
    }


    public ArrayList<User> getUsers() {
        return this.users;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    public User getUserByUsername(String username) {
        for (User user : this.users) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    public User getUserByNickname(String nickname) {
        for (User user : this.users) {
            if (nickname.equals(user.getNickname())) {
                return user;
            }
        }
        return null;
    }

    private void sortUsers() {
        this.users.sort(Comparator
                .comparing(User::getScore, Comparator.reverseOrder())
                .thenComparing(User::getNickname));
    }


    public ArrayList<CardTemplate> getCardTemplates() {
        return this.templates;
    }

    public CardTemplate getCardTemplateByName(String name) {
        for (CardTemplate template : this.templates) {
            if (name.equals(template.getName())) {
                return template;
            }
        }
        return null;
    }


    public ArrayList<Deck> getDecks() {
        return this.decks;
    }

    public void addDeck(Deck deck) {
        this.decks.add(deck);
    }

    public Deck getDeckById(String id) {
        for (Deck deck : this.decks) {
            if (deck.getId().equals(id)) {
                return deck;
            }
        }
        return null;
    }

    public void removeDeck(Deck deck) {
        this.decks.remove(deck);
    }


    public ArrayList<Card> getCards() {
        return this.cards;
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public Card getCardById(String id) {
        for (Card card : this.cards) {
            if (card.getId().equals(id)) {
                return card;
            }
        }
        return null;
    }


    public String getScoreboard() {
        sortUsers();
        StringBuilder scoreboardString = new StringBuilder();
        for (int i = 0, rank = 1, size = this.users.size(); i < size; i++) {
            User user = this.users.get(i);
            scoreboardString.append(rank).append(". ")
                    .append(user.getNickname()).append(": ")
                    .append(user.getScore()).append("\r\n");
            if (i < size - 1 && user.getScore() != this.users.get(i + 1).getScore()) {
                rank = i + 2;
            }
        }
        scoreboardString.delete(scoreboardString.length() - 2, scoreboardString.length());
        return scoreboardString.toString();
    }


    private RuntimeTypeAdapterFactory<Card> getCardAdapter() {
        return RuntimeTypeAdapterFactory
                .of(Card.class, "card_type")
                .registerSubtype(Monster.class, MonsterTemplate.class.getName())
                .registerSubtype(Spell.class, SpellTemplate.class.getName())
                .registerSubtype(Trap.class, TrapTemplate.class.getName());
    }


    private void loadUsers() {
        try {
            Gson gson = new Gson();
            JsonReader userReader = new JsonReader(new FileReader(USERS_JSON_PATH));
            Type userType = new TypeToken<ArrayList<User>>() {
            }.getType();
            this.users = gson.fromJson(userReader, userType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCards() {
        try {
            RuntimeTypeAdapterFactory<Card> cardAdapter = getCardAdapter();
            Gson cardGson = new GsonBuilder().registerTypeAdapterFactory(cardAdapter).create();
            JsonReader cardReader = new JsonReader(new FileReader(CARDS_JSON_PATH));
            Type cardType = new TypeToken<ArrayList<Card>>() {
            }.getType();
            this.cards = cardGson.fromJson(cardReader, cardType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadDecks() {
        try {
            Gson gson = new Gson();
            JsonReader deckReader = new JsonReader(new FileReader(DECKS_JSON_PATH));
            Type deckType = new TypeToken<ArrayList<Deck>>() {
            }.getType();
            this.decks = gson.fromJson(deckReader, deckType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadEffects() {
        try {
            JsonParser parser = new JsonParser();
            JsonReader effectReader = new JsonReader(new FileReader(EFFECTS_JSON_PATH));
            JsonArray effectsArray = parser.parse(effectReader).getAsJsonArray();
            for (JsonElement effectElement : effectsArray) {
                JsonObject effectObject = effectElement.getAsJsonObject();
                String cardName = effectObject.get("cardName").getAsString();
                Event event = Event.valueOf(effectObject.get("event").getAsString());
                ActionEnum action = ActionEnum.valueOf(effectObject.get("action").getAsString());

                Effect effect = new Effect(event, action);
                this.getCardTemplateByName(cardName).addEffect(effect);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        templates.clear();
        loadUsers();
        loadMonsterTemplatesFromCSV();
        loadSpellTrapTemplatesFromCSV();
        loadCards();
        loadDecks();
        loadEffects();
    }


    private void saveUsers() {
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            FileWriter userWriter = new FileWriter(USERS_JSON_PATH);
            gson.toJson(this.users, userWriter);
            userWriter.flush();
            userWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public void saveData() {
        saveUsers();
        saveCards();
        saveDecks();
    }


    public void addTemplateToCSV(CardTemplate template) {
        String[] line;
        String path;
        if (template instanceof MonsterTemplate) {
            path = MONSTER_CSV_PATH;
            line = getCSVLineMonster(template);
        } else {
            path = SPELL_TRAP_CSV_PATH;
            line = getCSVLineSpellTrap(template);
        }

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(path, true));
            writer.writeNext(line);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] getCSVLineMonster(CardTemplate template) {
        String[] line = new String[9];
        line[0] = template.getName();
        line[1] = String.valueOf(((MonsterTemplate) template).getLevel());
        line[2] = ((MonsterTemplate) template).getAttribute().getName();
        line[3] = ((MonsterTemplate) template).getMonsterType().getName();
        line[4] = template.getType().getName();
        line[5] = String.valueOf(((MonsterTemplate) template).getAttack());
        line[6] = String.valueOf(((MonsterTemplate) template).getDefence());
        line[7] = template.getDescription();
        line[8] = String.valueOf(template.getPrice());
        return line;
    }

    private String[] getCSVLineSpellTrap(CardTemplate template) {
        String[] line = new String[6];
        line[0] = template.getName();
        line[2] = template.getType().getName();
        line[3] = template.getDescription();
        line[5] = String.valueOf(template.getPrice());

        if (template instanceof SpellTemplate) {
            line[1] = "Spell";
            line[4] = ((SpellTemplate) template).getStatus().getName();
        } else if (template instanceof TrapTemplate) {
            line[1] = "Trap";
            line[4] = ((TrapTemplate) template).getStatus().getName();
        }
        return line;
    }
}
