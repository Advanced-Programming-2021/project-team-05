package model;

import controller.DataManager;
import model.card.Card;

import java.util.ArrayList;

public class User {

    private final ArrayList<String> purchasedCards;
    private final ArrayList<String> decks;
    private String username;
    private String password;
    private String nickname;
    private String activeDeck;
    private int score;
    private int money;

    {
        purchasedCards = new ArrayList<>();
        decks = new ArrayList<>();
    }


    public User(String username, String password, String nickname) {
        this.setUsername(username);
        this.setPassword(password);
        this.setNickname(nickname);
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void increaseScore(int amount) {
        this.score += amount;
    }


    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void increaseMoney(int amount) {
        this.money += amount;
    }

    public void decreaseMoney(int amount) {
        this.money -= amount;
    }


    public ArrayList<Card> getAllCards() {
        ArrayList<Card> cards = new ArrayList<>();
        DataManager dataManager = DataManager.getInstance();
        for (String cardId : purchasedCards) {
            cards.add(dataManager.getCardByUUID(cardId));
        }

        return cards;
    }

    public void purchaseCard(Card card) {
        this.purchasedCards.add(card.getId());
    }

    public void removeCard(Card card) {
        this.purchasedCards.remove(card.getId());
    }

    public ArrayList<Card> getPurchasedCardsByName(String name) {
        ArrayList<Card> purchasedCards = new ArrayList<>();
        DataManager dataManager = DataManager.getInstance();

        for (String id : this.purchasedCards) {
            Card card = dataManager.getCardByUUID(id);
            if (name.equals(card.getName())) {
                purchasedCards.add(card);
            }
        }

        return purchasedCards;
    }


    public ArrayList<Deck> getAllDecks() {
        ArrayList<Deck> allDecks = new ArrayList<>();
        DataManager dataManager = DataManager.getInstance();
        for (String deckId : this.decks) {
            allDecks.add(dataManager.getDeckByUUID(deckId));
        }

        return allDecks;
    }

    public void addDeck(Deck deck) {
        this.decks.add(deck.getId());
    }

    public void removeDeck(Deck deck) {
        this.decks.remove(deck.getId());
    }

    public Deck getDeckByName(String name) {
        DataManager dataManager = DataManager.getInstance();

        for (String id : this.decks) {
            Deck deck = dataManager.getDeckByUUID(id);
            if (name.equals(deck.getName())) {
                return deck;
            }
        }

        return null;
    }


    public Deck getActiveDeck() {
        return DataManager.getInstance().getDeckByUUID(this.activeDeck);
    }

    public void setActiveDeck(Deck activeDeck) {
        this.activeDeck = activeDeck.getId();
    }


    @Override
    public String toString() {
        return this.getUsername();
    }
}
