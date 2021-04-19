package model;

import controller.DataManager;
import model.card.Card;

import javax.lang.model.type.ArrayType;
import java.util.ArrayList;

public class User {

    private String username;
    private String password;
    private String nickname;
    private String activeDeck;
    private int score;
    private int money;
    private ArrayList<String > purchasedCards = new ArrayList<>();
    private ArrayList<String > decks = new ArrayList<>();

    public User(String username , String password , String nickname){
        this.username = username;
        this.password = password;
        this.nickname = nickname;
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

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void increaseScore(int amount){
        this.score += amount;
    }

    public void increaseMoney(int amount){
        this.money += amount;
    }

    public void decreaseMoney(int amount){
        this.money -= amount;
    }

    public void purchaseCard(Card card){
        this.purchasedCards.add(card.getId());
    }

    public void removeCard(Card card){
        this.purchasedCards.remove(card.getId());
    }

    public ArrayList<Card> getPurchasedCardsByName(String name) {
        ArrayList<Card> purchasedCards = new ArrayList<>();

        for (String id:
             this.purchasedCards) {
           if ( DataManager.getInstance().getCardByUUID(id).getName().equals(name)){
               purchasedCards.add(DataManager.getInstance().getCardByUUID(id)) ;
           }
        }
        return purchasedCards;
    }

    public void addDeck(Deck deck){
        this.decks.add(deck.getId());
    }

    public void removeDeck(Deck deck){
        this.decks.remove(deck.getId());
    }

    public Deck getDeckByName(String name){
        for (String id:
                this.decks) {
            if ( DataManager.getInstance().getDeckByUUID(id).getName().equals(name)){
                return DataManager.getInstance().getDeckByUUID(id) ;
            }
        }
        return null;
    }

    public String getActiveDeck() {
        return this.activeDeck;
    }

    public void setActiveDeck(String activeDeck) {
        this.activeDeck = activeDeck;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", activeDeck='" + activeDeck + '\'' +
                ", score=" + score +
                ", money=" + money +
                ", purchasedCards=" + purchasedCards +
                ", decks=" + decks +
                '}';
    }
}
