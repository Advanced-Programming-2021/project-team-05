package controller;

import model.*;
import model.card.Card;
import view.DuelMenuView;

public class DuelMenuController {

    private User player;
    private User opponent;
    private int rounds;
    private Card selectedCard;
    private String phase;
    private Board board;
    private DuelMenuView view;


    public void setView(DuelMenuView view) {
        this.view = view;
    }

    public DuelMenuController(User user1, User user2) {
        this.board = new Board(user1, user2);
    }

    private void swapPlayers() {
        User temp = player;
        player = opponent;
        opponent = temp;
    }


    public final void deselect() {
        selectedCard = null;
    }


    public final void selectCard(CardAddress cardAddress) {

        switch (cardAddress.getZone()) {
            case HAND:
                if (cardAddress.getPosition() <= 6 && cardAddress.getPosition() >= 1) {
                    if (board.getPlayerTable().getHand().size() >= cardAddress.getPosition()) {
                        if (board.getPlayerTable().getHand().get(cardAddress.getPosition()) != null) {
                            this.selectedCard = board.getPlayerTable().getCardByAddress(cardAddress);

                        } else
                            view.printSelectMessage(DuelMenuMessage.CARD_SELECTED);
                    } else
                        view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
                } else
                    view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
            case MONSTER:
                if (cardAddress.getPosition() <= 5 && cardAddress.getPosition() >= 1) {
                   if (cardAddress.isForOpponent()){
                       if (board.getOpponentTable().getHand().get(cardAddress.getPosition()) != null) {
                           this.selectedCard = board.getOpponentTable().getCardByAddress(cardAddress);
                           view.printSelectMessage(DuelMenuMessage.CARD_SELECTED);
                       } else
                           view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);

                   }
                   else {
                       if (board.getPlayerTable().getHand().get(cardAddress.getPosition()) != null) {
                           this.selectedCard = board.getPlayerTable().getCardByAddress(cardAddress);
                           view.printSelectMessage(DuelMenuMessage.CARD_SELECTED);
                       } else
                           view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);

                   }
                } else
                    view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);


            case SPELL:
                if (cardAddress.getPosition() <= 5 && cardAddress.getPosition() >= 1) {
                    if (cardAddress.isForOpponent()){
                        if (board.getOpponentTable().getHand().get(cardAddress.getPosition()) != null) {
                            this.selectedCard = board.getOpponentTable().getCardByAddress(cardAddress);
                            view.printSelectMessage(DuelMenuMessage.CARD_SELECTED);
                        } else
                            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
                    }
                    else {
                        if (board.getPlayerTable().getHand().get(cardAddress.getPosition()) != null) {
                            this.selectedCard = board.getPlayerTable().getCardByAddress(cardAddress);
                            view.printSelectMessage(DuelMenuMessage.CARD_SELECTED);
                        } else
                            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
                    }
                } else
                    view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);

            case FIELD:
                    if (cardAddress.isForOpponent()){
                        if (board.getOpponentTable().getHand().get(cardAddress.getPosition()) != null) {
                            this.selectedCard = board.getOpponentTable().getCardByAddress(cardAddress);
                            view.printSelectMessage(DuelMenuMessage.CARD_SELECTED);
                        }
                    else {
                        if (board.getPlayerTable().getHand().get(cardAddress.getPosition()) != null) {
                            this.selectedCard = board.getPlayerTable().getCardByAddress(cardAddress);
                            view.printSelectMessage(DuelMenuMessage.CARD_SELECTED);
                        } else
                            view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
                    }
                } else
                    view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);

        }

    }


    public final void summon() {

    }


    public final void set() {

    }


    public final void changePosition(String position) {

    }


    public final void flipSummon() {

    }


    public final void attack(int position) {

    }


    public final void directAttack() {

    }


    public final void activeEffect() {

    }
}
