package duel;

import control.DataManager;
import control.controller.DuelMenuController;
import control.controller.MainMenuController;
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

    @BeforeAll
    public static void generateData() {
        DataManager manager = DataManager.getInstance();
        manager.loadData();

        User userOne = new User("myUser", "myPass", "myNick");
        User userTwo = new User("opsUser", "opsPass", "opNick");

        manager.addUser(userOne);
        manager.addUser(userTwo);

        ArrayList<CardTemplate> templates = manager.getCardTemplates();
        ArrayList<Card> cards = manager.getCards();
        cards.clear();

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

        for (int i = 0; i < 59; i++) {
            deckOne.addCardToMainDeck(cards.get(i));
        }
        for (int i = 59; i < 118; i++) {
            deckTwo.addCardToMainDeck(cards.get(i));
        }

        userOne.addDeck(deckOne);
        userTwo.addDeck(deckTwo);

        userOne.setActiveDeck(deckOne);
        userTwo.setActiveDeck(deckTwo);
    }

    @BeforeEach
    public void resetUpStreams() {
        outContent.reset();
    }





    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }
}
