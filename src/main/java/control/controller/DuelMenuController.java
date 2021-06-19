package control.controller;

import control.DataManager;
import control.message.DuelMenuMessage;
import model.User;
import model.board.*;
import model.board.cell.Cell;
import model.board.cell.MonsterCell;
import model.board.cell.SpellTrapCell;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.effect.Effect;
import model.effect.Event;
import model.effect.action.ActionEnum;
import model.template.property.CardType;
import utils.CoinSide;
import utils.Utility;
import view.DuelMenuView;

import java.util.ArrayList;
import java.util.Random;

public class DuelMenuController {

    private final User playerOne;
    private final User playerTwo;
    private final int rounds;
    private final Board[] boards;

    private DuelMenuView view;
    private int currentRound;
    private int currentTurn;
    private Phase phase;
    private Board board;

    private Card selectedCard;
    private CardAddress selectedCardAddress;

    private Card ritualSummonSpell;
    private CardAddress ritualSummonSpellAddress;

    private Integer attackedCardPosition;
    private boolean preventAttack;

    {
        selectedCard = null;
        selectedCardAddress = null;
        ritualSummonSpell = null;
        ritualSummonSpellAddress = null;
    }


    public DuelMenuController(User playerOne, User playerTwo, int rounds) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.currentRound = 0;
        this.currentTurn = 1;
        this.rounds = rounds;
        this.boards = new Board[rounds];
        this.attackedCardPosition = null;
        this.setPreventAttack(false);
    }


    public final DuelMenuView getView() {
        return this.view;
    }

    public final void setView(DuelMenuView view) {
        this.view = view;
    }


    public final boolean isAi(User user) {
        return DataManager.getInstance().getAi().equals(user);
    }


    public final Board getBoard() {
        return this.board;
    }


    public final int getRounds() {
        return this.rounds;
    }


    public final Phase getPhase() {
        return this.phase;
    }


    public final Card getSelectedCard() {
        return this.selectedCard;
    }

    private void setSelectedCard(Card card, CardAddress cardAddress) {
        this.selectedCard = card;
        this.selectedCardAddress = cardAddress;
    }


    public final CardAddress getSelectedCardAddress() {
        return this.selectedCardAddress;
    }


    public final void setRitualSummonSpell(Card ritualSummonSpell) {
        this.ritualSummonSpell = ritualSummonSpell;
    }

    public final void setRitualSummonSpellAddress(CardAddress address) {
        this.ritualSummonSpellAddress = address;
    }


    public final Integer getAttackedCardPosition() {
        return this.attackedCardPosition;
    }


    public final void setPreventAttack(boolean preventAttack) {
        this.preventAttack = preventAttack;
    }


    private void reset() {
        deselect(false);
        setRitualSummonSpell(null);
        setRitualSummonSpellAddress(null);
        setPreventAttack(false);
    }


    public final void startNextRound() {
        currentRound++;
        initializeBoard();
        phase = Phase.DRAW;
        view.showPhase(phase.getName());
        boards[currentRound - 1] = board;
        view.showTurn(board.getPlayerTable().getOwner().getNickname());
        if (currentRound != 1) {
            rearrangeDeck();
        } else {
            board.getPlayerTable().initializeHand();
            board.getOpponentTable().initializeHand();
        }
        view.showDrawMessage(board.getPlayerTable().drawCard());
        if (isAi(board.getPlayerTable().getOwner())) {
            handleAI();
        }
    }

    private void initializeBoard() {
        User player;
        User opponent;
        if (currentRound == 1) {
            CoinSide coinSide = Utility.flipCoin();
            if (coinSide == CoinSide.HEADS) {
                player = playerOne;
                opponent = playerTwo;
            } else {
                player = playerTwo;
                opponent = playerOne;
            }
            view.showFlipCoinResult(player.getUsername(), coinSide);
        } else {
            Board previousBoard = boards[currentRound - 2];
            player = previousBoard.getLoserTable().getOwner();
            opponent = previousBoard.getWinnerTable().getOwner();
        }
        board = new Board(player, opponent);
    }


    private void rearrangeDeck() {
        for (int i = 0; i < 2; i++) {
            Table targetTable = board.getPlayerTable();
            if (isAi(targetTable.getOwner())) {
                continue;
            }
            if (targetTable.getDeck().getSideDeckSize() == 0) {
                board.swapTables();
                view.showTurn(board.getPlayerTable().getOwner().getNickname());
                continue;
            }
            ArrayList<Card> mainDeck = targetTable.getDeck().getMainDeck();
            ArrayList<Card> sideDeck = targetTable.getDeck().getSideDeck();
            while (true) {
                view.showCards(mainDeck, "Main Deck");
                view.showCards(sideDeck, "Side Deck");
                String message = "do you want to change your main deck? (yes/no)";
                String response = view.getOneOfValues("yes", "no", message, "invalid input");
                if ("no".equals(response)) {
                    break;
                }

                int mainCardPosition = getCardPosition(mainDeck, "enter card number in main deck:");
                if (mainCardPosition == -1) {
                    continue;
                }
                int sideCardPosition = getCardPosition(sideDeck, "enter card number in side deck:");
                if (sideCardPosition == -1) {
                    continue;
                }

                targetTable.getDeck().swapCards(mainCardPosition, sideCardPosition);
                Card mainCard = mainDeck.get(mainCardPosition - 1);
                Card sideCard = sideDeck.get(sideCardPosition - 1);
                mainDeck.set(mainCardPosition - 1, sideCard);
                mainDeck.set(sideCardPosition - 1, mainCard);
            }
            board.swapTables();
            view.showTurn(board.getPlayerTable().getOwner().getNickname());
        }
    }

    private int getCardPosition(ArrayList<Card> cards, String message) {
        int cardPosition;
        while (true) {
            ArrayList<Integer> positions = view.getNumbers(1, message);
            if (positions == null) {
                return -1;
            }
            cardPosition = positions.get(0);
            if (cardPosition < 1 || cardPosition > cards.size()) {
                message = "position should be between 1 and " + cards.size();
                continue;
            }
            break;
        }
        return cardPosition;
    }


    private void changeTurn() {
        board.swapTables();
        board.getPlayerTable().reset();
        board.getOpponentTable().reset();
        this.currentTurn++;
        view.showTurn(board.getPlayerTable().getOwner().getNickname());

    }

    public final void quickChangeTurn(boolean showBoard) {
        board.swapTables();
        view.showQuickTurn(board.getPlayerTable().getOwner().getNickname());
        if (showBoard) {
            view.showBoard(board);
        }
    }


    public final void goToNextPhase(boolean showBoard) {
        if (ritualSummonSpellAddress != null) {
            view.printRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
            return;
        }
        deselect(false);
        phase = phase.getNextPhase();
        if (phase == Phase.DRAW) {
            changeTurn();
            view.showPhase(phase.getName());
            if (board.getPlayerTable().getHand().size() < 6) {
                if (board.getPlayerTable().getDeck().getMainDeckSize() == 0) {
                    win(board.getPlayerTable(), board.getOpponentTable());
                    return;
                }
                view.showDrawMessage(board.getPlayerTable().drawCard());
            }
            if (isAi(board.getPlayerTable().getOwner())) {
                handleAI();
            }
            return;
        } else if (phase == Phase.MAIN_1 || phase == Phase.MAIN_2) {
            view.showPhase(phase.getName());
            if (showBoard) view.showBoard(board);
            return;
        }
        view.showPhase(phase.getName());
    }


    public final void deselect(boolean print) {
        if (selectedCard == null) {
            if (print) {
                view.printDeselectMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            }
            return;
        }
        selectedCard = null;
        selectedCardAddress = null;
        if (print) {
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
        boolean isForOpponent = cardAddress.isForOpponent();
        if (isForOpponent) {
            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
            return;
        }
        int position = cardAddress.getPosition();
        Table targetTable = board.getPlayerTable();
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
        if (position < 1 || position > 5) {
            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
            return;
        }
        Table targetTable = cardAddress.isForOpponent() ? board.getOpponentTable() : board.getPlayerTable();
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
        if (position < 1 || position > 5) {
            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
            return;
        }
        Table targetTable = cardAddress.isForOpponent() ? board.getOpponentTable() : board.getPlayerTable();
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


    public final void checkSummon(boolean isSpecial) {
        if (selectedCard == null) {
            view.printSummonMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        if (selectedCardAddress.getZone() != CardAddressZone.HAND || !(selectedCard instanceof Monster)) {
            view.printSummonMessage(DuelMenuMessage.CANT_SUMMON);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.printSummonMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        Table playerTable = board.getPlayerTable();
        Monster monster = (Monster) selectedCard;
        if (ritualSummonSpellAddress != null) {
            if (monster.getType() != CardType.RITUAL) {
                view.printRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
                return;
            }
            tributeSummon(3, true);
            return;
        }
        if (monster.getType() == CardType.RITUAL) {
            view.printSummonMessage(DuelMenuMessage.CANT_SUMMON);
            return;
        }
        if ("The Tricky".equals(monster.getName())) {
            String message = "do you want to summon card normal or special?";
            String summonType = view.getOneOfValues("normal", "special", message, "invalid input");
            if (summonType == null) {
                view.printActionCanceled();
                return;
            }
            if ("special".equals(summonType)) {
                if (playerTable.getHand().size() <= 1) {
                    view.printSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
                    return;
                }
                tributeSummonFromHand(1);
                return;
            }
        } else if ("Gate Guardian".equals(monster.getName())) {
            if (playerTable.getMonsterCardsCount() <= 2) {
                view.printSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
                return;
            }
            tributeSummon(3, true);
            return;
        }
        if (!isSpecial && !playerTable.canSummonOrSet()) {
            view.printSummonMessage(DuelMenuMessage.ALREADY_SUMMONED_SET);
            return;
        }
        if (monster.getLevel() <= 4) {
            if (playerTable.isMonsterZoneFull()) {
                view.printSummonMessage(DuelMenuMessage.MONSTER_ZONE_IS_FULL);
                return;
            }
            summon(monster, false);
        } else if (monster.getLevel() <= 6) {
            if (playerTable.getMonsterCardsCount() == 0) {
                view.printSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
                return;
            }
            tributeSummon(1, false);
        } else if (monster.getLevel() <= 8) {
            if (playerTable.getMonsterCardsCount() <= 1) {
                view.printSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
                return;
            }
            tributeSummon(2, false);
        }
    }

    public final void tributeSummon(int tributesCount, boolean isSpecial) {
        String message = "enter " + tributesCount + " number(s) for tribute positions";
        ArrayList<Integer> tributesPositions = view.getNumbers(tributesCount, message);
        if (tributesPositions == null) {
            view.printActionCanceled();
            return;
        }
        for (Integer position : tributesPositions) {
            if (position < 1 || position > 5) {
                view.printTributeSummonMessage(DuelMenuMessage.INVALID_POSITION);
                return;
            }
        }

        ArrayList<Monster> tributeCards = new ArrayList<>();
        Table playerTable = board.getPlayerTable();
        for (Integer position : tributesPositions) {
            Monster monster = playerTable.getMonster(position);
            if (monster == null) {
                view.printTributeSummonMessage(DuelMenuMessage.NO_MONSTER_ON_ADDRESS);
                return;
            }
            tributeCards.add(monster);
        }

        CardState cardState = CardState.VERTICAL_UP;
        if (ritualSummonSpellAddress != null) {
            int levelsSum = 0;
            for (Monster tributeMonster : tributeCards) {
                levelsSum += tributeMonster.getLevel();
            }
            if (levelsSum < ((Monster) selectedCard).getLevel()) {
                view.printRitualSummonMessage(DuelMenuMessage.DONT_MATCH_WITH_RITUAL_MONSTER);
                return;
            }
            String stateString = view.getOneOfValues("attack", "defense", "enter monster state: (attack/defense)", "invalid state");
            if (stateString == null) {
                view.printActionCanceled();
                return;
            }
            if ("attack".equals(stateString)) {
                cardState = CardState.VERTICAL_UP;
            } else {
                cardState = CardState.HORIZONTAL_UP;
            }
        }

        for (Integer position : tributesPositions) {
            moveMonsterToGraveyard(playerTable, position);
        }

        if (ritualSummonSpellAddress != null) {
            ritualSummon((Monster) selectedCard, cardState);
        } else {
            summon((Monster) selectedCard, isSpecial);
        }
    }

    private void tributeSummonFromHand(int tributesCount) {
        Table playerTable = board.getPlayerTable();

        view.showHand(playerTable.getHand());
        String message = "enter card position to be tribute from hand:";
        ArrayList<Integer> tributesPositions = view.getNumbers(tributesCount, message);
        if (tributesPositions == null) {
            view.printActionCanceled();
            return;
        }
        ArrayList<Card> tributeCards = new ArrayList<>();
        for (Integer position : tributesPositions) {
            if (position < 1 || position > playerTable.getHand().size() || position == selectedCardAddress.getPosition()) {
                view.printTributeSummonMessage(DuelMenuMessage.INVALID_POSITION);
                return;
            }
            tributeCards.add(playerTable.getCardFromHand(position));
        }

        for (int i = 0, tributeCardsSize = tributeCards.size(); i < tributeCardsSize; i++) {
            int position = tributesPositions.get(i);
            Card tributeCard = tributeCards.get(i);
            removeCardFromHand(playerTable, position);
            playerTable.addCardToGraveyard(tributeCard);
            if (position < selectedCardAddress.getPosition()) {
                selectedCardAddress.setPosition(selectedCardAddress.getPosition() - 1);
            }
        }

        summon((Monster) selectedCard, true);
    }

    public final void summon(Monster monster, boolean isSpecial) {
        Table playerTable = board.getPlayerTable();
        removeCardFromHand(playerTable, selectedCardAddress.getPosition());
        selectedCardAddress.setZone(CardAddressZone.MONSTER);
        selectedCardAddress.setPosition(playerTable.getFirstEmptyMonsterCellPosition());
        addMonsterToTable(monster, playerTable, CardState.VERTICAL_UP);
        if (!isSpecial) {
            playerTable.setCanSummonOrSet(false);
            if (((Monster) selectedCard).getAttack() >= 1000) {
                checkQuickActivation(Event.MONSTER_WITH_1000_ATTACK_SUMMONED);
            }
        }
        view.printSummonMessage(DuelMenuMessage.SUMMON_SUCCESSFUL);
        view.showBoard(board);
        checkQuickActivation(Event.MONSTER_SUMMONED);
        deselect(false);
    }

    private void ritualSummon(Monster monster, CardState state) {
        Table playerTable = board.getPlayerTable();
        removeCardFromHand(playerTable, ritualSummonSpellAddress.getPosition());
        playerTable.addCardToGraveyard(ritualSummonSpell);
        removeCardFromHand(playerTable, selectedCardAddress.getPosition());
        addMonsterToTable(monster, playerTable, state);
        selectedCardAddress.setZone(CardAddressZone.MONSTER);
        selectedCardAddress.setPosition(playerTable.getFirstEmptyMonsterCellPosition());
        view.printTributeSummonMessage(DuelMenuMessage.SUMMON_SUCCESSFUL);
        view.showBoard(board);
        checkQuickActivation(Event.MONSTER_SUMMONED);
        deselect(false);
        ritualSummonSpellAddress = null;
    }


    public final void checkFlipSummon() {
        if (ritualSummonSpellAddress != null) {
            view.printRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
            return;
        }
        if (selectedCard == null) {
            view.printFlipSummonMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        if (selectedCardAddress.getZone() != CardAddressZone.MONSTER) {
            view.printFlipSummonMessage(DuelMenuMessage.CANT_CHANGE_POSITION);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.printFlipSummonMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        MonsterCell targetCell = board.getPlayerTable().getMonsterCell(selectedCardAddress.getPosition());
        if (targetCell.getState() != CardState.HORIZONTAL_DOWN || targetCell.isNewlyAdded()) {
            view.printFlipSummonMessage(DuelMenuMessage.CANT_FLIP_SUMMON);
            return;
        }
        flipSummon(targetCell, true);
    }

    private void flipSummon(MonsterCell targetCell, boolean print) {
        targetCell.setState(CardState.VERTICAL_UP);
        targetCell.setDoesPositionChanged(true);
        if (((Monster) selectedCard).getAttack() >= 1000) {
            checkQuickActivation(Event.MONSTER_WITH_1000_ATTACK_SUMMONED);
        }
        if (print) {
            view.printFlipSummonMessage(DuelMenuMessage.FLIP_SUMMON_SUCCESSFUL);
            selectedCard.runActions(Event.YOU_FLIP_SUMMONED_MANUALLY, this);
            view.showBoard(board);
        } else {
            selectedCard.runActions(Event.YOU_FLIP_SUMMONED_BY_ATTACK, this);
        }
    }


    public final void set() {
        if (ritualSummonSpellAddress != null) {
            view.printRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
            return;
        }
        if (selectedCard == null) {
            view.printSetMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        if (selectedCardAddress.getZone() != CardAddressZone.HAND) {
            view.printSetMessage(DuelMenuMessage.CANT_SET);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.printSetMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        Table playerTable = board.getPlayerTable();
        if (selectedCard instanceof Monster) {
            if (selectedCard.getType() == CardType.RITUAL) {
                view.printSetMessage(DuelMenuMessage.CANT_SET);
                return;
            }
            if (playerTable.isMonsterZoneFull()) {
                view.printSetMessage(DuelMenuMessage.MONSTER_ZONE_IS_FULL);
                return;
            }
            if (!playerTable.canSummonOrSet()) {
                view.printSetMessage(DuelMenuMessage.ALREADY_SUMMONED_SET);
                return;
            }
            removeCardFromHand(playerTable, selectedCardAddress.getPosition());
            addMonsterToTable((Monster) selectedCard, playerTable, CardState.HORIZONTAL_DOWN);
            playerTable.setCanSummonOrSet(false);
            view.printSetMessage(DuelMenuMessage.SET_SUCCESSFUL);
            view.showBoard(board);
        } else if (selectedCard instanceof Spell || selectedCard instanceof Trap) {
            if (selectedCard.getType() == CardType.FIELD) {
                removeCardFromHand(playerTable, selectedCardAddress.getPosition());
                Spell fieldSpell = board.getFieldSpell();
                if (fieldSpell != null) {
                    fieldSpell.runActions(Event.DISABLE_FIELD_SPELL, this);
                }
                board.setFieldSpell((Spell) selectedCard, CardState.VERTICAL_UP);
            } else {
                if (playerTable.isSpellTrapZoneFull()) {
                    view.printSetMessage(DuelMenuMessage.SPELL_ZONE_FULL);
                    return;
                }
                removeCardFromHand(playerTable, selectedCardAddress.getPosition());
                playerTable.addSpellOrTrap(selectedCard, CardState.VERTICAL_DOWN);
            }
            deselect(false);
            view.printSetMessage(DuelMenuMessage.SET_SUCCESSFUL);
            view.showBoard(board);
        } else {
            view.printSetMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        }
    }


    public final void changePosition(String position) {
        if (ritualSummonSpellAddress != null) {
            view.printRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
            return;
        }
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
        MonsterCell targetCell = board.getPlayerTable().getMonsterCell(selectedCardAddress.getPosition());
        if (selectedCardAddress.getZone() != CardAddressZone.MONSTER
                || targetCell.getState() == CardState.HORIZONTAL_DOWN
                || targetCell.getState() == CardState.VERTICAL_DOWN) {
            view.printChangePositionMessage(DuelMenuMessage.CANT_CHANGE_POSITION);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.printChangePositionMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        if ((targetState == CardState.VERTICAL_UP && targetCell.getState() != CardState.HORIZONTAL_UP)
                || (targetState == CardState.HORIZONTAL_UP && targetCell.getState() != CardState.VERTICAL_UP)) {
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
        view.showBoard(board);
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
        if (selectedCardAddress.getZone() != CardAddressZone.MONSTER || selectedCardAddress.isForOpponent()) {
            view.printAttackMessage(DuelMenuMessage.CANT_ATTACK, 0, null);
            return;
        }
        if (phase != Phase.BATTLE) {
            view.printAttackMessage(DuelMenuMessage.ACTION_NOT_ALLOWED, 0, null);
            return;
        }
        MonsterCell attackerCell = board.getPlayerTable().getMonsterCell(selectedCardAddress.getPosition());
        if (attackerCell.didAttack()) {
            view.printAttackMessage(DuelMenuMessage.ALREADY_ATTACKED, 0, null);
            return;
        }
        MonsterCell targetCell = board.getOpponentTable().getMonsterCell(targetPosition);
        if (targetCell == null || targetCell.getCard() == null) {
            view.printAttackMessage(DuelMenuMessage.NO_CARD_TO_ATTACK, 0, null);
            return;
        }
        if (attackerCell.getState() != CardState.VERTICAL_UP) {
            view.printAttackMessage(DuelMenuMessage.CANT_ATTACK, 0, null);
            return;
        }
        attackerCell.setDidAttack(true);
        checkQuickActivation(Event.DECLARE_ATTACK);
        if (!preventAttack) {
            this.attackedCardPosition = targetPosition;
            if (targetCell.getState() == CardState.VERTICAL_UP) {
                attackAttackPositionCard(targetPosition, attackerCell, targetCell);
            } else if (targetCell.getState() == CardState.HORIZONTAL_UP || targetCell.getState() == CardState.HORIZONTAL_DOWN) {
                attackDefensePositionCard(targetPosition, attackerCell, targetCell);
            }
        }
        this.attackedCardPosition = null;
        setPreventAttack(false);
        deselect(false);
    }

    private void attackAttackPositionCard(int targetPosition, MonsterCell attackerCell, MonsterCell targetCell) {
        Table attackerTable = board.getPlayerTable();
        Table targetTable = board.getOpponentTable();
        int attackerCardAttack = ((Monster) attackerCell.getCard()).getAttack();
        int targetCardAttack = ((Monster) targetCell.getCard()).getAttack();
        int damage = attackerCardAttack - targetCardAttack;
        if (damage > 0) {
            targetCell.getCard().runActions(Event.YOU_DESTROYED_BY_ATTACK, this);
            moveMonsterToGraveyard(targetTable, targetPosition);
            if (board.arePlayersImmune()) {
                damage = 0;
            }
            view.printAttackMessage(DuelMenuMessage.OPPONENT_ATTACK_POSITION_MONSTER_DESTROYED, damage, null);
            if (checkLifePoint(targetTable, attackerTable, damage)) {
                targetTable.decreaseLifePoint(damage);
            } else return;
        } else if (damage == 0) {
            targetCell.getCard().runActions(Event.YOU_DESTROYED_BY_ATTACK, this);
            attackerCell.getCard().runActions(Event.YOU_DESTROYED_WHILE_ATTACKING, this);
            moveMonsterToGraveyard(attackerTable, selectedCardAddress.getPosition());
            moveMonsterToGraveyard(targetTable, targetPosition);
            view.printAttackMessage(DuelMenuMessage.BOTH_ATTACK_POSITION_MONSTERS_DESTROYED, 0, null);
        } else {
            attackerCell.getCard().runActions(Event.YOU_DESTROYED_WHILE_ATTACKING, this);
            moveMonsterToGraveyard(attackerTable, selectedCardAddress.getPosition());
            damage = Math.abs(damage);
            if (board.arePlayersImmune()) {
                damage = 0;
            }
            view.printAttackMessage(DuelMenuMessage.YOUR_ATTACK_POSITION_MONSTER_DESTROYED, damage, null);
            if (checkLifePoint(attackerTable, targetTable, damage)) {
                attackerTable.decreaseLifePoint(damage);
            } else return;
        }
        board.setPlayersImmune(false);
        view.showBoard(board);
    }

    private void attackDefensePositionCard(int targetPosition, MonsterCell attackerCell, MonsterCell targetCell) {
        Table attackerTable = board.getPlayerTable();
        Table targetTable = board.getOpponentTable();
        String hiddenCardName = null;
        if (targetCell.getState() == CardState.HORIZONTAL_DOWN) {
            hiddenCardName = targetCell.getCard().getName();
            targetCell.setState(CardState.VERTICAL_UP);
            targetCell.setDoesPositionChanged(true);
        }
        int attackerCardAttack = ((Monster) attackerCell.getCard()).getAttack();
        int targetCardDefense = ((Monster) targetCell.getCard()).getDefence();
        int damage = attackerCardAttack - targetCardDefense;
        if (damage > 0) {
            view.printAttackMessage(DuelMenuMessage.OPPONENT_DEFENSE_POSITION_MONSTER_DESTROYED, 0, hiddenCardName);
            targetCell.getCard().runActions(Event.YOU_DESTROYED_BY_ATTACK, this);
            moveMonsterToGraveyard(targetTable, targetPosition);
        } else if (damage == 0) {
            view.printAttackMessage(DuelMenuMessage.NO_CARD_DESTROYED_AND_NO_DAMAGE, 0, hiddenCardName);
        } else {
            if (board.arePlayersImmune()) {
                damage = 0;
            }
            view.printAttackMessage(DuelMenuMessage.NO_CARD_DESTROYED_WITH_DAMAGE, Math.abs(damage), hiddenCardName);
            if (checkLifePoint(attackerTable, targetTable, damage)) {
                attackerTable.decreaseLifePoint(Math.abs(damage));
            } else return;
        }
        board.setPlayersImmune(false);
        view.showBoard(board);
    }


    public final void directAttack() {
        if (selectedCard == null) {
            view.printDirectAttackMessage(DuelMenuMessage.NO_CARD_IS_SELECTED, 0);
            return;
        }
        if (selectedCardAddress.getZone() != CardAddressZone.MONSTER) {
            view.printDirectAttackMessage(DuelMenuMessage.CANT_ATTACK, 0);
            return;
        }
        if (phase != Phase.BATTLE) {
            view.printDirectAttackMessage(DuelMenuMessage.ACTION_NOT_ALLOWED, 0);
            return;
        }
        MonsterCell attackerCell = board.getPlayerTable().getMonsterCell(selectedCardAddress.getPosition());
        if (attackerCell.didAttack()) {
            view.printDirectAttackMessage(DuelMenuMessage.ALREADY_ATTACKED, 0);
            return;
        }
        if (board.getOpponentTable().getMonsterCardsCount() != 0 || currentTurn == 1) {
            view.printDirectAttackMessage(DuelMenuMessage.CANT_ATTACK_DIRECTLY, 0);
            return;
        }
        Table opponentTable = board.getOpponentTable();
        int damage = ((Monster) attackerCell.getCard()).getAttack();
        attackerCell.setDidAttack(true);
        deselect(false);
        view.printDirectAttackMessage(DuelMenuMessage.DIRECT_ATTACK_SUCCESSFUL, damage);
        if (checkLifePoint(opponentTable, board.getPlayerTable(), damage)) {
            opponentTable.decreaseLifePoint(damage);
            view.showBoard(board);
        }
    }


    public final boolean checkLifePoint(Table table, Table otherTable, int damage) {
        if (damage >= table.getLifePoint()) {
            table.setLifePoint(0);
            win(otherTable, table);
            return false;
        }
        return true;
    }


    public final void checkActivateEffect() {
        if (ritualSummonSpellAddress != null) {
            view.printRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
            return;
        }
        if (selectedCard == null) {
            view.printActivateEffectMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        if (!(selectedCard instanceof Spell)) {
            view.printActivateEffectMessage(DuelMenuMessage.ONLY_FOR_SPELLS);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.printActivateEffectMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        Table table = board.getPlayerTable();
        if (selectedCardAddress.getZone() == CardAddressZone.HAND) {
            if (selectedCard.getType() != CardType.FIELD && table.isSpellTrapZoneFull()) {
                view.printActivateEffectMessage(DuelMenuMessage.SPELL_ZONE_FULL);
                return;
            }
            if (!selectedCard.canRunActions(Event.ACTIVATE_EFFECT, this)) {
                if (selectedCard.getType() == CardType.RITUAL) {
                    view.printRitualSummonMessage(DuelMenuMessage.NO_WAY_TO_RITUAL_SUMMON);
                } else {
                    view.printActivateEffectMessage(DuelMenuMessage.PREPARATIONS_NOT_DONE_YET);
                }
                return;
            }
            if (selectedCard.getType() != CardType.RITUAL) {
                removeCardFromHand(table, selectedCardAddress.getPosition());
                if (selectedCard.getType() == CardType.FIELD) {
                    Spell fieldSpell = board.getFieldSpell();
                    if (fieldSpell != null) {
                        fieldSpell.runActions(Event.DISABLE_FIELD_SPELL, this);
                    }
                    board.setFieldSpell((Spell) selectedCard, CardState.VERTICAL_UP);
                    selectedCardAddress = new CardAddress(CardAddressZone.FIELD, 1, false);
                } else {
                    table.addSpellOrTrap(selectedCard, CardState.VERTICAL_UP);
                    int position = table.getSpellOrTrapPosition(selectedCard);
                    selectedCardAddress = new CardAddress(CardAddressZone.SPELL, position, false);
                }
            }
        } else {
            SpellTrapCell cell;
            if (selectedCardAddress.getZone() == CardAddressZone.SPELL) {
                cell = table.getSpellOrTrapCell(selectedCardAddress.getPosition());
            } else {
                cell = table.getFieldSpellCell();
            }
            if (cell.isEffectActivated()) {
                view.printActivateEffectMessage(DuelMenuMessage.CARD_ALREADY_ACTIVATED);
                return;
            }
            if (!selectedCard.canRunActions(Event.ACTIVATE_EFFECT, this)) {
                view.printActivateEffectMessage(DuelMenuMessage.PREPARATIONS_NOT_DONE_YET);
                return;
            }
        }
        SpellTrapCell cell;
        if (selectedCard.getType() == CardType.FIELD) {
            cell = table.getFieldSpellCell();
        } else {
            cell = table.getSpellOrTrapCell(selectedCardAddress.getPosition());
        }
        activateSpellTrap(table, cell, selectedCard, selectedCardAddress.getPosition(), false);
    }

    private void checkQuickActivation(Event event) {
        ArrayList<SpellTrapCell> cells = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        ArrayList<Integer> positions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            SpellTrapCell cell = board.getOpponentTable().getSpellOrTrapCell(i);
            if (cell.getCard() == null) {
                continue;
            }
            for (Effect effect : cell.getCard().getEffects()) {
                if (effect.getEvent() == event && effect.getActionEnum() == ActionEnum.QUICK_ACTIVE) {
                    if (effect.getActionEnum().getAction().canBeRun(this)) {
                        cells.add(cell);
                        cards.add(cell.getCard());
                        positions.add(i);
                    }
                }
            }
        }
        if (cells.size() == 0) {
            return;
        }
        quickChangeTurn(false);
        String askActivateMessage = "do you want to activate your trap or spell?";
        String answer = view.getOneOfValues("yes", "no", askActivateMessage, "invalid input");
        if ("no".equals(answer)) {
            quickChangeTurn(false);
            return;
        }
        view.showCards(cards, "Card(s):");
        String getNumberMessage = "enter card number:";
        int index;
        while (true) {
            ArrayList<Integer> numbers = view.getNumbers(1, getNumberMessage);
            if (numbers == null) {
                view.printActionCanceled();
                quickChangeTurn(true);
                return;
            }
            index = numbers.get(0);
            if (index < 1 || index > cards.size()) {
                getNumberMessage = "invalid number";
            } else {
                break;
            }
        }
        index--;
        activateSpellTrap(board.getPlayerTable(), cells.get(index), cards.get(index), positions.get(index), true);
        quickChangeTurn(true);
    }

    private void activateSpellTrap(Table table, SpellTrapCell cell, Card card, int position, boolean isQuick) {
        cell.setEffectActivated(true);
        view.printActivateEffectMessage(DuelMenuMessage.SPELL_TRAP_ACTIVATED);
        if (isQuick) {
            card.runActions(Event.QUICK_ACTIVATE, this);
        } else {
            card.runActions(Event.ACTIVATE_EFFECT, this);
        }
        CardType type = card.getType();
        if (type != CardType.CONTINUOUS && type != CardType.FIELD && type != CardType.EQUIP) {
            table.moveSpellOrTrapToGraveyard(position);
        }
        if (!isQuick) {
            view.showBoard(board);
        }
    }


    public final void cancel() {
        if (ritualSummonSpellAddress != null) {
            ritualSummonSpellAddress = null;
            view.printCancelMessage(DuelMenuMessage.ACTION_CANCELED);
            return;
        }

        view.printCancelMessage(DuelMenuMessage.NOTHING_TO_CANCEL);
    }


    private void win(Table winnerTable, Table loserTable) {
        Spell fieldSpell = board.getFieldSpell();
        if (fieldSpell != null) {
            fieldSpell.runActions(Event.DISABLE_FIELD_SPELL, this);
        }
        board.setWinnerTable(winnerTable);
        board.setLoserTable(loserTable);

        int player1Score = 0;
        int player2Score = 0;
        for (Board board : boards) {
            if (board != null) {
                if (playerOne.equals(board.getWinnerTable().getOwner())) {
                    player1Score++;
                } else {
                    player2Score++;
                }
            }
        }

        boolean wonWholeMatch = player1Score == rounds / 2 + 1 || player2Score == rounds / 2 + 1;
        if (wonWholeMatch) {
            int maxLifePoint = getMaxLifePoint(winnerTable.getOwner());
            winnerTable.getOwner().increaseMoney(rounds * 1000L + maxLifePoint);
            winnerTable.getOwner().increaseScore(Math.max(player1Score, player2Score));
            loserTable.getOwner().increaseMoney(rounds * 100L);
            winnerTable.getOwner().increaseScore(Math.min(player1Score, player2Score));
        }
        view.printWinnerMessage(wonWholeMatch, winnerTable.getOwner().getUsername(), player1Score, player2Score);
        if (!wonWholeMatch) {
            this.reset();
            startNextRound();
        }
    }

    private int getMaxLifePoint(User user) {
        int maxLifePoint = 0;
        for (Board board : boards) {
            if (board != null) {
                if (user.equals(board.getWinnerTable().getOwner())) {
                    if (board.getWinnerTable().getLifePoint() > maxLifePoint) {
                        maxLifePoint = board.getWinnerTable().getLifePoint();
                    }
                } else {
                    if (board.getLoserTable().getLifePoint() > maxLifePoint) {
                        maxLifePoint = board.getWinnerTable().getLifePoint();
                    }
                }
            }
        }
        return maxLifePoint;
    }


    public final void surrender() {
        win(board.getOpponentTable(), board.getPlayerTable());
    }


    public final String getSelectedCardString() {
        if (selectedCard == null) {
            return "no card is selected yet";
        }
        if (selectedCardAddress.isForOpponent()) {
            CardAddress copyAddress = new CardAddress(selectedCardAddress.getZone(), selectedCardAddress.getPosition(), false);
            Cell cell = board.getOpponentTable().getCellByAddress(copyAddress);
            if (cell == null || cell.getState() == CardState.VERTICAL_DOWN || cell.getState() == CardState.HORIZONTAL_DOWN) {
                return "card is not visible";
            }
        }
        return selectedCard.detailedToString();
    }


    public final void exit() {
        Spell fieldSpell = board.getFieldSpell();
        if (fieldSpell != null) {
            fieldSpell.runActions(Event.DISABLE_FIELD_SPELL, this);
        }
    }


    public final void removeCardFromHand(Table table, int position) {
        table.removeCardFromHand(position);
        if (selectedCardAddress != null && position < selectedCardAddress.getPosition()) {
            selectedCardAddress.setPosition(selectedCardAddress.getPosition() - 1);
        }
    }

    public final void moveMonsterToGraveyard(Table table, int position) {
        table.moveMonsterToGraveyard(position);
        Spell fieldSpell = board.getFieldSpell();
        if (fieldSpell != null) {
            fieldSpell.runActions(Event.CHECK_FIELD_SPELL, this);
        }
    }

    public final void addMonsterToTable(Monster monster, Table table, CardState cardState) {
        table.addMonster(monster, cardState);
        Spell fieldSpell = board.getFieldSpell();
        if (fieldSpell != null) {
            fieldSpell.runActions(Event.ACTIVATE_EFFECT, this);
        }
    }


    public final void handleAI() {
        Table aiTable = board.getPlayerTable();
        Table opponentTable = board.getOpponentTable();

        goToNextPhase(true);
        goToNextPhase(true);

        handleAIMainPhase1(aiTable);

        HandleAiBattlePhase(aiTable, opponentTable);

        goToNextPhase(true);
        goToNextPhase(true);
    }

    private void handleAIMainPhase1(Table aiTable) {
        while (aiTable.getHand().size() > 0 && !aiTable.isMonsterZoneFull() && aiTable.canSummonOrSet()) {
            Monster monster = (Monster) aiTable.getCardFromHand(1);
            selectedCard = monster;
            selectedCardAddress = new CardAddress(CardAddressZone.HAND, 1, false);
            if (monster.getDefence() - monster.getAttack() > -100) {
                set();
            } else {
                checkSummon(false);
            }
        }
        goToNextPhase(true);
    }

    private void HandleAiBattlePhase(Table aiTable, Table opponentTable) {
        if (currentTurn == 1) {
            goToNextPhase(true);
            return;
        }
        for (int i = 1; i <= 5; i++) {
            MonsterCell monsterCell = aiTable.getMonsterCell(i);
            Card card = monsterCell.getCard();
            if (card == null) {
                continue;
            }
            CardState cardState = monsterCell.getState();
            if (cardState != CardState.VERTICAL_UP) {
                continue;
            }
            Monster monster = (Monster) card;
            boolean opponentHasMonster = false;
            for (int j = 1; j <= 5; j++) {
                MonsterCell targetCell = opponentTable.getMonsterCell(i);
                Card targetCard = targetCell.getCard();
                if (targetCard == null) {
                    continue;
                }
                opponentHasMonster = true;
                Monster targetMonster = (Monster) targetCard;
                CardState targetCardState = targetCell.getState();
                boolean attack;
                if (targetCardState == CardState.VERTICAL_UP) {
                    attack = monster.getAttack() >= targetMonster.getAttack();
                } else if (targetCardState == CardState.HORIZONTAL_UP) {
                    attack = monster.getAttack() > targetMonster.getDefence();
                } else {
                    attack = new Random().nextBoolean();
                }
                if (attack) {
                    selectedCard = monster;
                    selectedCardAddress = new CardAddress(CardAddressZone.MONSTER, i, false);
                    attack(j);
                }
            }
            if (!opponentHasMonster) {
                selectedCard = monster;
                selectedCardAddress = new CardAddress(CardAddressZone.MONSTER, i, false);
                directAttack();
            }
        }
        goToNextPhase(true);
    }


    public final void increaseLP(int amount) {
        board.getPlayerTable().increaseLifePoint(amount);
        view.showLPIncreased();
    }


    public final void setWinner(String nickname) {
        Table winnerTable;
        Table loserTable;
        if (nickname.equals(board.getPlayerTable().getOwner().getNickname())) {
            winnerTable = board.getPlayerTable();
            loserTable = board.getOpponentTable();
        } else if (nickname.equals(board.getOpponentTable().getOwner().getNickname())) {
            winnerTable = board.getOpponentTable();
            loserTable = board.getPlayerTable();
        } else {
            view.showSetWinnerMessage(DuelMenuMessage.INVALID_NICKNAME);
            return;
        }
        view.showSetWinnerMessage(DuelMenuMessage.WINNER_SET);
        win(winnerTable, loserTable);
    }
}
