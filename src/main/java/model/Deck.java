package model;

import control.DataManager;
import model.card.Card;
import model.card.Monster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

public class Deck implements Cloneable {
    private ArrayList<String> mainDeckCards;
    private ArrayList<String> sideDeckCards;
    private String id;
    private String name;

    {
        mainDeckCards = new ArrayList<>();
        sideDeckCards = new ArrayList<>();
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
        DataManager dataManager = DataManager.getInstance();

        for (String id : this.mainDeckCards) {
            Card card = dataManager.getCardById(id);
            if (name.equals(card.getName())) cards.add(card);
        }

        return cards;
    }

    public final Card drawCard() {
        Card card = DataManager.getInstance().getCardById(this.mainDeckCards.get(0));
        this.mainDeckCards.remove(0);
        return card;
    }

    public final boolean isMainDeckFull() {
        return this.mainDeckCards.size() == 60;
    }

    public final int getMainDeckSize() {
        return this.mainDeckCards.size();
    }

    public final void shuffleMainDeck() {
        Collections.shuffle(this.mainDeckCards);
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
        DataManager dataManager = DataManager.getInstance();

        for (String id : this.sideDeckCards) {
            Card card = dataManager.getCardById(id);
            if (name.equals(card.getName())) cards.add(card);
        }

        return cards;
    }

    public final boolean isSideDeckFull() {
        return this.sideDeckCards.size() == 20;
    }

    private int getSideDeckSize() {
        return this.sideDeckCards.size();
    }

    public final void shuffleSideDeck() {
        Collections.shuffle(this.sideDeckCards);
    }


    public final boolean isCardFull(Card card) {
        ArrayList<Card> mainCards = this.getCardsByNameInMainDeck(card.getName());
        ArrayList<Card> sideCards = this.getCardsByNameInSideDeck(card.getName());

        return mainCards.size() + sideCards.size() == 3;
    }


    public final boolean isValid() {
        return this.mainDeckCards.size() >= 40;
    }


    public final String detailedToString(boolean isSide) {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<String> cards = isSide ? sideDeckCards : mainDeckCards;
        ArrayList<Card> monsters = new ArrayList<>();
        ArrayList<Card> spellsAndTraps = new ArrayList<>();

        for (String id : cards) {
            Card card = dataManager.getCardById(id);
            if (card instanceof Monster) {
                monsters.add(card);
            } else {
                spellsAndTraps.add(card);
            }
        }
        monsters.sort(Comparator.comparing(Card::getName));
        spellsAndTraps.sort(Comparator.comparing(Card::getName));

        StringBuilder deckString = new StringBuilder();
        deckString.append("Deck: ").append(this.getName()).append("\r\n")
                .append(isSide ? "Side" : "Main").append(" deck:\r\n");

        deckString.append("Monsters:\r\n");
        for (Card monster : monsters) {
            deckString.append(monster).append("\r\n");
        }

        deckString.append("Spell and Traps:\r\n");
        for (Card spellOrTrap : spellsAndTraps) {
            deckString.append(spellOrTrap).append("\r\n");
        }

        return deckString.toString();
    }


    @Override
    public final boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof Deck)) {
            return false;
        }

        return this.getName().equals(((Deck) object).getName());
    }


    @Override
    public final String toString() {
        return this.getName() + ": " +
                "main deck " + this.getMainDeckSize() +
                ", side deck " + this.getSideDeckSize() +
                ", " + (this.isValid() ? "valid" : "invalid");
    }


    @Override
    public Deck clone() throws CloneNotSupportedException {
        Deck cloned = (Deck) super.clone();
        DataManager dataManager = DataManager.getInstance();

        cloned.mainDeckCards = new ArrayList<>();
        for (String cardId : this.mainDeckCards) {
            cloned.addCardToMainDeck(dataManager.getCardById(cardId));
        }

        cloned.sideDeckCards = new ArrayList<>();
        for (String cardId : this.sideDeckCards) {
            cloned.addCardToSideDeck(dataManager.getCardById(cardId));
        }

        return cloned;
    }
}
