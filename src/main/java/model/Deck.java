package model;

import control.DataManager;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.template.property.SpellTrapStatus;

import java.util.*;

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


    public ArrayList<String> getMainDeckCardIds() {
        return mainDeckCardIds;
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

    public final void shuffleMainDeck() {
        Collections.shuffle(this.mainDeckCardIds);
    }

    public final Card drawCard() {
        Card card = DataManager.getInstance().getCardById(this.mainDeckCardIds.get(0));
        this.mainDeckCardIds.remove(0);
        return card;
    }


    public ArrayList<String> getSideDeckCardIds() {
        return sideDeckCardIds;
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


    public final void swapCards(int mainCardPosition, int sideCardPosition) {
        String mainCardId = this.mainDeckCardIds.get(mainCardPosition - 1);
        String sideCardId = this.sideDeckCardIds.get(sideCardPosition - 1);
        this.mainDeckCardIds.set(mainCardPosition - 1, sideCardId);
        this.sideDeckCardIds.set(sideCardPosition - 1, mainCardId);
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


    public final String detailedToString(boolean isSide) {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<String> cards = isSide ? sideDeckCardIds : mainDeckCardIds;
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

        deckString.delete(deckString.length() - 2, deckString.length());
        return deckString.toString();
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
