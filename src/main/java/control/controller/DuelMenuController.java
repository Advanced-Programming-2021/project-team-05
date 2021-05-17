package control.controller;

import control.message.DuelMenuMessage;
import model.User;
import model.board.*;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.effect.Event;
import model.template.property.CardType;
import utils.CoinSide;
import utils.Utility;
import view.DuelMenuView;

import java.util.ArrayList;

public class DuelMenuController {

    private final User player1;
    private final User player2;
    private final Board[] boards;
    private final int rounds;
    private DuelMenuView view;
    private int currentRound;
    private Phase phase;
    private Board board;
    private Card selectedCard;
    private CardAddress selectedCardAddress;
    private Integer ritualSummonSpellPosition;


    public DuelMenuController(User player1, User player2, int rounds) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentRound = 0;
        this.rounds = rounds;
        this.boards = new Board[rounds];
    }


    public DuelMenuView getView() {
        return this.view;
    }

    public void setView(DuelMenuView view) {
        this.view = view;
    }


    public Board getBoard() {
        return this.board;
    }


    public int getRounds() {
        return this.rounds;
    }


    public Phase getPhase() {
        return this.phase;
    }


    public Card getSelectedCard() {
        return this.selectedCard;
    }

    private void setSelectedCard(Card card, CardAddress cardAddress) {
        this.selectedCard = card;
        this.selectedCardAddress = cardAddress;
    }


    public CardAddress getSelectedCardAddress() {
        return this.selectedCardAddress;
    }


    public void setRitualSummonSpellPosition(int position) {
        this.ritualSummonSpellPosition = position;
    }


    private void changeTurn() {
        board.swapTables();
    }


    public void goToNextPhase() {
        if (ritualSummonSpellPosition != null) {
            view.printRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
            return;
        }
        deselect(false);
        phase = phase.getNextPhase();
        view.showPhase(phase.getName());

        if (phase == Phase.DRAW) {
            if (board.getPlayerTable().getDeck().getMainDeckSize() == 0) {
                win(board.getPlayerTable(), board.getOpponentTable());
                return;
            }
            board.getPlayerTable().drawCard();
        } else if (phase == Phase.MAIN_1) {
            view.showBoard(board);
        } else if (phase == Phase.END) {
            changeTurn();
            view.showTurn(board.getPlayerTable().getOwner().getNickname());
        }
    }


    public void startNextRound() {
        CoinSide coinSide = Utility.flipCoin();
        User player;
        User opponent;
        if (coinSide == CoinSide.HEADS) {
            player = player1;
            opponent = player2;
        } else {
            player = player2;
            opponent = player1;
        }
        view.showFlipCoinResult(player1.getUsername(), coinSide);
        currentRound++;
        phase = Phase.DRAW;
        board = new Board(player, opponent);
        board.getPlayerTable().drawCard();
        boards[currentRound - 1] = board;
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


    public final void checkSummon(boolean isSpecial) {
        if (selectedCard == null) {
            view.printSummonMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
            return;
        }
        if (!(selectedCard instanceof Monster) ||
                selectedCardAddress.getZone() != CardAddressZone.HAND ||
                "Gate Guardian".equals(selectedCard.getName())) {
            view.printSummonMessage(DuelMenuMessage.CANT_SUMMON);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.printSummonMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        Monster card = (Monster) selectedCard;
        if (ritualSummonSpellPosition != null) {
            if (card.getType() != CardType.RITUAL) {
                view.printRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
                return;
            }
            tributeSummon(3, true);
            return;
        }
        Table playerTable = board.getPlayerTable();
        if (!isSpecial && !playerTable.canSummonOrSet()) {
            view.printSummonMessage(DuelMenuMessage.ALREADY_SUMMONED_SET);
            return;
        }
        if (card.getLevel() <= 4) {
            if (playerTable.isMonsterZoneFull()) {
                view.printSummonMessage(DuelMenuMessage.MONSTER_ZONE_IS_FULL);
                return;
            }
            summon(card, false);
        } else if (card.getLevel() <= 6) {
            if (playerTable.getMonsterCardsCount() == 0) {
                view.printSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
            } else {
                tributeSummon(1, false);
            }
        } else {
            if (playerTable.getMonsterCardsCount() <= 1) {
                view.printSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
            } else {
                tributeSummon(2, false);
            }
        }
    }

    public final void tributeSummon(int tributesCount, boolean isSpecial) {
        String message = "enter " + tributesCount + "number(s) for tribute positions";
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
        if (ritualSummonSpellPosition != null) {
            int levelsSum = 0;
            for (Monster tributeMonster : tributeCards) {
                levelsSum += tributeMonster.getLevel();
            }
            if (levelsSum != ((Monster) selectedCard).getLevel()) {
                view.printRitualSummonMessage(DuelMenuMessage.DONT_MATCH_WITH_RITUAL_MONSTER);
                return;
            }
            String stateString = view.getOneOfValues("attack", "defense", "enter monster state (attack/defense)", "invalid state");
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
            playerTable.moveMonsterToGraveyard(position);
        }

        if (ritualSummonSpellPosition != null) {
            ritualSummon((Monster) selectedCard, cardState);
        } else {
            summon((Monster) selectedCard, isSpecial);
        }
    }

    public void summon(Monster monster, boolean isSpecial) {
        Table playerTable = board.getPlayerTable();
        playerTable.addMonster(monster, CardState.VERTICAL_UP);
        if (!isSpecial) {
            playerTable.setCanSummonOrSet(false);
        }
        view.printTributeSummonMessage(DuelMenuMessage.SUMMON_SUCCESSFUL);
        view.showBoard(board);
        deselect(false);
    }

    private void ritualSummon(Monster monster, CardState state) {
        Table playerTable = board.getPlayerTable();
        playerTable.moveSpellOrTrapToGraveyard(ritualSummonSpellPosition);
        playerTable.addMonster(monster, state);
        view.printTributeSummonMessage(DuelMenuMessage.SUMMON_SUCCESSFUL);
        view.showBoard(board);
        deselect(false);
        ritualSummonSpellPosition = null;
    }


    public final void flipSummon() {
        if (ritualSummonSpellPosition != null) {
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

        targetCell.setState(CardState.VERTICAL_UP);
        targetCell.setDoesPositionChanged(true);
        view.printFlipSummonMessage(DuelMenuMessage.FLIP_SUMMON_SUCCESSFUL);
        selectedCard.runActions(Event.YOU_FLIP_SUMMONED, this);
        view.showBoard(board);
    }


    public final void set() {
        if (ritualSummonSpellPosition != null) {
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
            if (playerTable.isMonsterZoneFull()) {
                view.printSetMessage(DuelMenuMessage.MONSTER_ZONE_IS_FULL);
                return;
            }
            if (!playerTable.canSummonOrSet()) {
                view.printSetMessage(DuelMenuMessage.ALREADY_SUMMONED_SET);
                return;
            }
            playerTable.addMonster((Monster) selectedCard, CardState.HORIZONTAL_DOWN);
            playerTable.setCanSummonOrSet(false);
            view.printSetMessage(DuelMenuMessage.SET_SUCCESSFUL);
        } else if (selectedCard instanceof Spell || selectedCard instanceof Trap) {
            if (playerTable.isSpellTrapZoneFull()) {
                view.printSetMessage(DuelMenuMessage.SPELL_ZONE_FULL);
                return;
            }
            deselect(false);
            playerTable.addSpellOrTrap(selectedCard, CardState.VERTICAL_DOWN);
            view.printSetMessage(DuelMenuMessage.SET_SUCCESSFUL);
            view.showBoard(board);
        } else {
            view.printSetMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        }
    }


    public final void changePosition(String position) {
        if (ritualSummonSpellPosition != null) {
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
        if (selectedCardAddress.getZone() != CardAddressZone.MONSTER) {
            view.printChangePositionMessage(DuelMenuMessage.CANT_CHANGE_POSITION);
            return;
        }
        if (phase != Phase.MAIN_1 && phase != Phase.MAIN_2) {
            view.printChangePositionMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
            return;
        }
        MonsterCell targetCell = board.getPlayerTable().getMonsterCell(selectedCardAddress.getPosition());
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
        if (selectedCardAddress.getZone() != CardAddressZone.MONSTER) {
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
        view.showBoard(board);
    }

    private void attackAttackPositionCard(int targetPosition, Cell attackerCell, Cell targetCell) {
        Table attackerTable = board.getPlayerTable();
        Table targetTable = board.getOpponentTable();
        int attackerCardAttack = ((Monster) attackerCell.getCard()).getAttack();
        int targetCardAttack = ((Monster) targetCell.getCard()).getAttack();
        int damage = attackerCardAttack - targetCardAttack;
        if (damage > 0) {
            targetTable.moveMonsterToGraveyard(targetPosition);
            view.printAttackMessage(DuelMenuMessage.OPPONENT_ATTACK_POSITION_MONSTER_DESTROYED, damage, null);
            if (checkLifePoint(targetTable, attackerTable, damage)) {
                targetTable.decreaseLifePoint(damage);
            }
        } else if (damage == 0) {
            attackerTable.moveMonsterToGraveyard(selectedCardAddress.getPosition());
            targetTable.moveMonsterToGraveyard(targetPosition);
            view.printAttackMessage(DuelMenuMessage.BOTH_ATTACK_POSITION_MONSTERS_DESTROYED, 0, null);
        } else {
            damage = Math.abs(damage);
            attackerTable.moveMonsterToGraveyard(selectedCardAddress.getPosition());
            view.printAttackMessage(DuelMenuMessage.YOUR_ATTACK_POSITION_MONSTER_DESTROYED, damage, null);
            if (checkLifePoint(attackerTable, targetTable, damage)) {
                attackerTable.decreaseLifePoint(damage);
            }
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
            targetTable.moveMonsterToGraveyard(targetPosition);
            view.printAttackMessage(DuelMenuMessage.OPPONENT_DEFENSE_POSITION_MONSTER_DESTROYED, 0, hiddenCardName);
        } else if (damage == 0) {
            view.printAttackMessage(DuelMenuMessage.NO_CARD_DESTROYED_AND_NO_DAMAGE, 0, hiddenCardName);
        } else {
            view.printAttackMessage(DuelMenuMessage.NO_CARD_DESTROYED_WITH_DAMAGE, Math.abs(damage), hiddenCardName);
            if (checkLifePoint(attackerTable, targetTable, damage)) {
                attackerTable.decreaseLifePoint(Math.abs(damage));
            }
        }
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
        if (board.getOpponentTable().getMonsterCardsCount() == 0) {
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


    private boolean checkLifePoint(Table table, Table otherTable, int damage) {
        if (damage >= table.getLifePoint()) {
            table.setLifePoint(0);
            win(otherTable, table);
            return false;
        }
        return true;
    }


    private void win(Table winnerTable, Table loserTable) {
        board.setWinnerTable(winnerTable);
        board.setLoserTable(loserTable);

        int player1Score = 0;
        int player2Score = 0;
        for (Board board : boards) {
            if (board != null) {
                if (player1.getUsername().equals(board.getWinnerTable().getOwner().getUsername())) {
                    player1Score++;
                } else {
                    player2Score++;
                }
            }
        }

        boolean wonWholeMatch = player1Score == rounds / 2 + 1 || player2Score == rounds / 2 + 1;
        if (wonWholeMatch) {
            int maxLifePoint = getMaxLifePoint(winnerTable);
            winnerTable.getOwner().increaseMoney(rounds * 1000 + maxLifePoint);
            winnerTable.getOwner().increaseScore(Math.max(player1Score, player2Score));
            loserTable.getOwner().increaseMoney(rounds * 100);
            winnerTable.getOwner().increaseScore(Math.min(player1Score, player2Score));
        }
        view.printWinnerMessage(wonWholeMatch, winnerTable.getOwner().getUsername(), player1Score, player2Score);
        if (!wonWholeMatch) {
            startNextRound();
        }
    }

    private int getMaxLifePoint(Table table) {
        int maxLifePoint = 0;
        for (Board board : boards) {
            if (table.getOwner().getUsername().equals(board.getWinnerTable().getOwner().getUsername())) {
                if (board.getWinnerTable().getLifePoint() > maxLifePoint) {
                    maxLifePoint = board.getWinnerTable().getLifePoint();
                }
            }
        }
        return maxLifePoint;
    }


    public final void activeEffect() {
        if (ritualSummonSpellPosition != null) {
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
        }
        selectedCard.runActions(Event.ACTIVATE_EFFECT, this);
        view.showBoard(board);
    }


    public final String selectedCardToString() {
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
}
