package model;

import control.DataManager;

import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class User {

    private final ArrayList<String> purchasedCardIds;
    private final ArrayList<String> deckIds;
    private String username;
    private String password;
    private String nickname;
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


    public Deck getActiveDeck() {
        return DataManager.getInstance().getDeckById(this.activeDeckId);
    }

    public boolean isActiveDeck(Deck deck) {
        return deck.getId().equals(this.activeDeckId);
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
