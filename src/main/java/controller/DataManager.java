package controller;

import model.Deck;
import model.User;
import model.card.Card;
import model.template.CardTemplate;

import java.util.ArrayList;

public class DataManager {
    private ArrayList<CardTemplate> allCardTemplates;
    private ArrayList<User> allUsers;
    private ArrayList<Deck> allDecks;
    private ArrayList<Card> allCards;
    private DataManager dataManager;


    private DataManager() {

    }


    public final DataManager getInstance() {
        return null;
    }


    public final CardTemplate getCardTemplateByName(String name) {
        return null;
    }


    public final void loadCardTemplates() {

    }


    public final void importCardTemplate(String name) {

    }


    public final void exportCardTemplate(String name) {

    }


    public final void addUser(User user) {

    }


    public final User getUserByUsername(String username) {
        return null;
    }


    public final User getUserByNickname(String nickname) {
        return null;
    }


    public final void addDeck(Deck deck) {

    }


    public final void removeDeck(Deck deck) {

    }


    public final Deck getDeckByUUID(String uuid) {
        return null;
    }


    public final void addCard(Card card) {

    }


    public final void removeCard(Card card) {

    }


    public final Card getCardByUUID(String uuid) {
        return null;
    }


    public final String getScoreboard() {
        return null;
    }
}
