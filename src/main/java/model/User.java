package model;

import control.DataManager;
import model.card.Card;

import java.util.ArrayList;
import java.util.Objects;

public class User {

    private final ArrayList<String> purchasedCardsIds;
    private final ArrayList<String> decksIds;
    private String username;
    private String password;
    private String nickname;
    private String activeDeckId;
    private int score;
    private long money;

    {
        purchasedCardsIds = new ArrayList<>();
        decksIds = new ArrayList<>();
    }


    public User(String username, String password, String nickname) {
        this.setUsername(username);
        this.setPassword(password);
        this.setNickname(nickname);
        this.setScore(0);
        this.setMoney(100000);
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

    public void increaseScore(int amount) {
        this.score += amount;
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
        for (String cardId : purchasedCardsIds) {
            cards.add(dataManager.getCardById(cardId));
        }
        return cards;
    }

    public void purchaseCard(Card card) {
        this.purchasedCardsIds.add(card.getId());
    }

    public void removeCard(Card card) {
        this.purchasedCardsIds.remove(card.getId());
    }

    public ArrayList<Card> getPurchasedCardsByName(String name) {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<Card> purchasedCards = new ArrayList<>();
        for (String cardId : this.purchasedCardsIds) {
            Card card = dataManager.getCardById(cardId);
            if (name.equals(card.getName())) {
                purchasedCards.add(card);
            }
        }
        return purchasedCards;
    }


    public ArrayList<Deck> getDecks() {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<Deck> allDecks = new ArrayList<>();
        for (String deckId : this.decksIds) {
            allDecks.add(dataManager.getDeckById(deckId));
        }
        return allDecks;
    }

    public void addDeck(Deck deck) {
        this.decksIds.add(deck.getId());
    }

    public void removeDeck(Deck deck) {
        this.decksIds.remove(deck.getId());
    }

    public Deck getDeckByName(String name) {
        DataManager dataManager = DataManager.getInstance();
        for (String deckId : this.decksIds) {
            Deck deck = dataManager.getDeckById(deckId);
            if (name.equals(deck.getName())) {
                return deck;
            }
        }
        return null;
    }


    public Deck getActiveDeck() {
        return DataManager.getInstance().getDeckById(this.activeDeckId);
    }

    public void setActiveDeck(Deck activeDeck) {
        this.activeDeckId = activeDeck.getId();
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;
        User user = (User) object;
        return this.getUsername().equals(user.getUsername());
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.getUsername());
    }


    @Override
    public String toString() {
        return this.getUsername();
    }
}
