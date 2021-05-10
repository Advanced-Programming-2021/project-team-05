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


    public final void deselect(boolean print) {
        if (selectedCard == null) {
            if (print) {
                view.printDeselectMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            }
        } else {
            selectedCard = null;
            selectedCardAddress = null;
            if (print) {
                view.printDeselectMessage(DuelMenuMessage.CARD_DESELECTED);
            }
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
            deselect(false);
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
                view.printTributeSummonMessage(DuelMenuMessage.INVALID_POSITION);
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
        deselect(false);
        canSummonOrSet = false;
        view.printTributeSummonMessage(DuelMenuMessage.SUMMON_SUCCESSFUL);
    }


    public final void flipSummon() {
        if (selectedCard == null) {
            view.printFlipSummonMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        Cell targetCell = board.getPlayerTable().getCellByAddress(selectedCardAddress);
        if (targetCell == null || selectedCardAddress.getZone() != CardAddressZone.MONSTER) {
            view.printFlipSummonMessage(DuelMenuMessage.CANT_CHANGE_POSITION);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.printFlipSummonMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        if (targetCell.getState() != CardState.HORIZONTAL_DOWN || targetCell.isNewlyAdded()) {
            view.printFlipSummonMessage(DuelMenuMessage.CANT_FLIP_SUMMON);
            return;
        }

        targetCell.setState(CardState.VERTICAL_UP);
        targetCell.setDoesPositionChanged(true);
        view.printFlipSummonMessage(DuelMenuMessage.FLIP_SUMMON_SUCCESSFUL);
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
        if (targetCell.doesPositionChanged()) {
            view.printChangePositionMessage(DuelMenuMessage.ALREADY_CHANGED_POSITION);
            return;
        }

        targetCell.setState(targetState);
        targetCell.setDoesPositionChanged(true);
        view.printChangePositionMessage(DuelMenuMessage.POSITION_CHANGED);
    }


    public final void attack(int targetPosition) {
        if (targetPosition < 1 || targetPosition > 5) {
            view.printAttackMessage(DuelMenuMessage.INVALID_POSITION, 0, null);
            return;
        }
        if (selectedCard == null) {
            view.printAttackMessage(DuelMenuMessage.NO_CARD_IS_SELECTED, 0, null);
            return;
        }
        Cell attackerCell = board.getPlayerTable().getCellByAddress(selectedCardAddress);
        if (attackerCell == null || selectedCardAddress.getZone() != CardAddressZone.MONSTER) {
            view.printAttackMessage(DuelMenuMessage.CANT_ATTACK, 0, null);
            return;
        }
        if (phase != Phase.BATTLE) {
            view.printAttackMessage(DuelMenuMessage.ACTION_NOT_ALLOWED, 0, null);
            return;
        }
        if (attackerCell.didAttack()) {
            view.printAttackMessage(DuelMenuMessage.ALREADY_ATTACKED, 0, null);
            return;
        }
        CardAddress targetCellAddress = new CardAddress(CardAddressZone.MONSTER, targetPosition, false);
        Cell targetCell = board.getOpponentTable().getCellByAddress(targetCellAddress);
        if (targetCell == null || targetCell.getCard() == null) {
            view.printAttackMessage(DuelMenuMessage.NO_CARD_TO_ATTACK, 0, null);
            return;
        }

        if (targetCell.getState() == CardState.VERTICAL_UP) {
            attackAttackPositionCard(targetPosition, attackerCell, targetCell);
        } else if (targetCell.getState() == CardState.HORIZONTAL_UP) {
            attackDefensePositionCard(targetPosition, attackerCell, targetCell);
        }
        attackerCell.setDidAttack(true);
        deselect(false);
    }

    private void attackAttackPositionCard(int targetPosition, Cell attackerCell, Cell targetCell) {
        Table attackerTable = board.getPlayerTable();
        Table targetTable = board.getOpponentTable();
        int attackerCardAttack = ((Monster) attackerCell.getCard()).getAttack();
        int targetCardAttack = ((Monster) targetCell.getCard()).getAttack();
        int damage = attackerCardAttack - targetCardAttack;
        if (damage > 0) {
            targetTable.decreaseLifePoint(damage);
            targetTable.moveMonsterToGraveyard(targetPosition);
            view.printAttackMessage(DuelMenuMessage.OPPONENT_ATTACK_POSITION_MONSTER_DESTROYED, damage, null);
        } else if (damage == 0) {
            attackerTable.moveMonsterToGraveyard(selectedCardAddress.getPosition());
            targetTable.moveMonsterToGraveyard(targetPosition);
            view.printAttackMessage(DuelMenuMessage.BOTH_ATTACK_POSITION_MONSTERS_DESTROYED, 0, null);
        } else {
            damage = Math.abs(damage);
            attackerTable.decreaseLifePoint(damage);
            attackerTable.moveMonsterToGraveyard(selectedCardAddress.getPosition());
            view.printAttackMessage(DuelMenuMessage.YOUR_ATTACK_POSITION_MONSTER_DESTROYED, damage, null);
        }
    }

    private void attackDefensePositionCard(int targetPosition, Cell attackerCell, Cell targetCell) {
        Table attackerTable = board.getPlayerTable();
        Table targetTable = board.getOpponentTable();
        String hiddenCardName = null;
        if (targetCell.getState() == CardState.HORIZONTAL_DOWN) {
            hiddenCardName = targetCell.getCard().getName();
        }
        int attackerCardAttack = ((Monster) attackerCell.getCard()).getAttack();
        int targetCardDefense = ((Monster) targetCell.getCard()).getDefence();
        int damage = attackerCardAttack - targetCardDefense;
        if (damage > 0) {
            targetTable.decreaseLifePoint(damage);
            targetTable.moveMonsterToGraveyard(targetPosition);
            view.printAttackMessage(DuelMenuMessage.OPPONENT_DEFENSE_POSITION_MONSTER_DESTROYED, 0, hiddenCardName);
        } else if (damage == 0) {
            view.printAttackMessage(DuelMenuMessage.NO_CARD_DESTROYED_AND_NO_DAMAGE, 0, hiddenCardName);
        } else {
            attackerTable.decreaseLifePoint(Math.abs(damage));
            view.printAttackMessage(DuelMenuMessage.NO_CARD_DESTROYED_WITH_DAMAGE, Math.abs(damage), hiddenCardName);
        }
    }


    public final void directAttack() {
        if (selectedCard == null) {
            view.printDirectAttackMessage(DuelMenuMessage.NO_CARD_IS_SELECTED, 0);
            return;
        }
        Cell attackerCell = board.getPlayerTable().getCellByAddress(selectedCardAddress);
        if (attackerCell == null || selectedCardAddress.getZone() != CardAddressZone.MONSTER) {
            view.printDirectAttackMessage(DuelMenuMessage.CANT_ATTACK, 0);
            return;
        }
        if (phase != Phase.BATTLE) {
            view.printDirectAttackMessage(DuelMenuMessage.ACTION_NOT_ALLOWED, 0);
            return;
        }
        if (attackerCell.didAttack()) {
            view.printDirectAttackMessage(DuelMenuMessage.ALREADY_ATTACKED, 0);
            return;
        }
        if (board.getOpponentTable().getMonsterCardsCount() == 0) {
            view.printDirectAttackMessage(DuelMenuMessage.CANT_ATTACK_DIRECTLY, 0);
            return;
        }

        int damage = ((Monster) attackerCell.getCard()).getAttack();
        board.getOpponentTable().decreaseLifePoint(damage);
        attackerCell.setDidAttack(true);
        deselect(false);
        view.printDirectAttackMessage(DuelMenuMessage.DIRECT_ATTACK_SUCCESSFUL, damage);
    }


    public final void activeEffect() {
    }
}
