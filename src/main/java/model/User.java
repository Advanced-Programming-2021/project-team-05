package model;

import model.card.Card;

import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private String nickname;
    private int score;
    private int money;
    private ArrayList<String> purchasedCards;
    private ArrayList<String> decks;
    private String activeDeck;


    public User(String username, String password, String nickname) {

    }


    public final String getUsername() {
        return null;
    }


    public final void setUsername(String username) {

    }


    public final String getPassword() {
        return null;
    }


    public final void setPassword(String password) {

    }


    public final String getNickname() {
        return null;
    }


    public final void setNickname(String nickname) {

    }


    public final int getScore() {
        return 0;
    }


    public final void setScore(int score) {

    }


    public final void increaseScore(int amount) {

    }


    public final int getMoney() {
        return 0;
    }


    public final void SetMoney(int money) {

    }


    public final void increaseMoney(int amount) {

    }


    public final void decreaseMoney(int amount) {

    }


    public final void purchaseCard(Card card) {

    }


    public final void removeCard(Card card) {

    }


    public final ArrayList<Card> getPurchasedCardsByName(String name) {
        return null;
    }


    public final void addDeck(Deck deck) {

    }


    public final void removeDeck(Deck deck) {

    }


    public final Deck getDeckByName(String name) {
        return null;
    }


    public final Deck getActiveDeck() {
        return null;
    }


    public final void setActiveDeck(Deck deck) {

    }


    public final String toString() {
        return null;
    }
}
