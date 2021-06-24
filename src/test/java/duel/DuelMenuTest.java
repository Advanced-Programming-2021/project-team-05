package duel;

import control.DataManager;
import control.controller.DuelMenuController;
import control.controller.ImportExportController;
import control.controller.MainMenuController;
import model.Deck;
import model.User;
import model.board.CardAddress;
import model.board.CardAddressZone;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import org.junit.jupiter.api.*;
import utils.TestUtility;
import utils.Utility;
import view.DuelMenuView;
import view.MainMenuView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class DuelMenuTest {
    private static final PrintStream originalOut = System.out;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @BeforeAll
    public static void generateData() {
        DataManager manager = DataManager.getInstance();
        manager.loadData();

        manager.getUsers().clear();

        User userOne = new User("myUser", "myPass", "myNick");
        User userTwo = new User("opsUser", "opsPass", "opsNick");

        manager.addUser(userOne);
        manager.addUser(userTwo);

        ArrayList<CardTemplate> templates = manager.getCardTemplates();
        ArrayList<Card> cards = manager.getCards();

        for (CardTemplate cardTemplate : templates) {
            if (cardTemplate instanceof MonsterTemplate) {
                manager.addCard(new Monster((MonsterTemplate) cardTemplate));
            } else if (cardTemplate instanceof SpellTemplate) {
                manager.addCard(new Spell((SpellTemplate) cardTemplate));
            } else {
                manager.addCard(new Trap((TrapTemplate) cardTemplate));
            }
        }

        for (CardTemplate cardTemplate : templates) {
            if (cardTemplate instanceof MonsterTemplate) {
                manager.addCard(new Monster((MonsterTemplate) cardTemplate));
            } else if (cardTemplate instanceof SpellTemplate) {
                manager.addCard(new Spell((SpellTemplate) cardTemplate));
            } else {
                manager.addCard(new Trap((TrapTemplate) cardTemplate));
            }
        }

        Deck deckOne = new Deck("deckOne");
        Deck deckTwo = new Deck("deckTwo");

        manager.addDeck(deckOne);
        manager.addDeck(deckTwo);

        for (int i = 0; i < 47; i++) {
            deckOne.addCardToMainDeck(cards.get(i));
        }
        for (int i = 47; i < 94; i++) {
            deckTwo.addCardToMainDeck(cards.get(i));
        }

        userOne.addDeck(deckOne);
        userTwo.addDeck(deckTwo);

        userOne.setActiveDeck(deckOne);
        userTwo.setActiveDeck(deckTwo);
    }

    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }

    @AfterAll
    public static void killScanner() {
        Utility.killScanner();
    }

    private void assertOutputIsEqual(String expectedOutput) {
        Assertions.assertEquals(expectedOutput, outContent.toString().trim());
        outContent.reset();
    }

    private void enterInput(String input) {
        InputStream stdIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Utility.initializeScanner();
    }

    @BeforeEach
    public void resetUpStreams() {
        outContent.reset();
    }

    @Test
    public void runTest() {
        DataManager manager = DataManager.getInstance();
        User myUser = manager.getUserByUsername("myUser");
        Assertions.assertNotNull(myUser);

        MainMenuController controller = new MainMenuController(myUser);
        MainMenuView view = new MainMenuView(controller);

        String input = "duel --new --second-player opsUser --rounds 3\n" +
                "menu exit\n" +
                "user logout\n" +
                "menu exit";

        enterInput(input);
        view.run();

        String output = outContent.toString();
        outContent.reset();

        Assertions.assertTrue(
                output.startsWith("coin side was tails and opsNick starts duel\r\n" +
                        "its opsNick's turn\r\n" +
                        "phase: draw phase\r\n" +
                        "you drew \"") ||
                        output.startsWith("coin side was heads and myNick starts duel\r\n" +
                                "its myNick's turn\r\n" +
                                "phase: draw phase\r\n" +
                                "you drew \""));
        Assertions.assertTrue(output.endsWith("user logged out successfully!\r\n"));
    }

    @Test
    public void commandsTest() {
        DataManager dataManager = DataManager.getInstance();
        User myUser = dataManager.getUserByUsername("myUser");
        User opsUser = dataManager.getUserByUsername("opsUser");
        Assertions.assertNotNull(myUser);

        boolean heads = true, tails = true;

        while (heads || tails) {
            DuelMenuController controller = new DuelMenuController(myUser, opsUser, 1);
            DuelMenuView view = new DuelMenuView(controller);
            outContent.reset();
            controller.startNextRound();
            if (outContent.toString().contains("heads")) heads = false;
            else tails = false;
            outContent.reset();
            ArrayList<String> commands = new ArrayList<>();
            ArrayList<String> outputs = new ArrayList<>();

            commands.add("cancel");
            outputs.add("there is nothing to cancel");

            commands.add("next phase");
            outputs.add("phase: standby phase");

            commands.add("select -d");
            outputs.add("no card is selected yet");

            commands.add("select -m 1");
            outputs.add("invalid selection");

            commands.add("select --graveyard 1");
            outputs.add("invalid selection");

            commands.add("summon");
            outputs.add("no card is selected yet");

            commands.add("set");
            outputs.add("no card is selected yet");

            commands.add("set -p attack");
            outputs.add("no card is selected yet");

            commands.add("flip-summon");
            outputs.add("no card is selected yet");

            commands.add("attack direct");
            outputs.add("no card is selected yet");

            commands.add("attack 1");
            outputs.add("no card is selected yet");

            commands.add("activate effect");
            outputs.add("no card is selected yet");

            commands.add("show graveyard");
            outputs.add("graveyard empty");
            outputs.add("enter \"back\" to return to game");
            commands.add("back");

            commands.add("show graveyard -o");
            outputs.add("graveyard empty");
            outputs.add("enter \"back\" to return to game");
            commands.add("back");

            commands.add("card show --selected");
            outputs.add("no card is selected yet");

            commands.add("increase -l he2");
            outputs.add("invalid command");

            commands.add("set-winner hello");
            outputs.add("invalid command");

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

            Assertions.assertEquals(outputsStringBuilder.toString(), outContent.toString());
            outContent.reset();

            System.setIn(stdIn);
        }
    }

    @Test
    public void surrenderTest() {
        DataManager manager = DataManager.getInstance();
        User myUser = manager.getUserByUsername("myUser");
        User opsUser = manager.getUserByUsername("opsUser");
        Assertions.assertNotNull(myUser);
        Assertions.assertNotNull(opsUser);

        boolean tails = true, heads = true;

        while (tails || heads) {
            MainMenuController controller = new MainMenuController(myUser);
            MainMenuView view = new MainMenuView(controller);

            String input = "duel --new --second-player opsUser --rounds 3\n" +
                    "surrender\n" +
                    "surrender\n" +
                    "user logout\n" +
                    "menu exit";

            long myUserMoneyBeforeDuel = myUser.getMoney();
            long myUserScoreBeforeDuel = myUser.getScore();
            long opsUserMoneyBeforeDuel = opsUser.getMoney();
            long opsUserScoreBeforeDuel = opsUser.getScore();

            enterInput(input);
            view.run();

            String output = outContent.toString();
            outContent.reset();

            if (output.startsWith("coin side was tails and opsNick")) {
                tails = false;
                Assertions.assertEquals(myUserMoneyBeforeDuel + 11000, myUser.getMoney());
                Assertions.assertEquals(myUserScoreBeforeDuel + 2, myUser.getScore());
                Assertions.assertEquals(opsUserMoneyBeforeDuel + 300, opsUser.getMoney());
                Assertions.assertEquals(opsUserScoreBeforeDuel, opsUser.getScore());
            } else {
                heads = false;
                Assertions.assertEquals(opsUserMoneyBeforeDuel + 11000, opsUser.getMoney());
                Assertions.assertEquals(opsUserScoreBeforeDuel + 2, opsUser.getScore());
                Assertions.assertEquals(myUserMoneyBeforeDuel + 300, myUser.getMoney());
                Assertions.assertEquals(myUserScoreBeforeDuel, myUser.getScore());
            }
        }
    }

    @Test
    public void surrenderAgainstAITest() {
        DataManager manager = DataManager.getInstance();
        User myUser = manager.getUserByUsername("myUser");
        Assertions.assertNotNull(myUser);

        boolean heads = true, tails = true;

        while (heads || tails) {
            MainMenuController controller = new MainMenuController(myUser);
            MainMenuView view = new MainMenuView(controller);

            String input = "duel --new --ai --rounds 3\n" +
                    "surrender\n" +
                    "surrender\n" +
                    "user logout\n" +
                    "menu exit";

            long myUserMoneyBeforeDuel = myUser.getMoney();
            long myUserScoreBeforeDuel = myUser.getScore();

            enterInput(input);
            view.run();

            if (outContent.toString().contains("tails")) tails = false;
            else heads  = false;

            outContent.reset();

            Assertions.assertEquals(myUserMoneyBeforeDuel + 300, myUser.getMoney());
            Assertions.assertEquals(myUserScoreBeforeDuel, myUser.getScore());
        }
    }

    @Test
    public void setWinnerTest() {
        DataManager manager = DataManager.getInstance();

        User userOne = manager.getUserByUsername("myUser");
        User userTwo = manager.getUserByUsername("opsUser");

        Assertions.assertNotNull(userOne);
        Assertions.assertNotNull(userTwo);

        boolean heads = true, tails = true;

        while (heads || tails) {
            DuelMenuController controller = new DuelMenuController(userOne, userTwo, 1);
            DuelMenuView view = new DuelMenuView(controller);
            controller.startNextRound();
            if (outContent.toString().contains("heads")) heads = false;
            if (outContent.toString().contains("tails")) tails = false;
            outContent.reset();

            controller.setWinner("myNick");
            assertOutputIsEqual("winner set successfully!\r\n" +
                    "myUser won the whole match with score: 1-0");

            controller.setWinner("opNick");
            assertOutputIsEqual("invalid nickname");

            controller = new DuelMenuController(userOne, userTwo, 1);
            view = new DuelMenuView(controller);
            controller.startNextRound();
            outContent.reset();

            controller.setWinner("opsNick");
            assertOutputIsEqual("winner set successfully!\r\n" +
                    "opsUser won the whole match with score: 0-1");
        }
    }

    @Test
    public void getSelectedCardString() {
        DataManager manager = DataManager.getInstance();

        User userOne = manager.getUserByUsername("myUser");
        User userTwo = manager.getUserByUsername("opsUser");

        DuelMenuController controller;
        DuelMenuView view;

        boolean heads = false;

        while (true) {
            controller = new DuelMenuController(userOne, userTwo, 1);
            view = new DuelMenuView(controller);
            controller.startNextRound();

            ArrayList<Card> handCards;
            if (outContent.toString().contains("heads")) {
                heads = true;
                handCards = controller.getBoard().getPlayerTable().getHand();
                CardAddress address = new CardAddress(CardAddressZone.HAND, 1, false);
                controller.selectCard(address);
                Assertions.assertEquals(controller.getSelectedCardString(),
                        handCards.get(0).detailedToString());
            } else {
                controller.selectCard(new CardAddress(CardAddressZone.HAND, 1, true));
                Assertions.assertEquals(controller.getSelectedCardString(), "no card is selected yet");
                if (heads) break;
            }
            outContent.reset();
        }
        outContent.reset();
        controller.quickChangeTurn(false);
        assertOutputIsEqual("now it will be myNick's turn");
    }
}
