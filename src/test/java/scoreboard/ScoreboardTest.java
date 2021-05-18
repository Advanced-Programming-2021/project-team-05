package scoreboard;

import control.DataManager;
import control.controller.MainMenuController;
import model.User;
import org.junit.jupiter.api.*;
import utils.TestUtility;
import utils.Utility;
import view.MainMenuView;
import view.ScoreboardMenuView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class ScoreboardTest {
    private static final PrintStream originalOut = System.out;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();


    private void assertOutputIsEqual(String expectedOutput) {
        Assertions.assertEquals(expectedOutput, outContent.toString().trim());
        outContent.reset();
    }


    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }


    @BeforeEach
    public void resetUpStreams() {
        outContent.reset();
    }


    @Test
    public void showScoreboardTest() {
        ScoreboardMenuView view = new ScoreboardMenuView();

        User user = new User("name0", "pass0", "nick0");
        User user1 = new User("name1", "pass1", "nick1");
        User user2 = new User("name2", "pass2", "nick2");
        User user3 = new User("name3", "pass3", "nick3");
        User user4 = new User("name4", "pass4", "nick4");
        User user5 = new User("name5", "pass5", "nick5");
        User user6 = new User("name6", "pass6", "nick6");
        User user7 = new User("name7", "pass7", "nick7");

        DataManager manager = DataManager.getInstance();
        manager.addUser(user);
        manager.addUser(user1);
        manager.addUser(user2);
        manager.addUser(user3);
        manager.addUser(user4);
        manager.addUser(user5);
        manager.addUser(user6);
        manager.addUser(user7);

        user.increaseScore(1000);
        user1.increaseScore(1500);
        user2.increaseScore(3000);
        user3.increaseScore(3000);
        user4.increaseScore(500);
        user6.increaseScore(500);
        user7.increaseScore(500);

        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> outputs = new ArrayList<>();

        commands.add("menu enter Deck");
        outputs.add("menu navigation is not possible");

        commands.add("scoreboard   ");
        outputs.add("invalid command");

        commands.add("scoreboard show");
        outputs.add("1. nick2: 3000\r\n" +
                "1. nick3: 3000\r\n" +
                "3. nick1: 1500\r\n" +
                "4. nick0: 1000\r\n" +
                "5. nick4: 500\r\n" +
                "5. nick6: 500\r\n" +
                "5. nick7: 500\r\n" +
                "8. nick5: 0");

        commands.add("menu help");
        outputs.add("commands:\r\n" +
                        "\tscoreboard show\r\n" +
                        "\tmenu show-current\r\n" +
                        "\tmenu exit\r\n" +
                        "\tmenu help");

        commands.add("menu exit");

        StringBuilder commandsStringBuilder = new StringBuilder();
        for (String command : commands) {
            commandsStringBuilder.append(command).append("\n");
        }

        StringBuilder outputsStringBuilder = new StringBuilder();
        for (String output : outputs) {
            outputsStringBuilder.append(output).append("\r\n");
        }

        InputStream stdIn = TestUtility.giveInput(commandsStringBuilder.toString());
        Utility.initializeScanner();
        view.run();

        assertOutputIsEqual(outputsStringBuilder.toString().trim());
        System.setIn(stdIn);
    }


    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }
}
