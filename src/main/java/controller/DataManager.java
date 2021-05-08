package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import model.template.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DataManager {

    private static DataManager dataManager;

    private ArrayList<User> allUsers;
    private ArrayList<Card> allCards;
    private ArrayList<CardTemplate> allTemplates;
    private ArrayList<Deck> allDecks;

    {
        allUsers = new ArrayList<>();
        allCards = new ArrayList<>();
        allTemplates = new ArrayList<>();
        allDecks = new ArrayList<>();
    }


    private DataManager() {
    }


    public ArrayList<Card> getAllCards() {
        return this.allCards;
    }

    public ArrayList<Deck> getAllDecks() {
        return this.allDecks;
    }

    public static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }

        return dataManager;
    }


    public ArrayList<User> getAllUsers() {
        return this.allUsers;
    }

    public void addUser(User user) {
        this.allUsers.add(user);
    }

    public User getUserByUsername(String username) {
        for (User user : this.allUsers) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }

        return null;
    }

    public User getUserByNickname(String nickname) {
        for (User user : this.allUsers) {
            if (nickname.equals(user.getNickname())) {
                return user;
            }
        }

        return null;
    }


    public ArrayList<CardTemplate> getCardTemplates() {
        return this.allTemplates;
    }

    public CardTemplate getCardTemplateByName(String name) {
        for (CardTemplate template : this.allTemplates) {
            if (name.equals(template.getName())) {
                return template;
            }
        }

        return null;
    }


    public void addDeck(Deck deck) {
        this.allDecks.add(deck);
    }

    public Deck getDeckByUUID(String uuid) {
        for (Deck deck : this.allDecks) {
            if (deck.getId().equals(uuid)) {
                return deck;
            }
        }

        return null;
    }

    public void removeDeck(Deck deck) {
        this.allDecks.remove(deck);
    }


    public void addCard(Card card) {
        this.allCards.add(card);
    }

    public Card getCardByUUID(String uuid) {
        for (Card card : this.allCards) {
            if (card.getId().equals(uuid)) {
                return card;
            }
        }

        return null;
    }


    public String getScoreboard() {
        return null;
    }


    public void createDataFolder() {
        String path = "data";
        File file = new File(path);
        if (!Files.exists(Paths.get(path))) {
            if (!file.mkdir()) {
                System.out.println("could not create data folder");
            }
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
                .of(CardTemplate.class, "template_type")
                .registerSubtype(MonsterTemplate.class, MonsterTemplate.class.getName())
                .registerSubtype(SpellTemplate.class, SpellTemplate.class.getName())
                .registerSubtype(TrapTemplate.class, TrapTemplate.class.getName());
    }


    private void loadUsers() {
        try {
            Gson gson = new Gson();
            JsonReader userReader = new JsonReader(new FileReader("data\\users.json"));
            Type userType = new TypeToken<ArrayList<User>>() {
            }.getType();
            this.allUsers = gson.fromJson(userReader, userType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadTemplates() {
        try {
            RuntimeTypeAdapterFactory<CardTemplate> templateAdapter = getCardTemplateAdapter();
            Gson templateGson = new GsonBuilder().registerTypeAdapterFactory(templateAdapter).create();
            JsonReader templateReader = new JsonReader(new FileReader("data\\templates.json"));
            Type templateType = new TypeToken<ArrayList<CardTemplate>>() {
            }.getType();
            this.allTemplates = templateGson.fromJson(templateReader, templateType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadMonsterTemplatesFromCSV() {
        try {
            CSVReader csvReader = new CSVReaderBuilder(new FileReader("data\\Monster.csv")).withSkipLines(1).build();

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

                dataManager.allTemplates.add(new MonsterTemplate(name, type, description, price, monsterType, attribute, level, attack, defense));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSpellTrapTemplatesFromCSV() {
        try {
            CSVReader csvReader = new CSVReaderBuilder(new FileReader("data\\SpellTrap.csv")).withSkipLines(1).build();

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
                    allTemplates.add(new SpellTemplate(name, type, description, price, status));
                } else if ("Trap".equals(cardType)) {
                    allTemplates.add(new TrapTemplate(name, type, description, price, status));
                } else {
                    throw new Exception("Spell Trap");
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
            JsonReader cardReader = new JsonReader(new FileReader("data\\cards.json"));
            Type cardType = new TypeToken<ArrayList<Card>>() {
            }.getType();
            this.allCards = cardGson.fromJson(cardReader, cardType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadDecks() {
        try {
            Gson gson = new Gson();
            JsonReader deckReader = new JsonReader(new FileReader("data\\decks.json"));
            Type deckType = new TypeToken<ArrayList<Deck>>() {
            }.getType();
            this.allDecks = gson.fromJson(deckReader, deckType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        loadUsers();
        loadMonsterTemplatesFromCSV();
        loadSpellTrapTemplatesFromCSV();
        loadCards();
        loadDecks();
    }


    private void saveUsers() {
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            FileWriter userWriter = new FileWriter("data\\users.json");
            gson.toJson(this.allUsers, userWriter);
            userWriter.flush();
            userWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCardTemplates() {
        try {
            RuntimeTypeAdapterFactory<CardTemplate> templateAdapter = getCardTemplateAdapter();
            Gson templateGson = new GsonBuilder().serializeNulls().registerTypeAdapterFactory(templateAdapter).create();
            FileWriter templatesWriter = new FileWriter("data\\templates.json");
            Type type = new TypeToken<ArrayList<CardTemplate>>() {
            }.getType();
            templateGson.toJson(this.allTemplates, type, templatesWriter);
            templatesWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCards() {
        try {
            RuntimeTypeAdapterFactory<Card> cardAdapter = getCardAdapter();
            Gson cardGson = new GsonBuilder().serializeNulls().registerTypeAdapterFactory(cardAdapter).create();
            FileWriter cardsWriter = new FileWriter("data\\cards.json");
            Type type = new TypeToken<ArrayList<Card>>() {
            }.getType();
            cardGson.toJson(this.allCards, type, cardsWriter);
            cardsWriter.flush();
            cardsWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDecks() {
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            FileWriter decksWriter = new FileWriter("data\\decks.json");
            gson.toJson(this.allDecks, decksWriter);
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
            path = "data\\Monster.csv";
            line = new String[9];
            line[0] = template.getName();
            line[1] = String.valueOf(((MonsterTemplate) template).getLevel());
            line[2] = ((MonsterTemplate) template).getAttribute().getName();
            line[3] = ((MonsterTemplate) template).getMonsterType().getName();
            line[4] = template.getType().getName();
            line[5] = String.valueOf(((MonsterTemplate) template).getAttack());
            line[6] = String.valueOf(((MonsterTemplate) template).getDefence());
            line[7] = template.getDescription();
            line[8] = String.valueOf(template.getPrice());
        } else {
            path = "data\\SpellTrap.csv";
            line = new String[6];
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
        }

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(path, true));
            writer.writeNext(line);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
