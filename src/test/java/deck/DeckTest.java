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

        Assertions.assertEquals(controller.getUser().getUsername(), "creatDeck");

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

        Deck testDeck = new Deck(deckName);
        user.addDeck(testDeck);
        manager.addDeck(testDeck);

        controller.addCard("testName", "testName", true);
        assertOutputIsEqual("deck with name testName does not exist");

        controller.addCard(deckName, "testName", true);
        assertOutputIsEqual("card with name testName does not exist");

        MonsterTemplate template = (MonsterTemplate) manager.getCardTemplateByName(cardName);
        Card testCard1 = new Monster(template);
        Card testCard2 = new Monster(template);
        Card testCard3 = new Monster(template);
        Card testCard4 = new Monster(template);

        manager.addCard(testCard1);
        manager.addCard(testCard2);
        manager.addCard(testCard3);
        manager.addCard(testCard4);

        user.purchaseCard(testCard1);
        user.purchaseCard(testCard2);
        user.purchaseCard(testCard3);
        user.purchaseCard(testCard4);

        testDeck.addCardToMainDeck(testCard1);
        testDeck.addCardToMainDeck(testCard2);
        testDeck.addCardToSideDeck(testCard3);

        controller.addCard(deckName, cardName, false);
        assertOutputIsEqual("there are already three cards with name " + cardName + " in deck " + deckName);

        ArrayList<CardTemplate> templates = manager.getCardTemplates();
        Assertions.assertEquals(76, templates.size());

        for (CardTemplate cardTemplate : templates) {
            if (cardTemplate instanceof MonsterTemplate) {
                manager.addCard(new Monster((MonsterTemplate) cardTemplate));
            } else if (cardTemplate instanceof SpellTemplate) {
                manager.addCard(new Spell((SpellTemplate) cardTemplate));
            } else {
                manager.addCard(new Trap((TrapTemplate) cardTemplate));
            }
        }

        ArrayList<Card> cards = manager.getCards();
        Assertions.assertEquals(80, cards.size());

        testDeck.getMainDeckCardIds().clear();

        Card cardToAddToMainDeck = cards.get(19);
        user.purchaseCard(cardToAddToMainDeck);
        controller.addCard(deckName, cardToAddToMainDeck.getName(), false);
        assertOutputIsEqual("card added to deck successfully!");

        for (int i = 20; i < 80; i++) {
            Card card = cards.get(i);
            user.purchaseCard(card);
            testDeck.addCardToMainDeck(cards.get(i));
        }

        controller.addCard(deckName, cardName, false);
        assertOutputIsEqual("main deck is full");

        testDeck.getSideDeckCardIds().clear();

        Card cardToAddToSideDeck = cards.get(18);
        user.purchaseCard(cardToAddToSideDeck);
        controller.addCard(deckName, cardToAddToSideDeck.getName(), true);
        assertOutputIsEqual("card added to deck successfully!");

        for (int i = 20; i < 65; i++) {
            Card card = cards.get(i);
            user.purchaseCard(card);
            testDeck.addCardToSideDeck(cards.get(i));
        }

        controller.addCard(deckName, cardName, true);
        assertOutputIsEqual("side deck is full");

        view.printAddCardMessage(DeckMenuMessage.ERROR, "", "");
        assertOutputIsEqual("unexpected error");
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


    @Test
    public void showHelpTest() {
        User user = new User("showHelp", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);

        view.showHelp();
        assertOutputIsEqual("commands:\r\n" +
                "\tdeck create <deck name>\r\n" +
                "\tdeck delete <deck name>\r\n" +
                "\tdeck set-activate <deck name>\r\n" +
                "\tdeck add-card --card <card name> --deck <deck name> --side(optional)\r\n" +
                "\tdeck rm-card --card <card name> --deck <deck name> --side(optional)\r\n" +
                "\tdeck show --all\r\n" +
                "\tdeck show --deck-name <deck name> --side(optional)\r\n" +
                "\tdeck show --cards\r\n" +
                "\tcard show <card name>\r\n" +
                "\tmenu show-current\r\n" +
                "\tmenu exit\r\n" +
                "\tmenu help");
    }


    @Test
    public void showCardTest() {
        User user = new User("showCard", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);
        DataManager.getInstance().loadData();

        view.showCard("show card Curtain_of_the_dark_ones".split("\\s"));
        assertOutputIsEqual("Name: Curtain of the dark ones\n" +
                "Level: 2\n" +
                "Type: Normal\n" +
                "Attack: 600\n" +
                "Defense: 500\n" +
                "Description: A curtain that a spellcaster made, it is said to raise a dark power.");

        view.showCard("show card Curtain of the dark ones".split("\\s"));
        assertOutputIsEqual("invalid card name");

        view.showCard("show card card_name".split("\\s"));
        assertOutputIsEqual("invalid card name");

        view.showCard("show card".split("\\s"));
        assertOutputIsEqual("invalid command");
    }


    @Test
    public void activateDeckViewTest() {
        User user = new User("activateView", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);
        DataManager manager = DataManager.getInstance();

        String deckName = "activateDeckViewTest";
        Deck testDeck = new Deck(deckName);
        user.addDeck(testDeck);
        manager.addDeck(testDeck);

        view.activateDeck("deck set-activate testName".split("\\s"));
        assertOutputIsEqual("deck with name testName does not exist");

        view.activateDeck(("deck set-activate " + deckName).split("\\s"));
        assertOutputIsEqual("deck activated successfully!");
    }


    @Test
    public void createDeckViewTest() {
        User user = new User("createView", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);
        DataManager manager = DataManager.getInstance();

        view.createDeck("deck create createView".split("\\s"));
        assertOutputIsEqual("deck created successfully!");

        view.createDeck("deck create createView".split("\\s"));
        assertOutputIsEqual("deck with name createView already exists");
    }


    @Test
    public void deleteDeckViewTest() {
        User user = new User("deleteDeckView", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);
        DataManager manager = DataManager.getInstance();

        view.deleteDeck("deck delete mmd".split("\\s"));
        assertOutputIsEqual("deck with name mmd does not exist");
    }


    @Test
    public void addOrRemoveCardViewTest() {
        User user = new User("addOrRemoveCardView", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);
        DataManager manager = DataManager.getInstance();

        view.addOrRemoveCard("deck add-card -d mmd --side golabi --card".split("\\s"), true);
        assertOutputIsEqual("invalid command");

        view.addOrRemoveCard("deck add-card mmd --side --card Suijin".split("\\s"), true);
        assertOutputIsEqual("invalid command");

        view.addOrRemoveCard("deck add-card --card mmd --deck golabi --side test".split("\\s"), true);
        assertOutputIsEqual("invalid command");

        view.addOrRemoveCard("deck add-card --card mmd --deck golabi --side".split("\\s"), true);
        assertOutputIsEqual("deck with name golabi does not exist");

        view.addOrRemoveCard("deck add-card --card mmd --deck golabi --side".split("\\s"), false);
        assertOutputIsEqual("deck with name golabi does not exist");
    }


    @Test
    public void showAllDecksTest() {
        User user = new User("showAllDecksView", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);
        DataManager manager = DataManager.getInstance();

        view.showAllDecks();
        assertOutputIsEqual("Decks:\r\n" +
                "Active Deck:\r\n" +
                "Other Decks:");

        manager.loadData();

        Deck deck1 = new Deck("deck1");
        Deck deck2 = new Deck("deck2");
        Deck deck3 = new Deck("deck3");
        manager.addDeck(deck1);
        manager.addDeck(deck2);
        manager.addDeck(deck3);
        user.addDeck(deck1);
        user.addDeck(deck2);
        user.addDeck(deck3);

        ArrayList<CardTemplate> templates = manager.getCardTemplates();

        for (CardTemplate cardTemplate : templates) {
            if (cardTemplate instanceof MonsterTemplate) {
                manager.addCard(new Monster((MonsterTemplate) cardTemplate));
            } else if (cardTemplate instanceof SpellTemplate) {
                manager.addCard(new Spell((SpellTemplate) cardTemplate));
            } else {
                manager.addCard(new Trap((TrapTemplate) cardTemplate));
            }
        }

        ArrayList<Card> cards = manager.getCards();

        user.setActiveDeck(deck1);
        deck2.addCardToSideDeck(cards.get(0));
        deck2.addCardToSideDeck(cards.get(1));
        for (int i = 0; i < 40; i++) {
            deck3.addCardToMainDeck(cards.get(i));
        }

        view.showAllDecks();
        assertOutputIsEqual("Decks:\r\n" +
                "Active Deck:\r\n" +
                "deck1: main deck 0, side deck 0, invalid\r\n" +
                "Other Decks:\r\n" +
                "deck2: main deck 0, side deck 2, invalid\r\n" +
                "deck3: main deck 40, side deck 0, valid");
    }


    @Test
    public void showDeckViewTest() {
        User user = new User("showDeckView", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);
        DataManager manager = DataManager.getInstance();

        view.showDeck("deck show mmd --deck-name".split("\\s"));
        assertOutputIsEqual("invalid command");

        view.showDeck("deck show mmd --side".split("\\s"));
        assertOutputIsEqual("invalid command");

        view.showDeck("deck show --deck-name <deck name> --side".split("\\s"));
        assertOutputIsEqual("invalid command");

        view.showDeck("deck show --deck-name mmd --side".split("\\s"));
        assertOutputIsEqual("no deck found");

        Deck deck = new Deck("testDeck");
        manager.addDeck(deck);
        user.addDeck(deck);
        view.showDeck("deck show --deck-name testDeck".split("\\s"));
        assertOutputIsEqual("Deck: testDeck\r\n" +
                "Main deck:\r\n" +
                "Monsters:\r\n" +
                "Spell and Traps:");
    }


    @Test
    public void showAllCardsTest() {
        User user = new User("showAllCardsView", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);
        DataManager manager = DataManager.getInstance();

        manager.loadData();
        ArrayList<CardTemplate> templates = manager.getCardTemplates();

        for (CardTemplate cardTemplate : templates) {
            if (cardTemplate instanceof MonsterTemplate) {
                manager.addCard(new Monster((MonsterTemplate) cardTemplate));
            } else if (cardTemplate instanceof SpellTemplate) {
                manager.addCard(new Spell((SpellTemplate) cardTemplate));
            } else {
                manager.addCard(new Trap((TrapTemplate) cardTemplate));
            }
        }

        ArrayList<Card> cards = manager.getCards();
        user.purchaseCard(cards.get(10));
        user.purchaseCard(cards.get(20));
        user.purchaseCard(cards.get(30));
        user.purchaseCard(cards.get(40));
        user.purchaseCard(cards.get(50));

        view.showAllCards();
        assertOutputIsEqual("Alexandrite Dragon: Many of the czars' lost jewels can be found in the scales of this priceless dragon. Its creator remains a mystery, along with how they acquired the imperial treasures. But whosoever finds this dragon has hit the jackpot... whether they know it or not.\r\n" +
                "Call of The Haunted: Activate this card by targeting 1 monster in your GY; Special Summon that target in Attack Position. When this card leaves the field, destroy that monster. When that monster is destroyed, destroy this card.\r\n" +
                "Command Knight: All Warrior-Type monsters you control gain 400 ATK. If you control another monster, monsters your opponent controls cannot target this card for an attack.\r\n" +
                "Haniwa: An earthen figure that protects the tomb of an ancient ruler.\r\n" +
                "Wattkid: A creature that electrocutes opponents with bolts of lightning.");
    }


    @Test
    public void runTest() {
        User user = new User("runTest", "pass", "nick");
        DeckMenuController controller = new DeckMenuController(user);
        DeckMenuView view = new DeckMenuView(controller);

        String input = "deck create zareaBidecki\n" +
                "deck delete zareaBidecki\n" +
                "deck create zareaBidecki\n" +
                "deck set-activate zareaBidecki\n" +
                "deck add-card --card mmd --deck zareaBideck --side\n" +
                "deck rm-card --card mmd --deck zareaBidecki --side\n" +
                "deck show --all\n" +
                "deck show --zareaBidecki --side\n" +
                "deck show --cards\n" +
                "deck show golabi\n" +
                "card show golabi\n" +
                "menu enter golabi\n" +
                "menu help\n" +
                "deck show AAA\n" +
                "menu exit\n";
        String output = "separate card name words with '_'. example: Battle_OX\r\n" +
                "deck created successfully!\r\n" +
                "deck deleted successfully!\r\n" +
                "deck created successfully!\r\n" +
                "deck activated successfully!\r\n" +
                "deck with name zareaBideck does not exist\r\n" +
                "card with name mmd does not exist in side deck\r\n" +
                "Decks:\r\n" +
                "Active Deck:\r\n" +
                "zareaBidecki: main deck 0, side deck 0, invalid\r\n" +
                "Other Decks:\r\n" +
                "invalid command\r\n" +
                "invalid command\r\n" +
                "invalid card name\r\n" +
                "menu navigation is not possible\r\n" +
                "commands:\r\n" +
                "\tdeck create <deck name>\r\n" +
                "\tdeck delete <deck name>\r\n" +
                "\tdeck set-activate <deck name>\r\n" +
                "\tdeck add-card --card <card name> --deck <deck name> --side(optional)\r\n" +
                "\tdeck rm-card --card <card name> --deck <deck name> --side(optional)\r\n" +
                "\tdeck show --all\r\n" +
                "\tdeck show --deck-name <deck name> --side(optional)\r\n" +
                "\tdeck show --cards\r\n" +
                "\tcard show <card name>\r\n" +
                "\tmenu show-current\r\n" +
                "\tmenu exit\r\n" +
                "\tmenu help\r\n" +
                "invalid command";

        enterInput(input);
        view.run();
        compareOutput(output);
    }


    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }

}
