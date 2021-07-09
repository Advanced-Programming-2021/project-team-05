package control.controller;

import control.DataManager;
import control.message.DuelMenuMessage;
import model.User;
import model.board.*;
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
import utils.ViewUtility;
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
        this.reset();
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.currentRound = 0;
        this.currentTurn = 1;
        this.rounds = rounds;
        this.boards = new Board[rounds];
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


    public final User getPlayerOne() {
        return this.playerOne;
    }


    public final Board getBoard() {
        return this.board;
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


    public void reset() {
        deselect();
        setRitualSummonSpell(null);
        setRitualSummonSpellAddress(null);
        setPreventAttack(false);
        this.attackedCardPosition = null;
    }


    public int getScore(User player) {
        int score = 0;
        for (Board board : boards) {
            if (board != null && board.getWinnerTable() != null)
                if (player.equals(board.getWinnerTable().getOwner())) score++;
        }
        return score;
    }


    public final void startNextRound() {
        currentRound++;
        initializeGame();
        view.showTurn(board.getPlayerTable().getOwner().getNickname(), false);
        if (currentRound != 1) rearrangeDeck();

        phase = Phase.DRAW;
        view.showPhase(phase);

        boards[currentRound - 1] = board;
        board.getPlayerTable().initializeHand();
        board.getOpponentTable().initializeHand();

        board.getPlayerTable().drawCard();
        view.updateBoard(board);
        if (isAi(board.getPlayerTable().getOwner())) handleAI();
    }

    private void initializeGame() {
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
            view.showFlipCoinResult(player.getNickname(), coinSide);
        } else {
            Board previousBoard = boards[currentRound - 2];
            player = previousBoard.getLoserTable().getOwner();
            opponent = previousBoard.getWinnerTable().getOwner();
        }
        board = new Board(player, opponent);
    }


    // TODO: 2021-07-09 change
    private void rearrangeDeck() {
        for (int i = 0; i < 2; i++) {
            Table targetTable = board.getPlayerTable();
            if (isAi(targetTable.getOwner())) continue;
            if (targetTable.getDeck().getSideDeckSize() == 0) {
                board.swapTables();
                view.showTurn(board.getPlayerTable().getOwner().getNickname(), false);
                continue;
            }
            ArrayList<Card> mainDeck = targetTable.getDeck().getMainDeck();
            ArrayList<Card> sideDeck = targetTable.getDeck().getSideDeck();
            while (true) {
                view.showCards(mainDeck, "Main Deck");
                view.showCards(sideDeck, "Side Deck");
                String message = "do you want to change your main deck? (yes/no)";
                String response = view.getOneOfValues("yes", "no", message, "invalid input");
                if ("no".equals(response)) break;

                int mainCardPosition = getCardPosition(mainDeck, "enter card number in main deck:");
                if (mainCardPosition == -1) continue;
                int sideCardPosition = getCardPosition(sideDeck, "enter card number in side deck:");
                if (sideCardPosition == -1) continue;

                targetTable.getDeck().swapCards(mainCardPosition, sideCardPosition);
                Card mainCard = mainDeck.get(mainCardPosition - 1);
                Card sideCard = sideDeck.get(sideCardPosition - 1);
                mainDeck.set(mainCardPosition - 1, sideCard);
                sideDeck.set(sideCardPosition - 1, mainCard);
            }
            board.swapTables();
            view.showTurn(board.getPlayerTable().getOwner().getNickname(), false);
        }
    }

    private int getCardPosition(ArrayList<Card> cards, String message) {
        int cardPosition;
        while (true) {
            ArrayList<Integer> positions = view.getNumbers(1, message);
            if (positions == null) return -1;
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
        this.currentTurn++;
        board.swapTables();
        board.getPlayerTable().reset();
        board.getOpponentTable().reset();
        view.showTurn(board.getPlayerTable().getOwner().getNickname(), false);
        view.updateBoard(board);
        view.updatePlayersInfo(board.getPlayerTable().getOwner(), board.getOpponentTable().getOwner());
    }

    public final void quickChangeTurn(boolean showBoard) {
        board.swapTables();
        view.showTurn(board.getPlayerTable().getOwner().getNickname(), true);
        if (showBoard) view.updateBoard(board);
    }


    public final void goToNextPhase(boolean showBoard) {
        if (ritualSummonSpellAddress != null) {
            view.showRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
            return;
        }
        deselect();
        phase = phase.getNextPhase();
        if (phase == Phase.DRAW) {
            changeTurn();
            view.showPhase(phase);
            if (board.getPlayerTable().getHand().size() < 6) {
                if (board.getPlayerTable().getDeck().getMainDeckSize() == 0) {
                    win(board.getPlayerTable(), board.getOpponentTable());
                    return;
                }
                board.getPlayerTable().drawCard();
                view.updateBoard(board);
            }
            if (isAi(board.getPlayerTable().getOwner())) handleAI();
            return;
        } else if (phase == Phase.MAIN_1 || phase == Phase.MAIN_2) {
            view.showPhase(phase);
            if (showBoard) view.updateBoard(board);
            return;
        }
        view.showPhase(phase);
    }


    public final void deselect() {
        if (selectedCard == null) return;
        selectedCard = null;
        selectedCardAddress = null;
        view.deselectCard();
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
                view.showTributeSummonMessage(DuelMenuMessage.INVALID_SELECTION);
        }
    }

    public void selectCardFromHand(Card card) {
        Table targetTable = board.getPlayerTable();
        ArrayList<Card> hand = targetTable.getHand();
        int position = -1;
        for (int i = 0, handSize = hand.size(); i < handSize; i++) {
            Card handCard = hand.get(i);
            if (card.equals(handCard)) {
                position = i + 1;
                break;
            }
        }
        if (position < 1) return;
        CardAddress address = new CardAddress(CardAddressZone.HAND, position, false);
        setSelectedCard(card, address);
    }

    private void selectCardFromHand(CardAddress cardAddress) {
        boolean isForOpponent = cardAddress.isForOpponent();
        if (isForOpponent) return;
        int position = cardAddress.getPosition();
        Table targetTable = board.getPlayerTable();
        int handSize = targetTable.getHand().size();
        if (position < 1 || position > handSize) return;
        setSelectedCard(targetTable.getCardFromHand(position), cardAddress);
    }

    private void selectCardFromMonsterZone(CardAddress cardAddress) {
        int position = cardAddress.getPosition();
        if (position < 1 || position > 5) return;
        Table targetTable = cardAddress.isForOpponent() ? board.getOpponentTable() : board.getPlayerTable();
        Card targetCard = targetTable.getMonster(position);
        if (targetCard == null) return;
        setSelectedCard(targetCard, cardAddress);
    }

    private void selectCardFromSpellZone(CardAddress cardAddress) {
        int position = cardAddress.getPosition();
        if (position < 1 || position > 5) return;
        Table targetTable = cardAddress.isForOpponent() ? board.getOpponentTable() : board.getPlayerTable();
        Card targetCard = targetTable.getSpellOrTrap(position);
        if (targetCard == null) return;
        setSelectedCard(targetCard, cardAddress);
    }

    private void selectCardFromFieldZone(CardAddress cardAddress) {
        Table targetTable = cardAddress.isForOpponent() ? board.getOpponentTable() : board.getPlayerTable();
        Card targetCard = targetTable.getFieldSpell();
        if (targetCard == null) return;
        setSelectedCard(targetCard, cardAddress);
    }


    public final void checkSummon(boolean isSpecial) {
        if (selectedCard == null) {
            view.showSummonMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        if (selectedCardAddress.getZone() != CardAddressZone.HAND || !(selectedCard instanceof Monster)) {
            view.showSummonMessage(DuelMenuMessage.CANT_SUMMON);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.showSummonMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        Table playerTable = board.getPlayerTable();
        Monster monster = (Monster) selectedCard;
        if (ritualSummonSpellAddress != null) {
            if (monster.getType() != CardType.RITUAL) {
                view.showRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
                return;
            }
            tributeSummonSet(3, false);
            return;
        }
        if (monster.getType() == CardType.RITUAL) {
            view.showSummonMessage(DuelMenuMessage.CANT_SUMMON);
            return;
        }
        if ("The Tricky".equals(monster.getName())) {
            String message = "Do you want to summon card normal or special?";
            String summonType = ViewUtility.getOneOfValues("Summon", "", message, "Normal", "Special");
            if (summonType == null) return;
            if ("Special".equals(summonType)) {
                if (playerTable.getHand().size() <= 1) {
                    view.showSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
                    return;
                }
                tributeSummonSetFromHand(false);
                return;
            }
        } else if ("Gate Guardian".equals(monster.getName())) {
            if (playerTable.getMonsterCardsCount() <= 2) {
                view.showSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
                return;
            }
            tributeSummonSet(3, false);
            return;
        }
        if (monster.getLevel() <= 4) {
            if (!isSpecial && !playerTable.canSummonOrSet()) {
                view.showSummonMessage(DuelMenuMessage.ALREADY_SUMMONED_SET);
                return;
            }
            if (playerTable.isMonsterZoneFull()) {
                view.showSummonMessage(DuelMenuMessage.MONSTER_ZONE_IS_FULL);
                return;
            }
            summon(monster, false);
        } else if (monster.getLevel() <= 6) {
            if (playerTable.getMonsterCardsCount() == 0) {
                view.showSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
                return;
            }
            tributeSummonSet(1, false);
        } else if (monster.getLevel() <= 8) {
            if (playerTable.getMonsterCardsCount() <= 1) {
                view.showSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
                return;
            }
            tributeSummonSet(2, false);
        }
    }

    public final void tributeSummonSet(int tributesCount, boolean set) {
        ArrayList<Monster> monsters = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        Table playerTable = board.getPlayerTable();
        for (int i = 1; i <= 5; i++) {
            Monster monster = playerTable.getMonster(i);
            if (monster != null) {
                monsters.add(monster);
                cards.add(monster);
            }
        }
        String message = "Select " + tributesCount + " monster(s) to tribute from monster zone";
        ArrayList<Integer> tributesPositions = view.getCardsPosition(cards, tributesCount, message);
        if (tributesPositions.size() == 0) return;

        ArrayList<Monster> tributeMonsters = new ArrayList<>();
        for (Integer position : tributesPositions) {
            tributeMonsters.add(monsters.get(position));
        }

        CardState cardState = CardState.VERTICAL_UP;
        if (ritualSummonSpellAddress != null) {
            int levelsSum = 0;
            for (Monster tributeMonster : tributeMonsters) {
                levelsSum += tributeMonster.getLevel();
            }
            if (levelsSum < ((Monster) selectedCard).getLevel()) {
                view.showRitualSummonMessage(DuelMenuMessage.DONT_MATCH_WITH_RITUAL_MONSTER);
                return;
            }
            String stateString = ViewUtility.getOneOfValues("Ritual Summon", "", "Select monster state", "Attack", "Defense");
            if (stateString == null) return;
            if ("Attack".equals(stateString)) cardState = CardState.VERTICAL_UP;
            else cardState = CardState.HORIZONTAL_UP;
        }

        for (Monster tributeMonster : tributeMonsters) {
            moveMonsterToGraveyard(playerTable, playerTable.getMonsterPosition(tributeMonster));
        }

        if (ritualSummonSpellAddress != null) ritualSummon((Monster) selectedCard, cardState);
        else if (set) setMonster(true);
        else summon((Monster) selectedCard, true);
    }

    private void tributeSummonSetFromHand(boolean set) {
        Table playerTable = board.getPlayerTable();
        ArrayList<Card> cards = new ArrayList<>(playerTable.getHand());
        int selectedCardIndex = cards.indexOf(selectedCard);
        cards.remove(selectedCard);
        String message = "Select Card to tribute from hand";
        ArrayList<Integer> tributesPositions = view.getCardsPosition(cards, 1, message);
        if (tributesPositions.size() == 0) return;
        ArrayList<Card> tributeCards = new ArrayList<>();
        for (int i = 0, tributesPositionsSize = tributesPositions.size(); i < tributesPositionsSize; i++) {
            Integer position = tributesPositions.get(i);
            tributeCards.add(cards.get(position));
            if (position >= selectedCardIndex) tributesPositions.set(i, position + 1);
        }

        for (int i = 0, tributeCardsSize = tributeCards.size(); i < tributeCardsSize; i++) {
            int position = tributesPositions.get(i) + 1;
            Card tributeCard = tributeCards.get(i);
            removeCardFromHand(playerTable, position);
            playerTable.addCardToGraveyard(tributeCard);
        }

        if (set) setMonster(true);
        else summon((Monster) selectedCard, true);
    }

    public final void summon(Monster monster, boolean isSpecial) {
        Table playerTable = board.getPlayerTable();
        removeCardFromHand(playerTable, selectedCardAddress.getPosition());
        selectedCardAddress.setZone(CardAddressZone.MONSTER);
        selectedCardAddress.setPosition(playerTable.getFirstEmptyMonsterCellPosition());
        addMonsterToTable(monster, playerTable, CardState.VERTICAL_UP);
        view.showSummonMessage(DuelMenuMessage.SUMMON_SUCCESSFUL);
        if (!isSpecial) {
            playerTable.setCanSummonOrSet(false);
            if (((Monster) selectedCard).getAttack() >= 1000)
                checkQuickActivation(Event.MONSTER_WITH_1000_ATTACK_SUMMONED);
        }
        checkQuickActivation(Event.MONSTER_SUMMONED);
        view.updateBoard(board);
        deselect();
    }

    private void ritualSummon(Monster monster, CardState state) {
        Table playerTable = board.getPlayerTable();
        playerTable.removeSpellOrTrap(ritualSummonSpellAddress.getPosition());
        playerTable.addCardToGraveyard(ritualSummonSpell);
        removeCardFromHand(playerTable, selectedCardAddress.getPosition());
        selectedCardAddress.setZone(CardAddressZone.MONSTER);
        selectedCardAddress.setPosition(playerTable.getFirstEmptyMonsterCellPosition());
        addMonsterToTable(monster, playerTable, state);
        view.showTributeSummonMessage(DuelMenuMessage.SUMMON_SUCCESSFUL);
        view.updateBoard(board);
        checkQuickActivation(Event.MONSTER_SUMMONED);
        deselect();
        ritualSummonSpellAddress = null;
    }


    public final void set() {
        if (ritualSummonSpellAddress != null) {
            view.showRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
            return;
        }
        if (selectedCard == null) {
            view.showSetMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        if (selectedCardAddress.getZone() != CardAddressZone.HAND) {
            view.showSetMessage(DuelMenuMessage.CANT_SET);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.showSetMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        Table playerTable = board.getPlayerTable();
        if (selectedCard instanceof Monster) {
            if (selectedCard.getType() == CardType.RITUAL) {
                view.showSetMessage(DuelMenuMessage.CANT_SET);
                return;
            }
            Monster monster = (Monster) selectedCard;
            if ("The Tricky".equals(monster.getName())) {
                String message = "Do you want to set card normal or special?";
                String summonType = ViewUtility.getOneOfValues("Set", "", message, "Normal", "Special");
                if (summonType == null) return;
                if ("Special".equals(summonType)) {
                    if (playerTable.getHand().size() <= 1) {
                        view.showSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
                        return;
                    }
                    tributeSummonSetFromHand(true);
                    return;
                }
            } else if ("Gate Guardian".equals(monster.getName())) {
                if (playerTable.getMonsterCardsCount() <= 2) {
                    view.showSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
                    return;
                }
                tributeSummonSet(3, true);
                return;
            }
            if (monster.getLevel() <= 4) {
                if (!playerTable.canSummonOrSet()) {
                    view.showSummonMessage(DuelMenuMessage.ALREADY_SUMMONED_SET);
                    return;
                }
                if (playerTable.isMonsterZoneFull()) {
                    view.showSummonMessage(DuelMenuMessage.MONSTER_ZONE_IS_FULL);
                    return;
                }
                setMonster(false);
            } else if (monster.getLevel() <= 6) {
                if (playerTable.getMonsterCardsCount() == 0) {
                    view.showSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
                    return;
                }
                tributeSummonSet(1, true);
            } else if (monster.getLevel() <= 8) {
                if (playerTable.getMonsterCardsCount() <= 1) {
                    view.showSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
                    return;
                }
                tributeSummonSet(2, true);
            }
        } else if (selectedCard instanceof Spell || selectedCard instanceof Trap) {
            if (selectedCard.getType() == CardType.FIELD) {
                removeCardFromHand(playerTable, selectedCardAddress.getPosition());
                Spell fieldSpell = board.getFieldSpell();
                if (fieldSpell != null) fieldSpell.runActions(Event.DISABLE_FIELD_SPELL, this);
                board.setFieldSpell((Spell) selectedCard, CardState.VERTICAL_DOWN);
            } else {
                if (playerTable.isSpellTrapZoneFull()) {
                    view.showSetMessage(DuelMenuMessage.SPELL_ZONE_FULL);
                    return;
                }
                removeCardFromHand(playerTable, selectedCardAddress.getPosition());
                playerTable.addSpellOrTrap(selectedCard, CardState.VERTICAL_DOWN);
            }
            deselect();
            view.showSetMessage(DuelMenuMessage.SET_SUCCESSFUL);
            view.updateBoard(board);
        } else view.showSetMessage(DuelMenuMessage.UNEXPECTED_ERROR);
    }

    private void setMonster(boolean isSpecial) {
        Table playerTable = board.getPlayerTable();
        removeCardFromHand(playerTable, selectedCardAddress.getPosition());
        addMonsterToTable((Monster) selectedCard, playerTable, CardState.HORIZONTAL_DOWN);
        if (!isSpecial) playerTable.setCanSummonOrSet(false);
        deselect();
        view.showSetMessage(DuelMenuMessage.SET_SUCCESSFUL);
        view.updateBoard(board);
    }


    public final void checkFlipSummon() {
        if (ritualSummonSpellAddress != null) {
            view.showRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
            return;
        }
        if (selectedCard == null) {
            view.showFlipSummonMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        if (selectedCardAddress.getZone() != CardAddressZone.MONSTER) {
            view.showFlipSummonMessage(DuelMenuMessage.CANT_CHANGE_POSITION);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.showFlipSummonMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        MonsterCell targetCell = board.getPlayerTable().getMonsterCell(selectedCardAddress.getPosition());
        if (targetCell.getState() != CardState.HORIZONTAL_DOWN || targetCell.isNewlyAdded()) {
            view.showFlipSummonMessage(DuelMenuMessage.CANT_FLIP_SUMMON);
            return;
        }
        flipSummon(targetCell);
    }

    private void flipSummon(MonsterCell targetCell) {
        targetCell.setState(CardState.VERTICAL_UP);
        targetCell.setDoesPositionChanged(true);
        view.showFlipSummonMessage(DuelMenuMessage.FLIP_SUMMON_SUCCESSFUL);
        if (((Monster) selectedCard).getAttack() >= 1000)
            checkQuickActivation(Event.MONSTER_WITH_1000_ATTACK_SUMMONED);
        selectedCard.runActions(Event.YOU_FLIP_SUMMONED_MANUALLY, this);
        view.updateBoard(board);
        deselect();
    }


    public final void changePosition(String position) {
        if (ritualSummonSpellAddress != null) {
            view.showRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
            return;
        }
        CardState targetState;
        if ("Attack".equals(position)) targetState = CardState.VERTICAL_UP;
        else targetState = CardState.HORIZONTAL_UP;
        if (selectedCard == null) {
            view.showChangePositionMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        MonsterCell targetCell = board.getPlayerTable().getMonsterCell(selectedCardAddress.getPosition());
        if (selectedCardAddress.getZone() != CardAddressZone.MONSTER
                || targetCell.getState() == CardState.HORIZONTAL_DOWN
                || targetCell.getState() == CardState.VERTICAL_DOWN) {
            view.showChangePositionMessage(DuelMenuMessage.CANT_CHANGE_POSITION);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.showChangePositionMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        if ((targetState == CardState.VERTICAL_UP && targetCell.getState() != CardState.HORIZONTAL_UP)
                || (targetState == CardState.HORIZONTAL_UP && targetCell.getState() != CardState.VERTICAL_UP)) {
            view.showChangePositionMessage(DuelMenuMessage.ALREADY_IN_WANTED_POSITION);
            return;
        }
        if (targetCell.doesPositionChanged()) {
            view.showChangePositionMessage(DuelMenuMessage.ALREADY_CHANGED_POSITION);
            return;
        }
        targetCell.setState(targetState);
        targetCell.setDoesPositionChanged(true);
        deselect();
        view.showChangePositionMessage(DuelMenuMessage.POSITION_CHANGED);
        view.updateBoard(board);
    }


    public final void attack(int targetPosition) {
        MonsterCell attackerCell = board.getPlayerTable().getMonsterCell(selectedCardAddress.getPosition());
        MonsterCell targetCell = board.getOpponentTable().getMonsterCell(targetPosition);
        attackerCell.setDidAttack(true);
        checkQuickActivation(Event.DECLARE_ATTACK);
        if (!preventAttack) {
            this.attackedCardPosition = targetPosition;
            if (targetCell.getState() == CardState.VERTICAL_UP)
                attackAttackPositionCard(targetPosition, attackerCell, targetCell);
            else if (targetCell.getState() == CardState.HORIZONTAL_UP
                    || targetCell.getState() == CardState.HORIZONTAL_DOWN)
                attackDefensePositionCard(targetPosition, attackerCell, targetCell);
        } else if (!isAi(board.getPlayerTable().getOwner())) view.turnOffAttackMode();
        this.attackedCardPosition = null;
        setPreventAttack(false);
        deselect();
        view.turnOffAttackMode();
    }

    public final boolean canSelectedCardAttack() {
        MonsterCell attackerCell = board.getPlayerTable().getMonsterCell(selectedCardAddress.getPosition());
        return currentTurn != 1 &&
                attackerCell.getState() == CardState.VERTICAL_UP &&
                !attackerCell.didAttack() &&
                board.getOpponentTable().getMonsterCardsCount() > 0;
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
            if (board.arePlayersImmune()) damage = 0;
            if (checkLifePoint(targetTable, attackerTable, damage)) targetTable.decreaseLifePoint(damage);
            else return;
        } else if (damage == 0) {
            targetCell.getCard().runActions(Event.YOU_DESTROYED_BY_ATTACK, this);
            attackerCell.getCard().runActions(Event.YOU_DESTROYED_WHILE_ATTACKING, this);
            moveMonsterToGraveyard(attackerTable, selectedCardAddress.getPosition());
            moveMonsterToGraveyard(targetTable, targetPosition);
        } else {
            attackerCell.getCard().runActions(Event.YOU_DESTROYED_WHILE_ATTACKING, this);
            moveMonsterToGraveyard(attackerTable, selectedCardAddress.getPosition());
            damage = Math.abs(damage);
            if (board.arePlayersImmune()) damage = 0;
            if (checkLifePoint(attackerTable, targetTable, damage)) attackerTable.decreaseLifePoint(damage);
            else return;
        }
        board.setPlayersImmune(false);
        view.updateBoard(board);
    }

    private void attackDefensePositionCard(int targetPosition, MonsterCell attackerCell, MonsterCell targetCell) {
        Table attackerTable = board.getPlayerTable();
        Table targetTable = board.getOpponentTable();
        if (targetCell.getState() == CardState.HORIZONTAL_DOWN) {
            targetCell.setState(CardState.VERTICAL_UP);
            targetCell.setDoesPositionChanged(true);
        }
        int attackerCardAttack = ((Monster) attackerCell.getCard()).getAttack();
        int targetCardDefense = ((Monster) targetCell.getCard()).getDefence();
        int damage = attackerCardAttack - targetCardDefense;
        if (damage > 0) {
            targetCell.getCard().runActions(Event.YOU_DESTROYED_BY_ATTACK, this);
            moveMonsterToGraveyard(targetTable, targetPosition);
        } else if (damage < 0) {
            if (board.arePlayersImmune()) damage = 0;
            if (checkLifePoint(attackerTable, targetTable, damage)) attackerTable.decreaseLifePoint(Math.abs(damage));
            else return;
        }
        board.setPlayersImmune(false);
        view.updateBoard(board);
    }


    public final void directAttack() {
        if (selectedCard == null) {
            view.showDirectAttackMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        MonsterCell attackerCell = board.getPlayerTable().getMonsterCell(selectedCardAddress.getPosition());
        if (selectedCardAddress.getZone() != CardAddressZone.MONSTER || attackerCell.getState() != CardState.VERTICAL_UP) {
            view.showDirectAttackMessage(DuelMenuMessage.CANT_ATTACK);
            return;
        }
        if (phase != Phase.BATTLE) {
            view.showDirectAttackMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        if (attackerCell.didAttack()) {
            view.showDirectAttackMessage(DuelMenuMessage.ALREADY_ATTACKED);
            return;
        }
        if (board.getOpponentTable().getMonsterCardsCount() != 0 || currentTurn == 1) {
            view.showDirectAttackMessage(DuelMenuMessage.CANT_ATTACK_DIRECTLY);
            return;
        }
        Table opponentTable = board.getOpponentTable();
        int damage = ((Monster) attackerCell.getCard()).getAttack();
        attackerCell.setDidAttack(true);
        checkQuickActivation(Event.DECLARE_ATTACK);
        if (preventAttack) {
            view.showDirectAttackMessage(DuelMenuMessage.ATTACK_PREVENTED);
            view.updateBoard(board);
        } else {
            if (checkLifePoint(opponentTable, board.getPlayerTable(), damage)) {
                opponentTable.decreaseLifePoint(damage);
                view.showDirectAttackMessage(DuelMenuMessage.DIRECT_ATTACK_SUCCESSFUL);
            }
        }
        deselect();
        setPreventAttack(false);
    }


    public final boolean checkLifePoint(Table attackedTable, Table otherTable, int damage) {
        if (damage >= attackedTable.getLifePoint()) {
            attackedTable.setLifePoint(0);
            view.showDirectAttackMessage(DuelMenuMessage.DIRECT_ATTACK_SUCCESSFUL);
            win(otherTable, attackedTable);
            return false;
        }
        return true;
    }


    public final void checkActivateEffect() {
        if (ritualSummonSpellAddress != null) {
            view.showRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
            return;
        }
        if (selectedCard == null) {
            view.showActivateEffectMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        if (!(selectedCard instanceof Spell)) {
            view.showActivateEffectMessage(DuelMenuMessage.ONLY_FOR_SPELLS);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.showActivateEffectMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        Table table = board.getPlayerTable();
        if (selectedCardAddress.getZone() == CardAddressZone.HAND) {
            if (selectedCard.getType() != CardType.FIELD && table.isSpellTrapZoneFull()) {
                view.showActivateEffectMessage(DuelMenuMessage.SPELL_ZONE_FULL);
                return;
            }
            if (!selectedCard.canRunActions(Event.ACTIVATE_EFFECT, this)) {
                if (selectedCard.getType() == CardType.RITUAL)
                    view.showRitualSummonMessage(DuelMenuMessage.NO_WAY_TO_RITUAL_SUMMON);
                else view.showActivateEffectMessage(DuelMenuMessage.PREPARATIONS_NOT_DONE_YET);
                return;
            }
            removeCardFromHand(table, selectedCardAddress.getPosition());
            if (selectedCard.getType() == CardType.FIELD) {
                Spell fieldSpell = board.getFieldSpell();
                if (fieldSpell != null) fieldSpell.runActions(Event.DISABLE_FIELD_SPELL, this);
                board.setFieldSpell((Spell) selectedCard, CardState.VERTICAL_UP);
                selectedCardAddress = new CardAddress(CardAddressZone.FIELD, 1, false);
            } else {
                table.addSpellOrTrap(selectedCard, CardState.VERTICAL_UP);
                int position = table.getSpellOrTrapPosition(selectedCard);
                selectedCardAddress = new CardAddress(CardAddressZone.SPELL, position, false);
                view.updateBoard(board);
            }
        } else {
            SpellTrapCell cell;
            if (selectedCardAddress.getZone() == CardAddressZone.SPELL)
                cell = table.getSpellOrTrapCell(selectedCardAddress.getPosition());
            else cell = table.getFieldSpellCell();
            if (cell.isEffectActivated()) {
                view.showActivateEffectMessage(DuelMenuMessage.CARD_ALREADY_ACTIVATED);
                return;
            }
            if (!selectedCard.canRunActions(Event.ACTIVATE_EFFECT, this)) {
                view.showActivateEffectMessage(DuelMenuMessage.PREPARATIONS_NOT_DONE_YET);
                return;
            }
        }
        SpellTrapCell cell;
        if (selectedCard.getType() == CardType.FIELD) cell = table.getFieldSpellCell();
        else cell = table.getSpellOrTrapCell(selectedCardAddress.getPosition());
        activateSpellTrap(table, cell, selectedCard, selectedCardAddress.getPosition(), false);
    }

    private void checkQuickActivation(Event event) {
        ArrayList<SpellTrapCell> cells = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        ArrayList<Integer> positions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            SpellTrapCell cell = board.getOpponentTable().getSpellOrTrapCell(i);
            if (cell.getCard() == null) continue;
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
        if (cells.size() == 0) return;
        quickChangeTurn(true);

        String askActivateMessage = "Do you want to quick activate you spell or trap?";
        boolean answer = ViewUtility.showConfirmationAlertAndReturn("Quick Activate", "", askActivateMessage, "No", "Yes");
        if (!answer) {
            quickChangeTurn(true);
            return;
        }

        ArrayList<Integer> trapPositions = view.getCardsPosition(cards, 1, "Select trap to activate");
        if (trapPositions.size() == 0) {
            quickChangeTurn(true);
            return;
        }
        int index = trapPositions.get(0);
        activateSpellTrap(board.getPlayerTable(), cells.get(index), cards.get(index), positions.get(index), true);
        quickChangeTurn(true);
    }

    private void activateSpellTrap(Table table, SpellTrapCell cell, Card card, int position, boolean isQuick) {
        cell.setEffectActivated(true);
        view.showActivateEffectMessage(DuelMenuMessage.SPELL_TRAP_ACTIVATED);
        if (isQuick) card.runActions(Event.QUICK_ACTIVATE, this);
        else card.runActions(Event.ACTIVATE_EFFECT, this);
        CardType type = card.getType();
        if (type != CardType.CONTINUOUS && type != CardType.FIELD && type != CardType.EQUIP && type != CardType.RITUAL)
            table.moveSpellOrTrapToGraveyard(position);
        if (type == CardType.FIELD)
            cell.setState(CardState.VERTICAL_UP);
        if (!isQuick) {
            deselect();
            view.updateBoard(board);
        }
    }


    private void win(Table winnerTable, Table loserTable) {
        Spell fieldSpell = board.getFieldSpell();
        if (fieldSpell != null) fieldSpell.runActions(Event.DISABLE_FIELD_SPELL, this);
        board.setWinnerTable(winnerTable);
        board.setLoserTable(loserTable);

        int player1Score = 0;
        int player2Score = 0;
        for (Board board : boards) {
            if (board != null) {
                if (playerOne.equals(board.getWinnerTable().getOwner())) player1Score++;
                else player2Score++;
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
        view.updateBoard(board);
        view.showWinner(wonWholeMatch, winnerTable.getOwner().getNickname(), player1Score, player2Score);
        if (wonWholeMatch) view.setEndDuel(true);
    }

    private int getMaxLifePoint(User user) {
        int maxLifePoint = 0;
        for (Board board : boards) {
            if (board != null) {
                if (user.equals(board.getWinnerTable().getOwner())) {
                    if (board.getWinnerTable().getLifePoint() > maxLifePoint)
                        maxLifePoint = board.getWinnerTable().getLifePoint();
                } else {
                    if (board.getLoserTable().getLifePoint() > maxLifePoint)
                        maxLifePoint = board.getWinnerTable().getLifePoint();
                }
            }
        }
        return maxLifePoint;
    }


    public final void surrender() {
        win(board.getOpponentTable(), board.getPlayerTable());
    }


    public final void exit() {
        view.setEndDuel(true);
        Spell fieldSpell = board.getFieldSpell();
        if (fieldSpell != null) fieldSpell.runActions(Event.DISABLE_FIELD_SPELL, this);
    }


    public final void removeCardFromHand(Table table, int position) {
        table.removeCardFromHand(position);
        if (selectedCardAddress != null && position < selectedCardAddress.getPosition())
            selectedCardAddress.setPosition(selectedCardAddress.getPosition() - 1);
    }

    public final void moveMonsterToGraveyard(Table table, int position) {
        table.moveMonsterToGraveyard(position);
        SpellTrapCell fieldSpellCell = board.getFieldSpellCell();
        if (fieldSpellCell != null && fieldSpellCell.isEffectActivated())
            fieldSpellCell.getCard().runActions(Event.CHECK_FIELD_SPELL, this);
    }

    public final void addMonsterToTable(Monster monster, Table table, CardState cardState) {
        table.addMonster(monster, cardState);
        SpellTrapCell fieldSpellCell = board.getFieldSpellCell();
        if (fieldSpellCell != null && fieldSpellCell.isEffectActivated())
            fieldSpellCell.getCard().runActions(Event.ACTIVATE_EFFECT, this);
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
            if (monster.getDefence() - monster.getAttack() > -100) set();
            else checkSummon(false);
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
            if (card == null) continue;
            CardState cardState = monsterCell.getState();
            if (cardState != CardState.VERTICAL_UP) continue;
            Monster monster = (Monster) card;
            boolean opponentHasMonster = false;
            for (int j = 1; j <= 5; j++) {
                MonsterCell targetCell = opponentTable.getMonsterCell(j);
                Card targetCard = targetCell.getCard();
                if (targetCard == null) continue;
                opponentHasMonster = true;
                Monster targetMonster = (Monster) targetCard;
                CardState targetCardState = targetCell.getState();
                boolean attack;
                if (targetCardState == CardState.VERTICAL_UP)
                    attack = monster.getAttack() >= targetMonster.getAttack();
                else if (targetCardState == CardState.HORIZONTAL_UP)
                    attack = monster.getAttack() > targetMonster.getDefence();
                else attack = new Random().nextBoolean();
                if (attack) {
                    selectedCard = monster;
                    selectedCardAddress = new CardAddress(CardAddressZone.MONSTER, i, false);
                    attack(j);
                    break;
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
        view.updateLPs(board.getPlayerTable().getLifePoint(), board.getOpponentTable().getLifePoint());
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
        } else return;
        win(winnerTable, loserTable);
    }
}
