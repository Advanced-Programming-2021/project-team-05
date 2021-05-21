package deck;

import control.DataManager;
import control.controller.DeckMenuController;
import control.message.DeckMenuMessage;
import model.Deck;
import model.User;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import org.junit.jupiter.api.*;
import utils.Utility;
import view.DeckMenuView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class DeckTest {
    private static final PrintStream originalOut = System.out;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static InputStream stdIn;

    private void assertOutputIsEqual(String expectedOutput) {
        Assertions.assertEquals(expectedOutput, outContent.toString().trim());
        outContent.reset();
    }

    private void enterInput(String input) {
        stdIn = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Utility.initializeScanner();
    }

    private void compareOutput(String output) {
        assertOutputIsEqual(output.trim());
        System.setIn(stdIn);
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
    public void creatDeckTest() {
        User user = new User("creatDeck", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);

        String deckName = "creatDeckTestDeck";

        controller.createDeck(deckName);
        assertOutputIsEqual("deck created successfully!");

        Deck testDeck = user.getDeckByName(deckName);
        Assertions.assertNotNull(testDeck);

        controller.createDeck(deckName);
        assertOutputIsEqual("deck with name " + deckName + " already exists");

        testDeck = user.getDeckByName("testName");
        Assertions.assertNull(testDeck);

        view.printCreateDeckMessage(DeckMenuMessage.ERROR, "");
        assertOutputIsEqual("unexpected error");
    }


    @Test
    public void deleteDeckTest() {
        User user = new User("deleteDeck", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);
        DataManager manager = DataManager.getInstance();

        String deckName = "deleteDeckTestDeck";

        controller.deleteDeck("testName");
        assertOutputIsEqual("deck with name testName does not exist");

        Deck deck = new Deck(deckName);
        user.addDeck(deck);
        manager.addDeck(deck);
        controller.deleteDeck(deckName);
        assertOutputIsEqual("deck deleted successfully!");

        view.printDeleteDeckMessage(DeckMenuMessage.ERROR, "");
        assertOutputIsEqual("unexpected error");

        Assertions.assertNull(user.getDeckByName(deckName));
    }


    @Test
    public void activateDeckTest() {
        User user = new User("activateDeck", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);
        DataManager manager = DataManager.getInstance();

        String deckName = "activateDeckTestDeck";
        Deck deck = new Deck(deckName);
        user.addDeck(deck);
        manager.addDeck(deck);

        controller.activateDeck("testName");
        assertOutputIsEqual("deck with name testName does not exist");

        controller.activateDeck(deckName);
        assertOutputIsEqual("deck activated successfully!");

        view.printActivateDeckMessage(DeckMenuMessage.ERROR, "");
        assertOutputIsEqual("unexpected error");
    }


    @Test
    public void addCardTest() {
        User user = new User("addCard", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);
        DataManager manager = DataManager.getInstance();

        String deckName = "removeCardTestDeck";
        String cardName = "Suijin";

        manager.loadData();
        MonsterTemplate template = (MonsterTemplate) manager.getCardTemplateByName(cardName);

        Deck testDeck = new Deck(deckName);
        Card testCard = new Monster(template);
        user.addDeck(testDeck);
        manager.addDeck(testDeck);
        manager.addCard(testCard);

        controller.addCard("testName", "testName", true);
        assertOutputIsEqual("deck with name testName does not exist");

        controller.addCard(deckName, "testName", true);
        assertOutputIsEqual("card with name testName does not exist");

        ArrayList<CardTemplate> templates = manager.getCardTemplates();
        Assertions.assertEquals(76, templates.size());

        for (int i = 0; i < templates.size(); i++) {
            CardTemplate template1 = templates.get(i);
            if (template1 instanceof MonsterTemplate) {
                manager.addCard(new Monster((MonsterTemplate) template1));
            } else if (template1 instanceof SpellTemplate) {
                manager.addCard(new Spell((SpellTemplate) template1));
            } else {
                manager.addCard(new Trap((TrapTemplate) template1));
            }
        }

        ArrayList<Card> cards = manager.getCards();
        Assertions.assertEquals(77, cards.size());

//        testDeck.addCardToMainDeck(testCard);
//        testDeck.addCardToMainDeck(testCard);
//        testDeck.addCardToMainDeck(testCard);
//
//        testDeck.addCardToSideDeck(testCard);
//        testDeck.addCardToSideDeck(testCard);
//        testDeck.addCardToSideDeck(testCard);
//
//        controller.addCard(deckName, cardName, true);
//        assertOutputIsEqual("there are already three cards with name " + cardName + " in deck " + deckName);
//
//        controller.addCard(deckName, cardName, false);
//        assertOutputIsEqual("there are already three cards with name " + cardName + " in deck " + deckName);

//        for (int i = 0; i < 60; i++) {
//            testDeck.addCardToSideDeck(cards.get(i));
//            testDeck.addCardToSideDeck(cards.get(i));
//        }
//
//        controller.addCard(deckName, cardName, true);
//        assertOutputIsEqual("side deck is full");
//
//        controller.addCard(deckName, cardName, false);
//        assertOutputIsEqual("main deck is full");
    }


    @Test
    public void removeCardTest() {
        User user = new User("removeCard", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);
        DataManager manager = DataManager.getInstance();

        String deckName = "removeCardTestDeck";
        String cardName = "Suijin";

        manager.loadData();
        MonsterTemplate template = (MonsterTemplate) manager.getCardTemplateByName(cardName);

        Deck testDeck = new Deck(deckName);
        Card testCard = new Monster(template);
        user.addDeck(testDeck);
        manager.addDeck(testDeck);
        manager.addCard(testCard);

        controller.removeCard("testName", "testName", true);
        assertOutputIsEqual("deck with name testName does not exist");

        controller.removeCard(deckName, cardName, true);
        assertOutputIsEqual("card with name " + cardName + " does not exist in side deck");

        testDeck.addCardToSideDeck(testCard);
        controller.removeCard(deckName, cardName, true);
        assertOutputIsEqual("card removed form deck successfully!");

        controller.removeCard(deckName, cardName, false);
        assertOutputIsEqual("card with name " + cardName + " does not exist in main deck");

        testDeck.addCardToMainDeck(testCard);
        controller.removeCard(deckName, cardName, false);
        assertOutputIsEqual("card removed form deck successfully!");

        view.printRemoveCardMessage(DeckMenuMessage.ERROR, "", "");
        assertOutputIsEqual("unexpected error");
    }


    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }

}
