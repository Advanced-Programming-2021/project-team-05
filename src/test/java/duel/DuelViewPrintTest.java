package duel;

import control.DataManager;
import control.controller.DuelMenuController;
import control.message.DuelMenuMessage;
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
import utils.CoinSide;
import utils.TestUtility;
import utils.Utility;
import view.DuelMenuView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class DuelViewPrintTest {

    private static final PrintStream originalOut = System.out;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();


    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @BeforeAll
    public static void loadData() {
        DataManager.getInstance().loadData();
    }

    @AfterAll
    public static void restoreStreams() {
        System.setOut(originalOut);
    }

    @BeforeEach
    public void resetUpStreams() {
        outContent.reset();
    }

    @Test
    public void printSelectMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printSelectMessage(DuelMenuMessage.INVALID_SELECTION);
        Assertions.assertEquals("invalid selection\r\n", outContent.toString());
        outContent.reset();

        view.printSelectMessage(DuelMenuMessage.NO_CARD_FOUND);
        Assertions.assertEquals("no card found in the given position\r\n", outContent.toString());
        outContent.reset();

        view.printSelectMessage(DuelMenuMessage.CARD_SELECTED);
        Assertions.assertEquals("card selected!\r\n", outContent.toString());
        outContent.reset();

        view.printSelectMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printDeselectMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printDeselectMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
        Assertions.assertEquals("no card is selected yet\r\n", outContent.toString());
        outContent.reset();

        view.printDeselectMessage(DuelMenuMessage.CARD_DESELECTED);
        Assertions.assertEquals("card deselected!\r\n", outContent.toString());
        outContent.reset();

        view.printDeselectMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printSummonMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printSummonMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
        Assertions.assertEquals("no card is selected yet\r\n", outContent.toString());
        outContent.reset();

        view.printSummonMessage(DuelMenuMessage.CANT_SUMMON);
        Assertions.assertEquals("you can’t summon this card\r\n", outContent.toString());
        outContent.reset();

        view.printSummonMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
        Assertions.assertEquals("you can’t do this action in this phase\r\n", outContent.toString());
        outContent.reset();

        view.printSummonMessage(DuelMenuMessage.MONSTER_ZONE_IS_FULL);
        Assertions.assertEquals("monster card zone is full\r\n", outContent.toString());
        outContent.reset();

        view.printSummonMessage(DuelMenuMessage.ALREADY_SUMMONED_SET);
        Assertions.assertEquals("you already summoned/set on this turn\r\n", outContent.toString());
        outContent.reset();

        view.printSummonMessage(DuelMenuMessage.NOT_ENOUGH_TRIBUTE);
        Assertions.assertEquals("there are not enough cards for tribute\r\n", outContent.toString());
        outContent.reset();

        view.printSummonMessage(DuelMenuMessage.CANT_PLAY_THIS_KIND_OF_MOVES);
        Assertions.assertEquals("it’s not your turn to play this kind of moves\r\n", outContent.toString());
        outContent.reset();

        view.printSummonMessage(DuelMenuMessage.SUMMON_SUCCESSFUL);
        Assertions.assertEquals("summoned successfully!\r\n", outContent.toString());
        outContent.reset();

        view.printSummonMessage(DuelMenuMessage.SPECIAL_SUMMON_RIGHT_NOW);
        Assertions.assertEquals("you should special summon right now\r\n", outContent.toString());
        outContent.reset();

        view.printSummonMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printTributeSummonMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printTributeSummonMessage(DuelMenuMessage.INVALID_POSITION);
        Assertions.assertEquals("invalid position\r\n", outContent.toString());
        outContent.reset();

        view.printTributeSummonMessage(DuelMenuMessage.NO_MONSTER_ON_ADDRESS);
        Assertions.assertEquals("there is no monsters in one of addresses\r\n", outContent.toString());
        outContent.reset();

        view.printTributeSummonMessage(DuelMenuMessage.SUMMON_SUCCESSFUL);
        Assertions.assertEquals("summoned successfully!\r\n", outContent.toString());
        outContent.reset();

        view.printSummonMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printRitualSummonMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printRitualSummonMessage(DuelMenuMessage.NO_WAY_TO_RITUAL_SUMMON);
        Assertions.assertEquals("there is no way you could ritual summon a monster\r\n", outContent.toString());
        outContent.reset();

        view.printRitualSummonMessage(DuelMenuMessage.RITUAL_SUMMON_RIGHT_NOW);
        Assertions.assertEquals("you should ritual summon right now\r\n", outContent.toString());
        outContent.reset();

        view.printRitualSummonMessage(DuelMenuMessage.DONT_MATCH_WITH_RITUAL_MONSTER);
        Assertions.assertEquals("selected monsters levels don’t match with ritual monster\r\n", outContent.toString());
        outContent.reset();

        view.printRitualSummonMessage(DuelMenuMessage.SUMMON_SUCCESSFUL);
        Assertions.assertEquals("summoned successfully!\r\n", outContent.toString());
        outContent.reset();

        view.printRitualSummonMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printSetMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printSetMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
        Assertions.assertEquals("no card is selected yet\r\n", outContent.toString());
        outContent.reset();

        view.printSetMessage(DuelMenuMessage.CANT_SET);
        Assertions.assertEquals("you can’t set this card\r\n", outContent.toString());
        outContent.reset();

        view.printSetMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
        Assertions.assertEquals("you can’t do this action in this phase\r\n", outContent.toString());
        outContent.reset();

        view.printSetMessage(DuelMenuMessage.MONSTER_ZONE_IS_FULL);
        Assertions.assertEquals("monster card zone is full\r\n", outContent.toString());
        outContent.reset();

        view.printSetMessage(DuelMenuMessage.SPELL_ZONE_FULL);
        Assertions.assertEquals("spell card zone is full\r\n", outContent.toString());
        outContent.reset();

        view.printSetMessage(DuelMenuMessage.ALREADY_SUMMONED_SET);
        Assertions.assertEquals("you already summoned/set on this turn\r\n", outContent.toString());
        outContent.reset();

        view.printSetMessage(DuelMenuMessage.CANT_PLAY_THIS_KIND_OF_MOVES);
        Assertions.assertEquals("it’s not your turn to play this kind of moves\r\n", outContent.toString());
        outContent.reset();

        view.printSetMessage(DuelMenuMessage.SET_SUCCESSFUL);
        Assertions.assertEquals("set successfully!\r\n", outContent.toString());
        outContent.reset();

        view.printSetMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printChangePositionMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printChangePositionMessage(DuelMenuMessage.INVALID_COMMAND);
        Assertions.assertEquals("invalid command\r\n", outContent.toString());
        outContent.reset();

        view.printChangePositionMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
        Assertions.assertEquals("no card is selected yet\r\n", outContent.toString());
        outContent.reset();

        view.printChangePositionMessage(DuelMenuMessage.CANT_CHANGE_POSITION);
        Assertions.assertEquals("you can’t change this card position\r\n", outContent.toString());
        outContent.reset();

        view.printChangePositionMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
        Assertions.assertEquals("you can’t do this action in this phase\r\n", outContent.toString());
        outContent.reset();

        view.printChangePositionMessage(DuelMenuMessage.ALREADY_IN_WANTED_POSITION);
        Assertions.assertEquals("this card is already in the wanted position\r\n", outContent.toString());
        outContent.reset();

        view.printChangePositionMessage(DuelMenuMessage.ALREADY_CHANGED_POSITION);
        Assertions.assertEquals("you already changed this card position in this turn\r\n", outContent.toString());
        outContent.reset();

        view.printChangePositionMessage(DuelMenuMessage.POSITION_CHANGED);
        Assertions.assertEquals("monster card position changed successfully!\r\n", outContent.toString());
        outContent.reset();

        view.printChangePositionMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printFlipSummonMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printFlipSummonMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
        Assertions.assertEquals("no card is selected yet\r\n", outContent.toString());
        outContent.reset();

        view.printFlipSummonMessage(DuelMenuMessage.CANT_CHANGE_POSITION);
        Assertions.assertEquals("you can’t change this card position\r\n", outContent.toString());
        outContent.reset();

        view.printFlipSummonMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
        Assertions.assertEquals("you can’t do this action in this phase\r\n", outContent.toString());
        outContent.reset();

        view.printFlipSummonMessage(DuelMenuMessage.CANT_FLIP_SUMMON);
        Assertions.assertEquals("you can’t flip summon this card\r\n", outContent.toString());
        outContent.reset();

        view.printFlipSummonMessage(DuelMenuMessage.FLIP_SUMMON_SUCCESSFUL);
        Assertions.assertEquals("flip summoned successfully!\r\n", outContent.toString());
        outContent.reset();

        view.printFlipSummonMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printDirectAttackMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printDirectAttackMessage(DuelMenuMessage.NO_CARD_IS_SELECTED, 0);
        Assertions.assertEquals("no card is selected yet\r\n", outContent.toString());
        outContent.reset();

        view.printDirectAttackMessage(DuelMenuMessage.CANT_ATTACK, 0);
        Assertions.assertEquals("you can’t attack with this card\r\n", outContent.toString());
        outContent.reset();

        view.printDirectAttackMessage(DuelMenuMessage.ACTION_NOT_ALLOWED, 0);
        Assertions.assertEquals("you can’t do this action in this phase\r\n", outContent.toString());
        outContent.reset();

        view.printDirectAttackMessage(DuelMenuMessage.ALREADY_ATTACKED, 0);
        Assertions.assertEquals("this card already attacked\r\n", outContent.toString());
        outContent.reset();

        view.printDirectAttackMessage(DuelMenuMessage.CANT_ATTACK_DIRECTLY, 0);
        Assertions.assertEquals("you can’t attack the opponent directly\r\n", outContent.toString());
        outContent.reset();

        view.printDirectAttackMessage(DuelMenuMessage.ATTACK_PREVENTED, 0);
        Assertions.assertEquals("attack prevented\r\n", outContent.toString());
        outContent.reset();

        view.printDirectAttackMessage(DuelMenuMessage.DIRECT_ATTACK_SUCCESSFUL, 7355);
        Assertions.assertEquals("you opponent receives 7355 battle damage\r\n", outContent.toString());
        outContent.reset();

        view.printDirectAttackMessage(DuelMenuMessage.UNEXPECTED_ERROR, 0);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printAttackMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printAttackMessage(DuelMenuMessage.INVALID_POSITION, 0, null);
        Assertions.assertEquals("invalid position\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.NO_CARD_IS_SELECTED, 0, null);
        Assertions.assertEquals("no card is selected yet\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.CANT_ATTACK, 0, null);
        Assertions.assertEquals("you can’t attack with this card\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.ACTION_NOT_ALLOWED, 0, null);
        Assertions.assertEquals("you can’t do this action in this phase\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.ALREADY_ATTACKED, 0, null);
        Assertions.assertEquals("this card already attacked\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.NO_CARD_TO_ATTACK, 0, null);
        Assertions.assertEquals("there is no card to attack here\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.ATTACK_PREVENTED, 0, null);
        Assertions.assertEquals("attack prevented\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.OPPONENT_ATTACK_POSITION_MONSTER_DESTROYED, 7355, null);
        Assertions.assertEquals("your opponent’s monster is destroyed and your opponent receives 7355 battle damage\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.BOTH_ATTACK_POSITION_MONSTERS_DESTROYED, 0, null);
        Assertions.assertEquals("both you and your opponent monster cards are destroyed and no one receives damage\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.YOUR_ATTACK_POSITION_MONSTER_DESTROYED, 7355, null);
        Assertions.assertEquals("Your monster card is destroyed and you received 7355 battle damage\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.OPPONENT_DEFENSE_POSITION_MONSTER_DESTROYED, 0, null);
        Assertions.assertEquals("the defense position monster is destroyed\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.NO_CARD_DESTROYED_AND_NO_DAMAGE, 0, null);
        Assertions.assertEquals("no card is destroyed\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.NO_CARD_DESTROYED_WITH_DAMAGE, 7355, null);
        Assertions.assertEquals("no card is destroyed and you received 7355 battle damage\r\n", outContent.toString());
        outContent.reset();

        view.printAttackMessage(DuelMenuMessage.UNEXPECTED_ERROR, 0, null);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printActivateEffectMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printActivateEffectMessage(DuelMenuMessage.NO_CARD_IS_SELECTED);
        Assertions.assertEquals("no card is selected yet\r\n", outContent.toString());
        outContent.reset();

        view.printActivateEffectMessage(DuelMenuMessage.ONLY_FOR_SPELLS);
        Assertions.assertEquals("activate effect is only for spell cards\r\n", outContent.toString());
        outContent.reset();

        view.printActivateEffectMessage(DuelMenuMessage.CANT_ACTIVATE_EFFECT);
        Assertions.assertEquals("you can’t activate an effect on this turn\r\n", outContent.toString());
        outContent.reset();

        view.printActivateEffectMessage(DuelMenuMessage.ACTION_NOT_ALLOWED);
        Assertions.assertEquals("you can’t do this action in this phase\r\n", outContent.toString());
        outContent.reset();

        view.printActivateEffectMessage(DuelMenuMessage.CARD_ALREADY_ACTIVATED);
        Assertions.assertEquals("you have already activated this card\r\n", outContent.toString());
        outContent.reset();

        view.printActivateEffectMessage(DuelMenuMessage.SPELL_ZONE_FULL);
        Assertions.assertEquals("spell card zone is full\r\n", outContent.toString());
        outContent.reset();

        view.printActivateEffectMessage(DuelMenuMessage.PREPARATIONS_NOT_DONE_YET);
        Assertions.assertEquals("preparations of this spell are not done yet\r\n", outContent.toString());
        outContent.reset();

        view.printActivateEffectMessage(DuelMenuMessage.SPELL_TRAP_ACTIVATED);
        Assertions.assertEquals("spell/trap activated!\r\n", outContent.toString());
        outContent.reset();

        view.printActivateEffectMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printCancelMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printCancelMessage(DuelMenuMessage.ACTION_CANCELED);
        Assertions.assertEquals("action canceled\r\n", outContent.toString());
        outContent.reset();

        view.printCancelMessage(DuelMenuMessage.NOTHING_TO_CANCEL);
        Assertions.assertEquals("there is nothing to cancel\r\n", outContent.toString());
        outContent.reset();

        view.printCancelMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void showCurrentMenuTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.showCurrentMenu();
        Assertions.assertEquals("Duel Menu\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void showFlipCoinResultTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.showFlipCoinResult("starter", CoinSide.HEADS);
        Assertions.assertEquals("coin side was heads and starter starts duel\r\n", outContent.toString());
        outContent.reset();

        view.showFlipCoinResult("starter", CoinSide.TAILS);
        Assertions.assertEquals("coin side was tails and starter starts duel\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void showTurnTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.showTurn("nickname");
        Assertions.assertEquals("its nickname's turn\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void showQuickTurnTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.showQuickTurn("nickname");
        Assertions.assertEquals("now it will be nickname's turn\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void showPhaseTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.showPhase("phase name");
        Assertions.assertEquals("phase: phase name\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void showDrawMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));
        DataManager dataManager = DataManager.getInstance();
        Monster monster = new Monster((MonsterTemplate) dataManager.getCardTemplateByName("Battle OX"));

        view.showDrawMessage(monster);
        Assertions.assertEquals("you drew \"Battle OX\" from your deck\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printWinnerMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printWinnerMessage(false, "winner", 5, 2);
        Assertions.assertEquals("winner won the game with score: 5-2\r\n", outContent.toString());
        outContent.reset();

        view.printWinnerMessage(true, "winner", 5, 2);
        Assertions.assertEquals("winner won the whole match with score: 5-2\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void printActionCanceledTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.printActionCanceled();
        Assertions.assertEquals("action canceled\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void showHelpTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.showHelp();
        Assertions.assertEquals("commands:\r\n" +
                "\tnext phase\r\n" +
                "\tselect <card address>\r\n" +
                "\tselect -d\r\n" +
                "\tsummon\r\n" +
                "\tset\r\n" +
                "\tset --position attack/defense\r\n" +
                "\tflip-summon\r\n" +
                "\tattack <number>\r\n" +
                "\tattack direct\r\n" +
                "\tactivate effect\r\n" +
                "\tshow graveyard\r\n" +
                "\tsurrender\r\n" +
                "\tcancel\r\n" +
                "\tcard show --selected\r\n" +
                "\tcard show <card name>\r\n" +
                "\tmenu show-current\r\n" +
                "\tmenu exit\r\n" +
                "\tmenu help\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void showLPIncreasedTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.showLPIncreased();
        Assertions.assertEquals("LP increased!\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void showSetWinnerMessageTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        view.showSetWinnerMessage(DuelMenuMessage.INVALID_NICKNAME);
        Assertions.assertEquals("invalid nickname\r\n", outContent.toString());
        outContent.reset();

        view.showSetWinnerMessage(DuelMenuMessage.WINNER_SET);
        Assertions.assertEquals("winner set successfully!\r\n", outContent.toString());
        outContent.reset();

        view.showSetWinnerMessage(DuelMenuMessage.UNEXPECTED_ERROR);
        Assertions.assertEquals("unexpected error\r\n", outContent.toString());
        outContent.reset();
    }

    @Test
    public void showHandTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));
        DataManager dataManager = DataManager.getInstance();

        ArrayList<Card> cards = new ArrayList<>();
        for (CardTemplate cardTemplate : dataManager.getCardTemplates()) {
            if (cardTemplate instanceof MonsterTemplate) {
                cards.add(new Monster((MonsterTemplate) cardTemplate));
            } else if (cardTemplate instanceof SpellTemplate) {
                cards.add(new Spell((SpellTemplate) cardTemplate));
            } else if (cardTemplate instanceof TrapTemplate) {
                cards.add(new Trap((TrapTemplate) cardTemplate));
            }
        }

        StringBuilder expectedOutput = new StringBuilder();
        expectedOutput.append("Hand\r\n");
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            expectedOutput.append(i + 1).append(". ").append(card.toString()).append("\r\n");
        }

        view.showHand(cards);
        Assertions.assertEquals(expectedOutput.toString(), outContent.toString());
        outContent.reset();
    }

    @Test
    public void getNumbersTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> outputs = new ArrayList<>();

        outputs.add("message1");
        commands.add("cancel");

        outputs.add("message2");
        commands.add("1");
        commands.add("he11o");
        outputs.add("please enter a number");
        commands.add("cancel");

        outputs.add("message3");
        commands.add("1");
        commands.add("he11o");
        outputs.add("please enter a number");
        commands.add("4");
        commands.add("94");


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

        ArrayList<Integer> numbers;
        numbers = view.getNumbers(2, "message1");
        Assertions.assertNull(numbers);
        numbers = view.getNumbers(2, "message2");
        Assertions.assertNull(numbers);
        numbers = view.getNumbers(3, "message3");
        Assertions.assertEquals(1, numbers.get(0));
        Assertions.assertEquals(4, numbers.get(1));
        Assertions.assertEquals(94, numbers.get(2));

        Assertions.assertEquals(outputsStringBuilder.toString(), outContent.toString());
        System.setIn(stdIn);
    }

    @Test
    public void getOneOfValuesTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));

        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> outputs = new ArrayList<>();

        outputs.add("message1");
        commands.add("cancel");

        outputs.add("message2");
        commands.add("value");
        outputs.add("invalid message2");
        commands.add("cancel");

        outputs.add("message3");
        commands.add("value");
        outputs.add("invalid message3");
        commands.add("second");


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

        String value;
        value = view.getOneOfValues("first", "second", "message1", "invalid message1");
        Assertions.assertNull(value);
        value = view.getOneOfValues("first", "second", "message2", "invalid message2");
        Assertions.assertNull(value);
        value = view.getOneOfValues("first", "second", "message3", "invalid message3");
        Assertions.assertEquals("second", value);

        Assertions.assertEquals(outputsStringBuilder.toString(), outContent.toString());
        System.setIn(stdIn);
    }

    @Test
    public void getAddressTest() {
        DuelMenuView view = new DuelMenuView(new DuelMenuController(new User("", "", ""), new User("", "", ""), 1));
        CardAddress address;

        address = view.getAddress("select -m 4".split("\\s"));
        Assertions.assertEquals(CardAddressZone.MONSTER, address.getZone());
        Assertions.assertEquals(4, address.getPosition());
        Assertions.assertFalse(address.isForOpponent());

        address = view.getAddress("select -m 4 -o --hand".split("\\s"));
        Assertions.assertNull(address);

        address = view.getAddress("select -m 4 --hand 5".split("\\s"));
        Assertions.assertNull(address);

        address = view.getAddress("select --spell 4 -o".split("\\s"));
        Assertions.assertEquals(CardAddressZone.SPELL, address.getZone());
        Assertions.assertEquals(4, address.getPosition());
        Assertions.assertTrue(address.isForOpponent());

        address = view.getAddress("select --hand 10 --opponent".split("\\s"));
        Assertions.assertEquals(CardAddressZone.HAND, address.getZone());
        Assertions.assertEquals(10, address.getPosition());
        Assertions.assertTrue(address.isForOpponent());

        address = view.getAddress("select --field".split("\\s"));
        Assertions.assertEquals(CardAddressZone.FIELD, address.getZone());
        Assertions.assertEquals(0, address.getPosition());
        Assertions.assertFalse(address.isForOpponent());

        address = view.getAddress("select -g 14 --opponent".split("\\s"));
        Assertions.assertEquals(CardAddressZone.GRAVEYARD, address.getZone());
        Assertions.assertEquals(14, address.getPosition());
        Assertions.assertTrue(address.isForOpponent());
    }
}
