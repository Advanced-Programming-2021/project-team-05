package controller;

import model.*;
import model.card.Card;
import model.card.Monster;
import model.template.CardType;
import view.DuelMenuView;

import java.util.ArrayList;

public class DuelMenuController {

    private DuelMenuView view;
    private Board board;
    private int rounds;
    private Phase phase;
    private Card selectedCard;
    private CardAddress selectedCardAddress;

    private boolean canSummonOrSet;

    {
        phase = Phase.DRAW;
        canSummonOrSet = true;
    }


    public DuelMenuController(User user1, User user2) {
        this.board = new Board(user1, user2);
    }


    public void setView(DuelMenuView view) {
        this.view = view;
    }


    public Card getSelectedCard() {
        return this.selectedCard;
    }

    private void setSelectedCard(Card card, CardAddress cardAddress) {
        this.selectedCard = card;
        this.selectedCardAddress = cardAddress;
    }


    private void changeTurn() {
        board.swapTables();
    }


    public final void deselect() {
        if (selectedCard == null) {
            view.printDeselectMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
        } else {
            selectedCard = null;
            view.printDeselectMessage(DuelMenuMessage.CARD_DESELECTED);
        }
    }


    public final void selectCard(CardAddress cardAddress) {
        switch (cardAddress.getZone()) {
            case HAND:
                selectCardFromHand(cardAddress);
                break;
            case MONSTER:
                selectCardFromMonsterZone(cardAddress);
                break;
            case SPELL:
                selectCardFromSpellZone(cardAddress);
                break;
            case FIELD:
                selectCardFromFieldZone(cardAddress);
                break;
            default:
                view.printTributeSummonMessage(DuelMenuMessage.INVALID_SELECTION);
        }
    }

    private void selectCardFromHand(CardAddress cardAddress) {
        int position = cardAddress.getPosition();
        boolean isForOpponent = cardAddress.isForOpponent();
        Table targetTable = cardAddress.isForOpponent() ? board.getOpponentTable() : board.getPlayerTable();
        if (isForOpponent) {
            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
            return;
        }
        int handSize = targetTable.getHand().size();
        if (position < 1 || position > handSize) {
            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
            return;
        }
        setSelectedCard(targetTable.getCardFromHand(position), cardAddress);
        view.printSelectMessage(DuelMenuMessage.CARD_SELECTED);
    }

    private void selectCardFromMonsterZone(CardAddress cardAddress) {
        int position = cardAddress.getPosition();
        Table targetTable = cardAddress.isForOpponent() ? board.getOpponentTable() : board.getPlayerTable();
        if (position < 1 || position > 5) {
            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
            return;
        }
        Card targetCard = targetTable.getMonster(position);
        if (targetCard == null) {
            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
            return;
        }
        setSelectedCard(targetCard, cardAddress);
        view.printSelectMessage(DuelMenuMessage.CARD_SELECTED);
    }

    private void selectCardFromSpellZone(CardAddress cardAddress) {
        int position = cardAddress.getPosition();
        Table targetTable = cardAddress.isForOpponent() ? board.getOpponentTable() : board.getPlayerTable();
        if (position < 1 || position > 5) {
            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
            return;
        }
        Card targetCard = targetTable.getSpellOrTrap(position);
        if (targetCard == null) {
            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
            return;
        }
        setSelectedCard(targetCard, cardAddress);
        view.printSelectMessage(DuelMenuMessage.CARD_SELECTED);
    }

    private void selectCardFromFieldZone(CardAddress cardAddress) {
        Table targetTable = cardAddress.isForOpponent() ? board.getOpponentTable() : board.getPlayerTable();
        Card targetCard = targetTable.getFieldSpell();
        if (targetCard == null) {
            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
            return;
        }
        setSelectedCard(targetCard, cardAddress);
        view.printSelectMessage(DuelMenuMessage.CARD_SELECTED);
    }


    public final void summon() {
        if (selectedCard == null) {
            view.parseSummonMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        if (!(selectedCard instanceof Monster) ||
                selectedCardAddress.getZone() != CardAddressZone.HAND ||
                selectedCard.getType() == CardType.RITUAL ||
                "Gate Guardian".equals(selectedCard.getName())) {
            view.parseSummonMessage(DuelMenuMessage.CANT_SUMMON);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.parseSummonMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        Table playerTable = board.getPlayerTable();
        if (playerTable.isMonsterZoneFull()) {
            view.parseSummonMessage(DuelMenuMessage.MONSTER_ZONE_IS_FULL);
            return;
        }
        if (!canSummonOrSet) {
            view.parseSummonMessage(DuelMenuMessage.ALREADY_SUMMONED_SET);
            return;
        }
        Monster card = (Monster) selectedCard;
        if (card.getLevel() <= 4) {
            playerTable.addMonster(card, CardState.VERTICAL_UP);
            canSummonOrSet = false;
            view.parseSummonMessage(DuelMenuMessage.SUMMON_SUCCESSFUL);
        } else if (card.getLevel() <= 6) {
            if (playerTable.getMonsterCardsCount() == 0) {
                view.parseSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
            } else {
                view.parseSummonMessage(DuelMenuMessage.TRIBUTE_1_CARD);
            }
        } else {
            if (playerTable.getMonsterCardsCount() <= 1) {
                view.parseSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
            } else {
                view.parseSummonMessage(DuelMenuMessage.TRIBUTE_2_CARDS);
            }
        }
    }


    public final void tributeSummon(ArrayList<Integer> tributesPositions) {
        for (Integer position : tributesPositions) {
            if (position < 1 || position > 5) {
                view.printTributeSummonMessage(DuelMenuMessage.INVALID_SELECTION);
                return;
            }
        }

        ArrayList<Card> tributeCards = new ArrayList<>();
        Table playerTable = board.getPlayerTable();
        for (Integer position : tributesPositions) {
            Card card = playerTable.getMonster(position);
            if (card == null) {
                view.printTributeSummonMessage(DuelMenuMessage.NO_MONSTER_ON_ADDRESS);
                return;
            } else {
                tributeCards.add(card);
            }
        }

        for (Integer position : tributesPositions) {
            playerTable.removeMonster(position);
        }
        for (Card card : tributeCards) {
            playerTable.addCardToGraveyard(card);
        }

        playerTable.addMonster((Monster) selectedCard, CardState.VERTICAL_UP);
        canSummonOrSet = false;
        view.printTributeSummonMessage(DuelMenuMessage.SUMMON_SUCCESSFUL);
    }


    public final void flipSummon() {

    }


    public final void set() {
    }


    public final void changePosition(String position) {
        CardState targetState;
        switch (position) {
            case "attack":
                targetState = CardState.VERTICAL_UP;
                break;
            case "defense":
                targetState = CardState.HORIZONTAL_UP;
                break;
            default:
                view.printChangePositionMessage(DuelMenuMessage.INVALID_COMMAND);
                return;
        }

        if (selectedCard == null) {
            view.printChangePositionMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        Cell targetCell = board.getPlayerTable().getCellByAddress(selectedCardAddress);
        if (targetCell == null || selectedCardAddress.getZone() != CardAddressZone.MONSTER) {
            view.printChangePositionMessage(DuelMenuMessage.CANT_CHANGE_POSITION);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.printChangePositionMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        if ((targetState == CardState.VERTICAL_UP && targetCell.getState() != CardState.HORIZONTAL_UP) ||
                (targetState == CardState.HORIZONTAL_UP && targetCell.getState() != CardState.VERTICAL_UP)) {
            view.printChangePositionMessage(DuelMenuMessage.ALREADY_IN_WANTED_POSITION);
            return;
        }
        if (targetCell.isPositionChanged()) {
            view.printChangePositionMessage(DuelMenuMessage.ALREADY_CHANGED_POSITION);
            return;
        }

        targetCell.setState(targetState);
        targetCell.setPositionChanged(true);
        view.printChangePositionMessage(DuelMenuMessage.POSITION_CHANGED);
    }


    public final void attack(int position) {
    }


    public final void directAttack() {
    }


    public final void activeEffect() {
    }
}
