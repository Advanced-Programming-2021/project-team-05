package controller;

import model.Deck;
import model.User;
import model.card.Card;
import model.template.CardTemplate;

import java.util.ArrayList;

public class DataManager {

    private static DataManager dataManager;

    private final ArrayList<User> allUsers;
    private final ArrayList<Card> allCards;
    private final ArrayList<CardTemplate> allTemplates;
    private final ArrayList<Deck> allDecks;

    {
        allUsers = new ArrayList<>();
        allCards = new ArrayList<>();
        allTemplates = new ArrayList<>();
        allDecks = new ArrayList<>();
    }


    private DataManager() {}


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

    public Card getCardByUUID(String uuid) {
        for (Card card : this.allCards) {
            if (card.getId().equals(uuid)) {
                return card;
            }
        }

        return null;
    }

    public Deck getDeckByUUID(String uuid) {
        for (Deck deck : this.allDecks) {
            if (deck.getId().equals(uuid)) {
                return deck;
            }
        }

        return null;
    }

    public void addDeck(Deck deck){
        allDecks.add(deck);
    }

    public ArrayList<Deck> getAllDecks() {
        return this.allDecks;
    }

    public String getScoreboard() {
        return null;
    }

    public ArrayList<Card> getAllCards() {
        return this.allCards;
    }
}
