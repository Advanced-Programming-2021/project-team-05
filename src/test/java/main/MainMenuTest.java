package main;

import controller.MainMenuController;
import model.User;
import org.junit.jupiter.api.*;
import utils.TestUtility;
import utils.Utility;
import view.MainMenuView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class MainMenuTest {
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
    public void enterMenuTest() {
        User user = new User("name", "pass", "nick");
        MainMenuView view = new MainMenuView(new MainMenuController(user));

        view.enterMenu(new String[]{"menu", "enter"});
        assertOutputIsEqual("invalid command");

        view.enterMenu(new String[]{"menu", "enter", "a", "Menu"});
        assertOutputIsEqual("menu name is not valid");

        view.enterMenu(new String[]{"menu", "enter", "Menu"});
        assertOutputIsEqual("menu name is not valid");

        view.enterMenu(new String[]{"menu", "enter", "test test", "Menu"});
        assertOutputIsEqual("menu name is not valid");

        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> outputs = new ArrayList<>();

        commands.add("menu enter Deck Menu");
        commands.add("menu show-current");
        outputs.add("Deck Menu");
        commands.add("menu exit");

        commands.add("menu enter Scoreboard Menu");
        commands.add("menu show-current");
        outputs.add("Scoreboard Menu");
        commands.add("menu exit");

        commands.add("menu enter Profile Menu");
        commands.add("menu show-current");
        outputs.add("Profile Menu");
        commands.add("menu exit");

        commands.add("menu enter Shop Menu");
        commands.add("menu show-current");
        outputs.add("Shop Menu");
        commands.add("menu exit");

        commands.add("menu enter Import/Export Menu");
        commands.add("menu show-current");
        outputs.add("Import/Export Menu");
        commands.add("menu exit");

        commands.add("user logout");
        outputs.add("user logged out successfully!");

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
