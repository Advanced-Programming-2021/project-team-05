package controller;

import model.User;
import model.card.Card;

public class DuelMenuController {

    private User player;
    private User opponent;
    private int rounds;
    private Card selectedCard;
    private String phase;


    private void swapPlayers() {
        User temp = player;
        player = opponent;
        opponent = temp;
    }


    public final void deselect() {
        selectedCard = null;
    }


    public final void selectCard(String zone, int position) {

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
