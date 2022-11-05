package model;

import control.DataManager;
import model.card.Card;
import model.card.Spell;
import model.card.Trap;
import model.template.property.SpellTrapStatus;

import java.util.ArrayList;
import java.util.UUID;

public class Deck implements Cloneable {
    private String id;
    private String name;
    private ArrayList<String> mainDeckCardIds;
    private ArrayList<String> sideDeckCardIds;

    {
        mainDeckCardIds = new ArrayList<>();
        sideDeckCardIds = new ArrayList<>();
    }


    public Deck(String name) {
        this.setId(UUID.randomUUID().toString());
        this.setName(name);
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


    public final ArrayList<Card> getMainDeck() {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<Card> mainDeck = new ArrayList<>();
        for (String id : this.mainDeckCardIds) {
            mainDeck.add(dataManager.getCardById(id));
        }
        return mainDeck;
    }

    public final void addCardToMainDeck(Card card) {
        this.mainDeckCardIds.add(card.getId());
    }

    public final void removeCardFromMainDeck(Card card) {
        this.mainDeckCardIds.remove(card.getId());
    }

    public final boolean hasCardInMainDeck(Card card) {
        return this.mainDeckCardIds.contains(card.getId());
    }

    public final ArrayList<Card> getCardsByNameInMainDeck(String name) {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<Card> cards = new ArrayList<>();
        for (String id : this.mainDeckCardIds) {
            Card card = dataManager.getCardById(id);
            if (name.equals(card.getName())) {
                cards.add(card);
            }
        }
        return cards;
    }

    public final boolean isMainDeckFull() {
        return this.getMainDeckSize() >= 60;
    }

    public final int getMainDeckSize() {
        return this.mainDeckCardIds.size();
    }


    public final ArrayList<Card> getSideDeck() {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<Card> sideDeck = new ArrayList<>();
        for (String id : this.sideDeckCardIds) {
            sideDeck.add(dataManager.getCardById(id));
        }
        return sideDeck;
    }

    public final void addCardToSideDeck(Card card) {
        this.sideDeckCardIds.add(card.getId());
    }

    public final void removeCardFromSideDeck(Card card) {
        this.sideDeckCardIds.remove(card.getId());
    }

    public final boolean hasCardInSideDeck(Card card) {
        return this.sideDeckCardIds.contains(card.getId());
    }

    public final ArrayList<Card> getCardsByNameInSideDeck(String name) {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<Card> cards = new ArrayList<>();
        for (String id : this.sideDeckCardIds) {
            Card card = dataManager.getCardById(id);
            if (name.equals(card.getName())) {
                cards.add(card);
            }
        }
        return cards;
    }

    public final boolean isSideDeckFull() {
        return this.getSideDeckSize() >= 20;
    }

    public int getSideDeckSize() {
        return this.sideDeckCardIds.size();
    }


    public final boolean isCardFull(Card card) {
        ArrayList<Card> mainCards = this.getCardsByNameInMainDeck(card.getName());
        ArrayList<Card> sideCards = this.getCardsByNameInSideDeck(card.getName());

        SpellTrapStatus status = SpellTrapStatus.UNLIMITED;
        if (card instanceof Spell) {
            status = ((Spell) card).getStatus();
        } else if (card instanceof Trap) {
            status = ((Trap) card).getStatus();
        }

        return mainCards.size() + sideCards.size() >= status.getMaxCount();
    }


    public final boolean isValid() {
        return this.mainDeckCardIds.size() >= 40;
    }


    public ArrayList<Card> getAddableCards(ArrayList<Card> cards) {
        ArrayList<Card> addableCards = new ArrayList<>();
        for (Card card : cards) {
            if (!this.hasCardInMainDeck(card) && !this.hasCardInSideDeck(card)) {
                int cardCount = this.getCardsByNameInMainDeck(card.getName()).size();
                cardCount += this.getCardsByNameInSideDeck(card.getName()).size();
                for (Card addableCard : addableCards)
                    if (card.getName().equals(addableCard.getName())) cardCount++;

                int maxCount = 3;
                if (card instanceof Spell && ((Spell) card).getStatus() == SpellTrapStatus.LIMITED)
                    maxCount = 1;
                else if (card instanceof Trap && ((Trap) card).getStatus() == SpellTrapStatus.LIMITED)
                    maxCount = 1;

                if (cardCount < maxCount) addableCards.add(card);
            }
        }
        return addableCards;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;
        Deck deck = (Deck) object;
        return this.getId().equals(deck.getId());
    }


    @Override
    public final String toString() {
        return this.getName() + "\n" +
                "Main Deck Size: " + this.getMainDeckSize() + "\n" +
                "Side Deck Size: " + this.getSideDeckSize() + "\n" +
                "Deck is " + (this.isValid() ? "" : "Not ") + "Valid";
    }


    @Override
    public Deck clone() throws CloneNotSupportedException {
        Deck cloned = (Deck) super.clone();
        DataManager dataManager = DataManager.getInstance();

        cloned.mainDeckCardIds = new ArrayList<>();
        for (String cardId : this.mainDeckCardIds) {
            cloned.addCardToMainDeck(dataManager.getCardById(cardId));
        }

        cloned.sideDeckCardIds = new ArrayList<>();
        for (String cardId : this.sideDeckCardIds) {
            cloned.addCardToSideDeck(dataManager.getCardById(cardId));
        }

        return cloned;
    }
}
