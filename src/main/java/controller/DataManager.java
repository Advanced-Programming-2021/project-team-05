package controller;

import model.Deck;
import model.card.Card;


import java.util.ArrayList;

public class DataManager {

    private static DataManager dataManager ;

    private ArrayList<Card> allCards = new ArrayList<>();
    private ArrayList<Deck> allDecks = new ArrayList<>();

    public static DataManager getInstance(){

        if (dataManager == null){
            dataManager = new DataManager();
        }

        return dataManager;
    }

    public Card getCardByUUID(String uuid){

        for (Card card:
             allCards) {
            if (card.getId().equals(uuid)){
                return card;
            }
        }

        return null;
    }

    public Deck getDeckByUUID(String uuid){

        for (Deck deck:
             allDecks) {
            if (deck.getId().equals(uuid)){
                return deck;
            }
        }

        return null;
    }


}
