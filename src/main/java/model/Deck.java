package model;

import controller.DataManager;
import model.card.Card;
import model.card.Monster;

import java.util.ArrayList;

public class Deck {
    private String id;
    private String name;
    private ArrayList<String> mainDeckCards = new ArrayList<>();
    private ArrayList<String> sideDeckCards = new ArrayList<>();


    public Deck(String name) {
        this.name = name;
    }


    public final String getId() {
        return this.id;
    }


    public final void setId(String id) {
        this.id = id;
    }


    public final String getName() {
        return this.name;
    }


    public final void setName(String name) {
        this.name = name;
    }


    public final void addCardToMainDeck(Card card) {
        this.mainDeckCards.add(card.getId());
    }


    public final void removeCardFromMainDeck(Card card) {
        this.mainDeckCards.remove(card.getId());
    }


    public final boolean hasCardInMainDeck(Card card) {
        return this.mainDeckCards.contains(card.getId());
    }


    public final ArrayList<Card> getCardsByNameInMainDeck(String name) {

        ArrayList<Card> cards = new ArrayList<>();

        for (String id:
            this.mainDeckCards) {
            if (DataManager.getInstance().getCardByUUID(id).getName().equals(name))
                cards.add(DataManager.getInstance().getCardByUUID(id));
        }
        return cards;
    }


    public final boolean isMainDeckFull() {

        if (this.mainDeckCards.size() == 60){
            return true;
        }
        return false;
    }


    public final void addCardToSideDeck(Card card) {
        this.sideDeckCards.add(card.getId());
    }


    public final void removeCardFromSideDeck(Card card) {
        this.sideDeckCards.remove(card.getId());
    }

    public final boolean hasCardInSideDeck(Card card) {
        return this.sideDeckCards.contains(card.getId());
    }

    public final ArrayList<Card> getCardsByNameInSideDeck(String name) {
        ArrayList<Card> cards = new ArrayList<>();

        for (String id:
                this.sideDeckCards) {
            if (DataManager.getInstance().getCardByUUID(id).getName().equals(name))
                cards.add(DataManager.getInstance().getCardByUUID(id));
        }
        return cards;
    }

    public final boolean isSideDeckFull() {
        return this.sideDeckCards.size() == 20;
    }

    public final boolean isCardFull(Card card) {

        int counter = 0;

        for (String id:
             mainDeckCards) {
            if (DataManager.getInstance().getCardByUUID(id).getName().equals(card.getName())){
                counter++;
            }
        }   for (String id:
             sideDeckCards) {
            if (DataManager.getInstance().getCardByUUID(id).getName().equals(card.getName())){
                counter++;
            }
        }
        return counter == 3;
    }

    public final boolean isValid() {
        return mainDeckCards.size() >= 40;
    }

    public final String detailedToString(boolean isSide) {

        StringBuilder monsters = new StringBuilder();
        StringBuilder spellsAndTraps = new StringBuilder();

        if (isSide){
            for (String id:
                 sideDeckCards) {
                if (DataManager.getInstance().getCardByUUID(id) instanceof Monster){
                    monsters.append(DataManager.getInstance().getCardByUUID(id).getName()).append(":").append(DataManager.getInstance().getCardByUUID(id).getDescription()).append("\n");
                }
                else{
                    spellsAndTraps.append(DataManager.getInstance().getCardByUUID(id).getName()).append(":").append(DataManager.getInstance().getCardByUUID(id).getDescription()).append("\n");
                }
            }
            return "Deck: " + this.name + "\n" +
                    "Side Deck:\n" +
                    "Monsters:\n" + monsters.toString() +
                    "Spell and Traps:\n" +
                    "Spell and Traps:\n" + spellsAndTraps.toString() ;


        } else{
            for (String id:
                 mainDeckCards) {
                if (DataManager.getInstance().getCardByUUID(id) instanceof Monster){
                    monsters.append(DataManager.getInstance().getCardByUUID(id).getName()).append(":").append(DataManager.getInstance().getCardByUUID(id).getDescription()).append("\n");

                }
                else{
                    spellsAndTraps.append(DataManager.getInstance().getCardByUUID(id).getName()).append(":").append(DataManager.getInstance().getCardByUUID(id).getDescription()).append("\n");
                }
            }
            return "Deck: " + this.name + "\n" +
                    "Main Deck:\n" +
                    "Monsters:\n" + monsters.toString() +
                    "Spell and Traps:\n" +
                    "Spell and Traps:\n" + spellsAndTraps.toString() ;
        }

    }


    public final String toString() {
        return this.name ;
    }
}
