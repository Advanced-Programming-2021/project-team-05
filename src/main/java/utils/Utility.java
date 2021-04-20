package utils;

import java.util.Random;
import java.util.Scanner;

public class Utility {
    private static Scanner scanner;


    public static void initializeScanner() {
        scanner = new Scanner(System.in);
    }


    public static String getNextLine() {
        return scanner.nextLine();
    }


    public static int rollDice() {
        Random rand = new Random();
        return rand.nextInt(6) + 1;
    }


    public static CoinSide spinCoin() {
        Random rand = new Random();
        return CoinSide.values()[rand.nextInt(2)];
    }
}


