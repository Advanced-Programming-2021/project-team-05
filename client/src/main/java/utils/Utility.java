package utils;

import java.util.Random;

public class Utility {

    public static void reverseArray(String[] array) {
        for (int i = 0, length = array.length; i < length / 2; i++) {
            swapElements(array, i, length - i - 1);
        }
    }

    public static void swapElements(String[] array, int index_1, int index_2) {
        String temp = array[index_1];
        array[index_1] = array[index_2];
        array[index_2] = temp;
    }

    public static String joinArray(String[] array, char joinChar) {
        StringBuilder tableString = new StringBuilder();
        for (String row : array) {
            tableString.append(row).append(joinChar);
        }
        tableString.delete(tableString.length() - 1, tableString.length());

        return tableString.toString();
    }


    public static CoinSide flipCoin() {
        Random rand = new Random();
        return CoinSide.values()[rand.nextInt(2)];
    }
}


