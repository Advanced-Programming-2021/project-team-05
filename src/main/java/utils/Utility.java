package utils;

import model.card.Card;

import java.util.ArrayList;
import java.util.Scanner;

public class Utility {
    private static Scanner scanner;


    public static void initializeScanner() {
        scanner = new Scanner(System.in);
    }


    public static String getNextLine() {
        return scanner.nextLine();
    }


    public static void shuffleCards(ArrayList<Card> cards) {

    }


    public static int rollDice() {
        return 0;
    }


    public static boolean spinCoin() {
        return false;
    }


    public static int rockPaperScissors() {
        return 0;
    }
}
