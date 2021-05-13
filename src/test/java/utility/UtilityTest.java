package utility;

import org.junit.jupiter.api.*;
import utils.RockPaperScissors;
import utils.TestUtility;
import utils.Utility;

import java.io.InputStream;

public class UtilityTest {
    @Test
    public void diceRangeTest() {
        for (int i = 0; i < 1000; i++) {
            int result = Utility.rollDice();
            Assertions.assertTrue(result <= 6 && result >= 1);
        }
    }


    @Test
    public void spinCoinTest() {
        for (int i = 0; i < 1000; i++) {
            Assertions.assertNotNull(Utility.spinCoin());
        }
    }


    @Test
    public void swapElementsTest() {
        String[] arrayInput1 = {"apple", "banana", "orange"};
        String[] arrayOutput1 = {"banana", "apple", "orange"};
        Utility.swapElements(arrayInput1, 0, 1);
        Assertions.assertArrayEquals(arrayInput1, arrayOutput1);

        String[] arrayInput2 = {"apple"};
        String[] arrayOutput2 = {"apple"};
        Utility.swapElements(arrayInput2, 0, 0);
        Assertions.assertArrayEquals(arrayInput2, arrayOutput2);

        String[] arrayInput3 = {"apple", "banana", "orange"};
        String[] arrayOutput3 = {"orange", "apple", "banana"};
        Utility.swapElements(arrayInput3, 0, 1);
        Utility.swapElements(arrayInput3, 0, 2);
        Assertions.assertArrayEquals(arrayInput3, arrayOutput3);
    }


    @Test
    public void reverseArrayTest() {
        String[] arrayInput1 = {"apple", "banana", "orange"};
        String[] arrayOutput1 = {"apple", "banana", "orange"};
        Utility.reverseArray(arrayInput1);
        Utility.reverseArray(arrayInput1);
        Assertions.assertArrayEquals(arrayInput1, arrayOutput1);

        String[] arrayInput2 = {"apple"};
        String[] arrayOutput2 = {"apple"};
        Utility.reverseArray(arrayInput2);
        Assertions.assertArrayEquals(arrayInput2, arrayOutput2);

        String[] arrayInput3 = {"apple", "banana", "orange", "tomato", "ununennium"};
        String[] arrayOutput3 = {"ununennium", "tomato", "orange", "banana", "apple"};
        Utility.reverseArray(arrayInput3);
        Assertions.assertArrayEquals(arrayInput3, arrayOutput3);
    }


    @Test
    public void joinArrayTest() {
        String output = "";

        String[] arrayInput1 = {"a", "b", "c"};
        String output1 = "azbzc";
        output = Utility.joinArray(arrayInput1, 'z');
        Assertions.assertEquals(output1, output);

        String[] arrayInput2 = {"a"};
        String output2 = "a";
        output = Utility.joinArray(arrayInput2, 'z');
        Assertions.assertEquals(output2, output);

        String[] arrayInput3 = {"a", "b", "c", "zz", " ", "\n", "2"};
        String output3 = "azbzczzzz z\nz2";
        output = Utility.joinArray(arrayInput3, 'z');
        Assertions.assertEquals(output3, output);
    }


    @Test
    public void initializeScannerTest() {
        boolean thrown = false;
        try {
            Utility.getNextLine();
        } catch (NullPointerException exception) {
            thrown = true;
        }
        Assertions.assertTrue(thrown);

        thrown = false;
        try {
            InputStream stdIn = TestUtility.giveInput("input");
            Utility.initializeScanner();
            Utility.getNextLine();
            System.setIn(stdIn);
        } catch (NullPointerException exception) {
            thrown = true;
        }
        Assertions.assertFalse(thrown);
    }


    @Test
    public void getNextLineTest() {
        InputStream stdIn = TestUtility.giveInput("Hello!\nHow are you?\n\nBye!");
        Utility.initializeScanner();
        Assertions.assertEquals("Hello!", Utility.getNextLine());
        Assertions.assertEquals("How are you?", Utility.getNextLine());
        Assertions.assertEquals("", Utility.getNextLine());
        Assertions.assertEquals("Bye!", Utility.getNextLine());
        System.setIn(stdIn);
    }

    @Test
    public void getValueOfTest() {
        RockPaperScissors rock = RockPaperScissors.getValueOf("rOcK");
        RockPaperScissors paper = RockPaperScissors.getValueOf("paper");
        RockPaperScissors scissors = RockPaperScissors.getValueOf("SCISSORS");
        RockPaperScissors nullPointer = RockPaperScissors.getValueOf("rcK");

        Assertions.assertEquals(rock, RockPaperScissors.ROCK);
        Assertions.assertEquals(paper, RockPaperScissors.PAPER);
        Assertions.assertEquals(scissors, RockPaperScissors.SCISSORS);
        Assertions.assertNull(nullPointer);
    }

    @Test
    public void compareTest() {
        int paperVsPaper = RockPaperScissors.compare(RockPaperScissors.PAPER, RockPaperScissors.PAPER);
        int paperVsRock = RockPaperScissors.compare(RockPaperScissors.PAPER, RockPaperScissors.ROCK);
        int paperVsScissors = RockPaperScissors.compare(RockPaperScissors.PAPER, RockPaperScissors.SCISSORS);
        int rockVsPaper = RockPaperScissors.compare(RockPaperScissors.ROCK, RockPaperScissors.PAPER);
        int rockVsRock = RockPaperScissors.compare(RockPaperScissors.ROCK, RockPaperScissors.ROCK);
        int rockVsScissors = RockPaperScissors.compare(RockPaperScissors.ROCK, RockPaperScissors.SCISSORS);
        int scissorsVsPaper = RockPaperScissors.compare(RockPaperScissors.SCISSORS, RockPaperScissors.PAPER);
        int scissorsVsRock = RockPaperScissors.compare(RockPaperScissors.SCISSORS, RockPaperScissors.ROCK);
        int scissorsVsScissors = RockPaperScissors.compare(RockPaperScissors.SCISSORS, RockPaperScissors.SCISSORS);
        int nullPointer = RockPaperScissors.compare(null, null);

        boolean answer = paperVsPaper == 0 && paperVsRock == -1 && paperVsScissors == 1 &&
                rockVsPaper == 1 && rockVsRock == 0 && rockVsScissors == -1 &&
                scissorsVsPaper == -1 && scissorsVsRock == 1 && scissorsVsScissors == 0 &&
                nullPointer == 0;
        Assertions.assertTrue(answer);
    }
}
