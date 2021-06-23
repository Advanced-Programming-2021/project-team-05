package main;

import control.DataManager;
import control.controller.MainMenuController;
import control.message.MainMenuMessage;
import model.Deck;
import model.User;
import model.card.Card;
import model.card.Monster;
import model.template.MonsterTemplate;
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
        assertOutputIsEqual("invalid command");

        view.enterMenu(new String[]{"menu", "enter", "test test", "Menu"});
        assertOutputIsEqual("menu name is not valid");

        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> outputs = new ArrayList<>();

        commands.add("menu enter Deck Menu");
        outputs.add("separate card name words with '_'. example: Battle_OX");
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
        outputs.add("separate card name words with '_'. example: Battle_OX");
        commands.add("menu show-current");
        outputs.add("Shop Menu");
        commands.add("menu exit");

        commands.add("menu enter Import/Export Menu");
        outputs.add("separate card name words with '_'. example: Battle_OX");
        commands.add("menu show-current");
        outputs.add("Import/Export Menu");
        commands.add("menu exit");

        commands.add("menu help");
        outputs.add("commands:\r\n" +
                        "\tduel --new --second-player <player2 username> --rounds <1/3>\r\n" +
                        "\tduel --new --ai --rounds <1/3>\r\n" +
                        "\tuser logout\r\n" +
                        "\tmenu show-current\r\n" +
                        "\tmenu enter <menu name>\r\n" +
                        "\tmenu help");

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


    @Test
    public void runTest() {
        User user = new User("name", "pass", "nick");
        MainMenuView view = new MainMenuView(new MainMenuController(user));

        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> outputs = new ArrayList<>();

        commands.add("duel mmd ali sdq");
        outputs.add("invalid command");

        commands.add("menu enter    Menu");
        outputs.add("invalid command");

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


    @Test
    public void printStartDuelMessageTest() {
        User user = new User("name", "pass", "nick");
        MainMenuView view = new MainMenuView(new MainMenuController(user));

        String username = "mmd";

        view.printStartDuelMessage(MainMenuMessage.NO_PLAYER_EXISTS, username);
        assertOutputIsEqual("there is no player with this username");

        view.printStartDuelMessage(MainMenuMessage.NO_ACTIVE_DECK, username);
        assertOutputIsEqual(username + " has no active deck");

        view.printStartDuelMessage(MainMenuMessage.INVALID_DECK, username);
        assertOutputIsEqual(username + "'s deck is invalid");

        view.printStartDuelMessage(MainMenuMessage.INVALID_ROUND, username);
        assertOutputIsEqual("number of rounds is not supported");

        view.printStartDuelMessage(MainMenuMessage.GAME_STARTED_SUCCESSFULLY, username);
        assertOutputIsEqual("unexpected error");

        view.printStartDuelMessage(MainMenuMessage.ERROR, username);
        assertOutputIsEqual("unexpected error");
    }


    @Test
    public void startDuelTest() {
        User user = new User("name", "pass", "nick");
        MainMenuView view = new MainMenuView(new MainMenuController(user));

        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> outputs = new ArrayList<>();

        commands.add("duel --new --second-player mmd 3 --rounds");
        outputs.add("invalid command");

        commands.add("duel --new --second-player mmd --rounds 3 1");
        outputs.add("invalid command");

        commands.add("duel --new --ai --rounds 1 2");
        outputs.add("invalid command");

        commands.add("duel --ai --new --rounds 1 2");
        outputs.add("invalid command");

        commands.add("duel --second-player mmd --rounds 2");
        outputs.add("invalid command");

        commands.add("duel --new --second-player mmd");
        outputs.add("invalid command");

        commands.add("duel --rounds 1 --second-player mmd --new");
        outputs.add("there is no player with this username");

        commands.add("duel --new --rounds 2 --ai");
        outputs.add("name has no active deck");

        commands.add("menu enter    Menu");
        outputs.add("invalid command");

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


    @Test
    public void startDuelWithUserTest() {
        DataManager manager = DataManager.getInstance();
        manager.loadData();

        String myName = "myName";
        String opName = "opponentsName";
        String myNick = "myNickname";
        String opNick = "opponentsNickname";
        User myUser = new User(myName, "myPassword", myNick);
        User opponentsUser = new User(opName, "opponentsPass", opNick);

        manager.addUser(myUser);
        manager.addUser(opponentsUser);
        MainMenuController controller = new MainMenuController(myUser);
        MainMenuView view = new MainMenuView(controller);

        controller.startDuelWithUser("mmd", 1);
        assertOutputIsEqual("there is no player with this username");

        controller.startDuelWithUser(opName, 1);
        assertOutputIsEqual(myName + " has no active deck");

        Deck myDeck = new Deck("my active deck");
        myDeck.setId("1");
        manager.addDeck(myDeck);
        myUser.setActiveDeck(myDeck);

        controller.startDuelWithUser(opName, 1);
        assertOutputIsEqual(opName + " has no active deck");

        Deck opponentsDeck = new Deck("opponents active deck");
        opponentsDeck.setId("2");
        manager.addDeck(opponentsDeck);
        opponentsUser.setActiveDeck(opponentsDeck);

        controller.startDuelWithUser(opName, 1);
        assertOutputIsEqual(myName + "'s deck is invalid");

        for (int i = 0; i < 41; i++) {
            Card card = new Monster((MonsterTemplate) manager.getCardTemplateByName("Wattkid"));
            myDeck.addCardToMainDeck(card);
            manager.addCard(card);
        }

        controller.startDuelWithUser(opName, 1);
        assertOutputIsEqual(opName + "'s deck is invalid");

        for (int i = 0; i < 41; i++) {
            Card card = new Monster((MonsterTemplate) manager.getCardTemplateByName("Wattkid"));
            opponentsDeck.addCardToMainDeck(card);
            manager.addCard(card);
        }

        controller.startDuelWithUser(opName, 2);
        assertOutputIsEqual("number of rounds is not supported");

        boolean tails = true;
        boolean heads = true;

        while (tails || heads) {
            InputStream stdIn = TestUtility.giveInput("menu exit\nuser logout\nmenu exit");
            Utility.initializeScanner();

            controller.startDuelWithUser(opName, 1);
            String output = outContent.toString().trim();
            outContent.reset();

            if (output.contains("tails")) {
                tails = false;
                Assertions.assertEquals("coin side was tails and " + opNick + " starts duel\r\n" +
                        "its " + opNick + "'s turn\r\n" +
                        "phase: draw phase\r\n" +
                        "you drew \"Wattkid\" from your deck", output);
            } else {
                heads = false;
                Assertions.assertEquals("coin side was heads and " + myNick + " starts duel\r\n" +
                        "its " + myNick + "'s turn\r\n" +
                        "phase: draw phase\r\n" +
                        "you drew \"Wattkid\" from your deck", output);
            }

            view.run();

            assertOutputIsEqual("user logged out successfully!");
            System.setIn(stdIn);
        }
    }


    @Test
    public void startDuelWithAi() {
        DataManager manager = DataManager.getInstance();
        manager.loadData();

        String myName = "myName";
        String opName = "opponentsName";
        User myUser = new User(myName, "myPassword", "myNickname");
        User opponentsUser = new User(opName, "opponentsPass", "opponentsNick");

        manager.addUser(myUser);
        manager.addUser(opponentsUser);
        MainMenuController controller = new MainMenuController(myUser);
        MainMenuView view = new MainMenuView(controller);

        controller.startDuelWithAi(1);
        assertOutputIsEqual(myName + " has no active deck");

        Deck myDeck = new Deck("my active deck");
        myDeck.setId("1");
        manager.addDeck(myDeck);
        myUser.setActiveDeck(myDeck);

        controller.startDuelWithAi(1);
        assertOutputIsEqual(myName + "'s deck is invalid");

        for (int i = 0; i < 41; i++) {
            Card card = new Monster((MonsterTemplate) manager.getCardTemplateByName("Wattkid"));
            myDeck.addCardToMainDeck(card);
            manager.addCard(card);
        }

        controller.startDuelWithAi(2);
        assertOutputIsEqual("number of rounds is not supported");

        boolean tails = true;
        boolean heads = true;

        while (tails || heads) {
            InputStream stdIn = TestUtility.giveInput("menu exit\nuser logout\nmenu exit");
            Utility.initializeScanner();

            controller.startDuelWithAi(1);
            String output = outContent.toString().trim();
            outContent.reset();

            if (output.contains("tails")) {
                tails = false;
                Assertions.assertTrue(output.startsWith("coin side was tails and AI starts duel\r\n" +
                        "its AI's turn\r\n" +
                        "phase: draw phase"));
                Assertions.assertTrue(output.endsWith("phase: end phase\r\n" +
                        "its myNickname's turn\r\n" +
                        "phase: draw phase\r\n" +
                        "you drew \"Wattkid\" from your deck"));
            } else {
                heads = false;
                Assertions.assertEquals("coin side was heads and myNickname starts duel\r\n" +
                        "its myNickname's turn\r\n" +
                        "phase: draw phase\r\n" +
                        "you drew \"Wattkid\" from your deck", output);
            }

            view.run();

            assertOutputIsEqual("user logged out successfully!");
            System.setIn(stdIn);
        }
    }


    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }
}
