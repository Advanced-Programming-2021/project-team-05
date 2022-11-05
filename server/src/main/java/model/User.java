package model;

import control.DataManager;
import model.card.Card;

import java.util.ArrayList;
import java.util.Random;

public class User {

    private final ArrayList<String> purchasedCardIds;
    private final ArrayList<String> deckIds;
    private String username;
    private String password;
    private String nickname;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String activeDeckId;
    private int score;
    private long money;
    private String profilePictureName;

    {
        purchasedCardIds = new ArrayList<>();
        deckIds = new ArrayList<>();
    }


    public User(String username, String password, String nickname) {
        this.setUsername(username);
        this.setPassword(password);
        this.setNickname(nickname);
        this.setScore(0);
        this.setMoney(200000);
        this.setProfilePictureName("profile-pic" + (new Random().nextInt(37) + 1) + ".png");
    }


    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public long getMoney() {
        return this.money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public void increaseMoney(long amount) {
        this.money += amount;
    }

    public void decreaseMoney(long amount) {
        this.money -= amount;
    }


    public ArrayList<Card> getPurchasedCards() {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<Card> cards = new ArrayList<>();
        for (String cardId : purchasedCardIds) {
            cards.add(dataManager.getCardById(cardId));
        }
        return cards;
    }

    public void purchaseCard(Card card) {
        this.purchasedCardIds.add(card.getId());
    }

    public ArrayList<Card> getPurchasedCardsByName(String name) {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<Card> cards = new ArrayList<>();
        for (String cardId : this.purchasedCardIds) {
            Card card = dataManager.getCardById(cardId);
            if (name.equals(card.getName())) {
                cards.add(card);
            }
        }
        return cards;
    }


    public ArrayList<Deck> getDecks() {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<Deck> decks = new ArrayList<>();
        for (String deckId : this.deckIds) {
            decks.add(dataManager.getDeckById(deckId));
        }
        return decks;
    }

    public void addDeck(Deck deck) {
        this.deckIds.add(deck.getId());
    }

    public void removeDeck(Deck deck) {
        this.deckIds.remove(deck.getId());
    }

    public Deck getDeckByName(String name) {
        DataManager dataManager = DataManager.getInstance();
        for (String deckId : this.deckIds) {
            Deck deck = dataManager.getDeckById(deckId);
            if (name.equals(deck.getName())) {
                return deck;
            }
        }
        return null;
    }


    public void setActiveDeck(Deck activeDeck) {
        this.activeDeckId = activeDeck.getId();
    }


    public String getProfilePictureName() {
        return this.profilePictureName;
    }

    public void setProfilePictureName(String profilePictureName) {
        this.profilePictureName = profilePictureName;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;
        User user = (User) object;
        return this.getUsername().equals(user.getUsername());
    }


    @Override
    public String toString() {
        return this.getUsername();
    }
}
