package shop;

import control.*;
import control.controller.ShopMenuController;
import control.message.ShopMenuMessage;
import model.User;
import model.card.Card;
import model.template.CardTemplate;
import org.junit.jupiter.api.*;
import utils.TestUtility;
import utils.Utility;
import view.ShopMenuView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;

public class ShopTest {
    private static final PrintStream originalOut = System.out;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();


    private void assertOutputIsEqual(String expectedOutput) {
        Assertions.assertEquals(expectedOutput, outContent.toString().trim());
        outContent.reset();
    }

    private String getAllCardsString() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadData();

        ArrayList<CardTemplate> allTemplates = dataManager.getCardTemplates();
        allTemplates.sort(Comparator.comparing(CardTemplate::getName));

        StringBuilder exceptedOutput = new StringBuilder();
        for (CardTemplate template : allTemplates) {
            exceptedOutput.append(template.getName()).append(": ").append(template.getPrice()).append("\r\n");
        }
        exceptedOutput.delete(exceptedOutput.length() - 2, exceptedOutput.length());

        return exceptedOutput.toString();
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
    public void getUserTest() {
        User user = new User("username", "password", "nickname");
        ShopMenuController shop = new ShopMenuController(user);
        User shopCurrentUser = shop.getUser();
        Assertions.assertEquals(shopCurrentUser.getUsername(), "username");
        Assertions.assertEquals(shopCurrentUser.getPassword(), "password");
        Assertions.assertEquals(shopCurrentUser.getNickname(), "nickname");
    }


    @Test
    public void buyCardControllerTest() {
        DataManager manager = DataManager.getInstance();
        manager.loadData();

        User testUser = new User("testUser", "testPass", "testNick");
        testUser.setMoney(1000);
        ShopMenuController shop = new ShopMenuController(testUser);
        ShopMenuView view = new ShopMenuView(shop);

        shop.buyCard("Battle warrior");
        shop.buyCard("mohammadSadeghi");
        shop.buyCard("Curtain of the dark ones");

        testUser.increaseMoney(5000);
        shop.buyCard("Trap Hole");
        shop.buyCard("Advanced Ritual Art");

        ArrayList<Card> cards = testUser.getPurchasedCards();
        Assertions.assertEquals(3, cards.size());
        Assertions.assertEquals(cards.get(0).getName(), "Curtain of the dark ones");
        Assertions.assertEquals(cards.get(1).getName(), "Trap Hole");
        Assertions.assertEquals(cards.get(2).getName(), "Advanced Ritual Art");
        Assertions.assertEquals(testUser.getMoney(), 300);
    }


    @Test
    public void increaseMoneyTest() {
        DataManager manager = DataManager.getInstance();
        manager.loadData();

        User testUser = new User("testUser", "testPass", "testNick");
        testUser.setMoney(1000);
        ShopMenuController controller = new ShopMenuController(testUser);
        ShopMenuView view = new ShopMenuView(controller);

        controller.increaseMoney(10000);
        Assertions.assertEquals(11000, testUser.getMoney());

        view.increaseMoney("test test 1000".split(" "));
        Assertions.assertEquals(12000, testUser.getMoney());

        view.increaseMoney("test test 10a0".split(" "));
        assertOutputIsEqual("money increased!\r\n" +
                "money increased!\r\n" +
                "invalid command");
    }


    @Test
    public void runTest() {
        User testUser = new User("name", "pass", "nick");
        testUser.setMoney(1300);
        ShopMenuView view = new ShopMenuView(new ShopMenuController(testUser));

        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> outputs = new ArrayList<>();

        outputs.add("separate card name words with '_'. example: Battle_OX");
        commands.add("shop buy sadeghi");
        outputs.add("there is no card with this name");

        commands.add("shop buy Fireyarou");
        outputs.add("not enough money");

        commands.add("shop buy Haniwa");
        outputs.add("card bought successfully!");

        commands.add("shop buy Curtain_of_the_dark_ones");
        outputs.add("card bought successfully!");

        commands.add("shop buy Curtain of the dark ones");
        outputs.add("invalid command");

        commands.add("card show Curtain of the dark ones");
        outputs.add("invalid command");

        commands.add("card show Fireyarou");
        outputs.add("Name: Fireyarou\n" +
                "Level: 4\n" +
                "Type: Normal\n" +
                "Attack: 1300\n" +
                "Defense: 1000\n" +
                "Description: A malevolent creature wrapped in flames that attacks enemies with intense fire.");

        commands.add("menu enter kdsmkm");
        outputs.add("menu navigation is not possible");

        commands.add("shop buy ");
        outputs.add("invalid command");

        commands.add("shop show --all");
        outputs.add(getAllCardsString());

        commands.add("menu help");
        outputs.add("commands:\r\n" +
                        "\tshop buy <card name>\r\n" +
                        "\tshop show --all\r\n" +
                        "\tcard show <card name>\r\n" +
                        "\tmenu show-current\r\n" +
                        "\tmenu exit\r\n" +
                        "\tmenu help");

        commands.add("increase --money 1000");
        outputs.add("money increased!");

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
    public void showCurrentMenuTest() {
        User testUser = new User("name", "pass", "nick");
        ShopMenuView view = new ShopMenuView(new ShopMenuController(testUser));

        view.showCurrentMenu();
        assertOutputIsEqual("Shop Menu");

    }


    @Test
    public void showAllCardsTest() {
        User testUser = new User("name", "pass", "nick");
        ShopMenuView view = new ShopMenuView(new ShopMenuController(testUser));

        String exceptedOutput = getAllCardsString();

        view.showAllCards();
        assertOutputIsEqual(exceptedOutput.trim());
    }


    @Test
    public void showCardTest() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadData();

        User testUser = new User("name", "pass", "nick");
        testUser.setMoney(2900);
        ShopMenuView view = new ShopMenuView(new ShopMenuController(testUser));

        view.showCard(new String[]{"card", "show", "parsaSeddighi"});
        assertOutputIsEqual("invalid card name");

        view.showCard(new String[]{"card", "show", "Battle OX"});
        String exceptedOutput = dataManager.getCardTemplateByName("Battle OX").detailedToString();
        assertOutputIsEqual(exceptedOutput);

        view.showCard(new String[]{"card", "show", "Raigeki"});
        exceptedOutput = dataManager.getCardTemplateByName("Raigeki").detailedToString();
        assertOutputIsEqual(exceptedOutput);

        view.showCard(new String[]{"card", "show", "Trap Hole"});
        exceptedOutput = dataManager.getCardTemplateByName("Trap Hole").detailedToString();
        assertOutputIsEqual(exceptedOutput);
    }


    @Test
    public void printBuyCardMessageTest() {
        User testUser = new User("name", "pass", "nick");
        ShopMenuView view = new ShopMenuView(new ShopMenuController(testUser));

        view.printBuyCardMessage(ShopMenuMessage.NO_CARD_EXISTS);
        assertOutputIsEqual("there is no card with this name");
        view.printBuyCardMessage(ShopMenuMessage.NOT_ENOUGH_MONEY);
        assertOutputIsEqual("not enough money");
        view.printBuyCardMessage(ShopMenuMessage.CARD_SUCCESSFULLY_PURCHASED);
        assertOutputIsEqual("card bought successfully!");
        view.printBuyCardMessage(ShopMenuMessage.ERROR);
        assertOutputIsEqual("unexpected error");
    }


    @Test
    public void byCardViewTest() {
        DataManager dataManager = DataManager.getInstance();
        dataManager.loadData();

        User testUser = new User("name", "pass", "nick");
        testUser.setMoney(1000);
        ShopMenuView view = new ShopMenuView(new ShopMenuController(testUser));

        view.buyCard(new String[]{"shop", "buy", "parsaSeddighi"});
        assertOutputIsEqual("there is no card with this name");

        view.buyCard(new String[]{"shop", "buy", "Battle_OX"});
        assertOutputIsEqual("not enough money");

        view.buyCard(new String[]{"shop", "buy", "Curtain_of_the_dark_ones"});
        assertOutputIsEqual("card bought successfully!");
    }


    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }
}
