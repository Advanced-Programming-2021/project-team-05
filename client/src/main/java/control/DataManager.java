package control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import model.Deck;
import model.User;
import model.card.Card;
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

public class DataManager {

    private static final String MONSTER_CSV_PATH = "data" + File.separator + "Monster.csv";
    private static final String SPELL_TRAP_CSV_PATH = "data" + File.separator + "SpellTrap.csv";
    private static final String IMPORT_EXPORT_DIR = "import_export";

    private static DataManager dataManager;
    private final ArrayList<CardTemplate> templates;
    private User ai;
    private ArrayList<User> users;
    private ArrayList<Card> cards;
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


    public User getAi() {
        return this.ai;
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

    public void addTemplate(CardTemplate template) {
        this.templates.add(template);
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


    public void checkTemplate(CardTemplate template, boolean add) throws Exception {
        String[] line;
        String path;
        if (template instanceof MonsterTemplate) {
            path = MONSTER_CSV_PATH;
            line = getCSVLineMonster(template);
        } else {
            path = SPELL_TRAP_CSV_PATH;
            line = getCSVLineSpellTrap(template);
        }

        if (add) {
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(path, true), ',', CSVWriter.NO_QUOTE_CHARACTER, ICSVWriter.DEFAULT_ESCAPE_CHARACTER, ICSVWriter.RFC4180_LINE_END);
                writer.writeNext(line);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String[] getCSVLineMonster(CardTemplate template) throws Exception {
        if (template.getEffects() == null) {
            throw new Exception("Invalid File");
        }
        String[] line = new String[9];
        line[0] = template.getName();
        if (getCardTemplateByName(line[0]) != null) throw new Exception("Template Exists");
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

    private String[] getCSVLineSpellTrap(CardTemplate template) throws Exception {
        if (template.getEffects() == null) {
            throw new NullPointerException();
        }
        String[] line = new String[6];
        line[0] = template.getName();
        if (getCardTemplateByName(line[0]) != null) throw new Exception("Template Exists");
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


    public boolean importCard(File file, Type type, boolean addToCSV) {
        try {
            Gson gson = new GsonBuilder().serializeNulls().create();
            JsonReader reader = new JsonReader(new FileReader(file));
            CardTemplate template = gson.fromJson(reader, type);
            reader.close();
            this.checkTemplate(template, addToCSV);
            this.templates.add(template);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void exportCard(CardTemplate cardTemplate) {
        try {
            Type type;
            if (cardTemplate instanceof MonsterTemplate) {
                type = MonsterTemplate.class;
            } else if (cardTemplate instanceof SpellTemplate) {
                type = SpellTemplate.class;
            } else {
                type = TrapTemplate.class;
            }

            String path = IMPORT_EXPORT_DIR + "\\" + cardTemplate.getName().replaceAll("\\s", "_") + ".json";
            Gson gson = new Gson();
            FileWriter writer = new FileWriter(path);
            gson.toJson(cardTemplate, type, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
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
            csvReader.close();
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
            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        templates.clear();
        loadMonsterTemplatesFromCSV();
        loadSpellTrapTemplatesFromCSV();
    }
}
